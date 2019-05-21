package com.sm.lt.infrastructure.services.credit_session_manager;

import java.util.Map;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.ImmutableSet;
import com.sm.lt.infrastructure.HttpUtils;
import com.sm.lt.infrastructure.JsonUtils;
import com.sm.lt.infrastructure.services.credit_session_manager.dto.BooleanDTO;
import com.sm.lt.infrastructure.services.credit_session_manager.dto.SessionTokenDTO;
import com.sm.lt.infrastructure.services.credit_session_manager.enc.AESEncryptionUtils;
import com.sm.lt.infrastructure.services.credit_session_manager.enc.AttributeEncryption;

@Slf4j
@RequiredArgsConstructor
public class CreditSessionManagerClient {

    private final String baseUrl;
    private static final CloseableHttpClient CLIENT = HttpUtils.defaultClient();

    public String saveAttributes(Map<String, String> map) {
        final String encrptKey = AESEncryptionUtils.getSecretEncryptionKeyHex(128);
        AttributeEncryption.encryptAttributes(map, encrptKey);

        final HttpPost request = new HttpPost(baseUrl + "/attributes");
        request.setEntity(EntityBuilder.create()
                                       .setContentType(ContentType.APPLICATION_JSON)
                                       .setText(JsonUtils.toJson(map))
                                       .build());
        final Set<Integer> statuses = ImmutableSet.of(HttpStatus.SC_OK);
        final SessionTokenDTO token = HttpUtils.makeRequestReturnResult(CLIENT, request, statuses, SessionTokenDTO.class);

        if (token.getError() != null) {
            log.error("Response contains error. {}", JsonUtils.toJson(token));
            return null;
        }
        return token.getToken() + "-" + encrptKey;
    }

    public boolean updateAttributes(String smToken, Map<String, String> map) {
        final String encrptKey = getEncryptKey(smToken);
        AttributeEncryption.encryptAttributes(map, encrptKey);

        final String sessionToken = getSessionToken(smToken);
        final HttpPost request = new HttpPost(baseUrl + "/" + sessionToken + "/attributes");
        request.setEntity(EntityBuilder.create()
                                       .setContentType(ContentType.APPLICATION_JSON)
                                       .setText(JsonUtils.toJson(map))
                                       .build());
        final Set<Integer> statuses = ImmutableSet.of(HttpStatus.SC_OK);
        final BooleanDTO b = HttpUtils.makeRequestReturnResult(CLIENT, request, statuses, BooleanDTO.class);
        if (b.getError() != null) {
            log.error("Response contains error. {}", JsonUtils.toJson(b));
            return false;
        }
        return b.isValue();
    }

    public boolean removeToken(String smToken) {
        final String sessionToken = getSessionToken(smToken);
        final HttpDelete request = new HttpDelete(baseUrl + "/" + sessionToken);
        HttpUtils.makeRequest(CLIENT, request);
        return true;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getAttributes(String smToken) {
        final String sessionToken = getSessionToken(smToken);
        final HttpGet request = new HttpGet(baseUrl + "/" + sessionToken + "/attributes");
        final Set<Integer> statuses = ImmutableSet.of(HttpStatus.SC_OK);
        final Map<String, String> map = HttpUtils.makeRequestReturnResult(CLIENT, request, statuses, Map.class);

        final String encrptKey = getEncryptKey(smToken);
        AttributeEncryption.decryptAttributes(map, encrptKey);
        return map;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getAttributes(String smToken, String prefix) {
        final String sessionToken = getSessionToken(smToken);
        final HttpGet request = new HttpGet(baseUrl + "/" + sessionToken + "/attributes/prefix/" + prefix);
        final Set<Integer> statuses = ImmutableSet.of(HttpStatus.SC_OK);
        final Map<String, String> map = HttpUtils.makeRequestReturnResult(CLIENT, request, statuses, Map.class);

        final String encrptKey = getEncryptKey(smToken);
        AttributeEncryption.decryptAttributes(map, encrptKey);
        return map;
    }

    public Boolean prolongToken(String smToken) {
        final String sessionToken = getSessionToken(smToken);
        final HttpPost request = new HttpPost(baseUrl + "/" + sessionToken + "/prolong");
        final Set<Integer> statuses = ImmutableSet.of(HttpStatus.SC_OK);
        final BooleanDTO b = HttpUtils.makeRequestReturnResult(CLIENT, request, statuses, BooleanDTO.class);
        if (b.getError() != null) {
            log.error("Response contains error. {}", JsonUtils.toJson(b));
            return false;
        }
        return b.isValue();
    }

    public String cloneToken(String smToken) {
        final String sessionToken = getSessionToken(smToken);
        final HttpPost request = new HttpPost(baseUrl + "/" + sessionToken + "/clone");
        final Set<Integer> statuses = ImmutableSet.of(HttpStatus.SC_OK);
        final SessionTokenDTO cloneToken = HttpUtils.makeRequestReturnResult(CLIENT, request, statuses, SessionTokenDTO.class);
        if (cloneToken.getError() != null) {
            log.error("Response contains error. {}", JsonUtils.toJson(smToken));
            return null;
        }
        return cloneToken.getToken() + "-" + getEncryptKey(smToken);
    }

    private static String getSessionToken(String smToken) {
        return smToken.substring(0, getDashIndex(smToken));
    }

    private static String getEncryptKey(String smToken) {
        return smToken.substring(getDashIndex(smToken) + 1);
    }

    private static int getDashIndex(String smToken) {
        int index = smToken.lastIndexOf("-");
        if (index < 0) {
            log.error("Wrong token format: {}", smToken);
            throw new IllegalArgumentException("Wrong token format " + smToken);
        }
        return index;
    }
}
