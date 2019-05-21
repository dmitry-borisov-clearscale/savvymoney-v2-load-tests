package com.sm.lt.infrastructure;

import java.io.IOException;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import lombok.extern.slf4j.Slf4j;

import com.google.common.io.ByteStreams;

@Slf4j
public class HttpUtils {

    public static CloseableHttpClient defaultClient() {
        return defaultClient(30_000);
    }

    public static CloseableHttpClient defaultClient(int socketTimeout) {
        final RequestConfig requestConfig = RequestConfig.custom()
                                                         .setConnectTimeout(5_000)
                                                         .setConnectionRequestTimeout(15_000)
                                                         .setSocketTimeout(socketTimeout)
                                                         .build();
        return HttpClientBuilder.create()
                                .setDefaultRequestConfig(requestConfig)
                                .disableCookieManagement()
                                .build();
    }

    public static void makeRequest(CloseableHttpClient client, HttpUriRequest request) {
        try (CloseableHttpResponse ignore = client.execute(request)) {
            // do nothing
        } catch (Exception e) {
            log.error("Error occurred", e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T makeRequestReturnResult(CloseableHttpClient client, HttpUriRequest request, Set<Integer> statuses, Class<T> clz) {
        try (CloseableHttpResponse res = client.execute(request)) {
            if (!statuses.isEmpty()) {
                checkStatusCode(request, res, statuses);
            }
            return JsonUtils.fromJson(res.getEntity().getContent(), clz);
        } catch (IOException e) {
            log.error("Error occurred. Request: {}. Statuses: {}. Clz: {}", request, statuses, clz, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T makeRequestReturnResult(CloseableHttpClient client,
                                                HttpUriRequest request,
                                                Set<Integer> statuses,
                                                ResponseParser<T> responseParser) {
        try (CloseableHttpResponse res = client.execute(request)) {
            if (!statuses.isEmpty()) {
                checkStatusCode(request, res, statuses);
            }
            return responseParser.parse(res);
        } catch (Exception e) {
            log.error("Error occurred. Request: {}. Statuses: {}", request, statuses, e);
            throw new RuntimeException(e);
        }
    }

    private static void checkStatusCode(HttpUriRequest request, CloseableHttpResponse response, Set<Integer> statuses) throws IOException {
        if (!statuses.contains(response.getStatusLine().getStatusCode())) {
            final byte[] bytes = ByteStreams.toByteArray(response.getEntity().getContent());
            final String content = new String(bytes, Charsets.UTF_8);
            log.error("Invalid status code. Method: {}. URI: {}. Expected: {}. Got: {}. Body: \n{}",
                    request.getMethod(), request.getURI(), statuses, response.getStatusLine(), content);
            throw new RuntimeException("Invalid status code");
        }
    }

    @FunctionalInterface
    public interface ResponseParser<T> {

        T parse(HttpResponse httpResponse) throws Exception;
    }
}