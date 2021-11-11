package org.apache.camel.example;

import java.util.HashMap;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WireMockTestResource implements QuarkusTestResourceLifecycleManager {

    private WireMockServer server;

    @Override
    public Map<String, String> start() {
        // Setup & start the server
        server = new WireMockServer(wireMockConfig().dynamicPort());
        server.start();

        // create mock endpoint
        server.stubFor(post(urlEqualTo("/camel/subscriber/details")).willReturn(aResponse()
                .withHeader("Content-Type", "application/xml").withStatus(200).withBodyFile("individual.xml")));

        // obtain value as Camel property expects
        String host = server.baseUrl().substring(server.baseUrl().lastIndexOf("http://") + 7);

        // Ensure the camel component API client passes requests through the WireMock proxy
        Map<String, String> conf = new HashMap<>();
        conf.put("api.backend1.host", host);
        return conf;
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }
}
