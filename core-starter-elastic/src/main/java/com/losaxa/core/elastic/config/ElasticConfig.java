package com.losaxa.core.elastic.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.losaxa.core.common.JsonUtil;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.util.List;

@EnableConfigurationProperties(ElasticsearchRestClientProperties.class)
@Configuration
public class ElasticConfig {

    @Autowired
    ElasticsearchRestClientProperties properties;

    @Bean
    public RestClient restClient() {
        List<String> uris = properties.getUris();
        HttpHost[] httpHosts = uris.stream().map(e -> {
            String[] uri = e.split(":");
            return new HttpHost(uri[0], Integer.parseInt(uri[1]), "https");
        }).toArray(HttpHost[]::new);
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword()));
        RestClient restClient = null;
        try {
            final SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(
                    new File(System.getProperty("javax.net.ssl.trustStore")),
                    System.getProperty("javax.net.ssl.trustStorePassword").toCharArray(),
                    new TrustSelfSignedStrategy()).build();
            restClient = RestClient.builder(httpHosts)
                    .setHttpClientConfigCallback(httpAsyncClientBuilder ->
                            httpAsyncClientBuilder
                                    .setDefaultCredentialsProvider(credentialsProvider)
                                    .setSSLContext(sslContext))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return restClient;
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        ElasticsearchTransport transport           = new RestClientTransport(restClient, new JacksonJsonpMapper(JsonUtil.OM.copy()));
        ElasticsearchClient    elasticsearchClient = new ElasticsearchClient(transport);
        return elasticsearchClient;
    }
}
