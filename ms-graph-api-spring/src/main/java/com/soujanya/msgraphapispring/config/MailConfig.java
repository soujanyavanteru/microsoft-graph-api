package com.soujanya.msgraphapispring.config;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MailConfig {

    @Value("${spring.security.oauth2.client.registration.azure.client-id}")
    private String clientId;

    @Value("${microsoft.azure.tenant-id}")
    private String tenantId;

    @Value("${spring.security.oauth2.client.registration.azure.client-secret}")
    private String clientSecret;

    @Bean
    public GraphServiceClient mailClient() {
        ClientSecretCredential credential= new ClientSecretCredentialBuilder().clientId(clientId)
                .tenantId(tenantId)
                .clientSecret(clientSecret).build();
        return new GraphServiceClient(credential, "https://graph.microsoft.com/.default");
    }

}
