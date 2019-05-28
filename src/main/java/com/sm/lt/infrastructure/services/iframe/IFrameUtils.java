package com.sm.lt.infrastructure.services.iframe;

import static com.google.common.base.Strings.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Preconditions;

@Slf4j
@RequiredArgsConstructor
public class IFrameUtils {

    private final String baseUrl;

    public Builder builder() {
        return new Builder(baseUrl);
    }

    public enum Environment {
        LOCAL("local"),
        DEV("dev"),
        STAB("stab"),
        STAGING("staging"),
        STAGING_STAB("staging-stab"),
        SANDBOX("sandbox"),
        BETA("beta"),
        PREPROD("preprod"),
        PROD("prod");

        private final String str;

        Environment(String str) {
            this.str = str;
        }

        public static Environment fromString(String environment) {
            for (Environment e : values()) {
                if (e.str.equalsIgnoreCase(environment)) {
                    return e;
                }
            }
            throw new IllegalArgumentException("Invalid environment name: " + environment);
        }
    }

    public enum Endpoint {
        SSO("sso"),
        WIDGET_SSO("widget-sso");

        private final String str;

        Endpoint(String str) {
            this.str = str;
        }
    }

    public enum Protocol {
        HTTP("http"),
        HTTPS("https");

        private final String str;

        Protocol(String str) {
            this.str = str;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {
        private final String baseUrl;
        private final Map<String, String> parameters = new HashMap<>();

        public Builder environment(Environment environment) {
            Preconditions.checkNotNull(environment);
            parameters.remove("host");
            parameters.remove("protocol");
            parameters.put("host", environment.str);
            return this;
        }

        public Builder environment(Protocol protocol, String host) {
            Preconditions.checkNotNull(protocol);
            Preconditions.checkArgument(!isNullOrEmpty(host));
            parameters.put("host", host);
            parameters.put("protocol", protocol.str);
            return this;
        }

        public Builder endpoint(Endpoint endpoint) {
            Preconditions.checkNotNull(endpoint);
            parameters.put("endpoint", endpoint.str);
            return this;
        }

        public Builder width(String width) {
            Preconditions.checkArgument(!isNullOrEmpty(width));
            parameters.put("width", width);
            return this;
        }

        public Builder height(String height) {
            Preconditions.checkArgument(!isNullOrEmpty(height));
            parameters.put("height", height);
            return this;
        }

        public Builder left(String left) {
            Preconditions.checkArgument(!isNullOrEmpty(left));
            parameters.put("left", left);
            return this;
        }

        public Builder top(String top) {
            Preconditions.checkArgument(!isNullOrEmpty(top));
            parameters.put("top", top);
            return this;
        }

        public Builder smToken(String smToken) {
            Preconditions.checkArgument(!isNullOrEmpty(smToken));
            parameters.put("smtoken", smToken);
            return this;
        }

        public Builder link(String link) {
            Preconditions.checkArgument(!isNullOrEmpty(link) && !link.startsWith("/"));
            parameters.put("link", link);
            return this;
        }

        public URI build() {
            try {
                final URIBuilder uriBuilder = new URIBuilder(baseUrl);
                for (Map.Entry<String, String> e : parameters.entrySet()) {
                    uriBuilder.setParameter(e.getKey(), e.getValue());
                }
                return uriBuilder.build();
            } catch (URISyntaxException e) {
                log.error("Error occurred", e);
                throw new RuntimeException(e);
            }
        }
    }
}