package com.sm.lt.infrastructure.services.credit_session_manager.enc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Slf4j
public class AttributeEncryption {
    private static boolean needToEncryptAttribute(String key) {
        return key.startsWith("SAML:")
                && !key.startsWith("SAML:partner")
                && !key.equals("SAML:FI_ID")
                && !key.equals("SAML:USER_ID");
    }

    public static void encryptAttributes(Map<String, String> attributes, String encryptKey) {
        if (attributes != null) {
            for (String key : attributes.keySet()) {
                String value = attributes.get(key);
                if (value != null && !value.isEmpty()) {
                    try {
                        if (needToEncryptAttribute(key))
                            attributes.put(key, AESEncryptionUtils.encryptTextToHex(value, encryptKey));
                    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                            | IllegalBlockSizeException | BadPaddingException | DecoderException e) {
                        log.error("Error occurred", e);
                    }
                }
            }
        }
    }

    public static void decryptAttributes(Map<String, String> attributes, String encryptKey) {
        if (attributes != null) {
            for (String key : attributes.keySet()) {
                String value = attributes.get(key);
                if (value != null && !value.isEmpty()) {
                    try {
                        if (needToEncryptAttribute(key))
                            attributes.put(key, AESEncryptionUtils.decryptHexText(value, encryptKey));
                    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                            | IllegalBlockSizeException | BadPaddingException | DecoderException e) {
                        log.error("Error occurred", e);
                    }
                }
            }
        }
    }
}
