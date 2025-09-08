package com.example.productservice.config;

import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Configuration
public class AzureSearchConfig {
    
    @Value("${azure.search.endpoint:}")
    private String searchEndpoint;
    
    @Value("${azure.search.api-key:}")
    private String searchApiKey;
    
    @Value("${azure.search.index-name:products}")
    private String indexName;
    
    @Bean
    @Conditional(AzureSearchPropertiesPresent.class)
    public SearchClient searchClient() {
        if (searchEndpoint == null || searchEndpoint.isEmpty() || 
            searchApiKey == null || searchApiKey.isEmpty()) {
            return null;
        }
        
        return new SearchClientBuilder()
                .endpoint(searchEndpoint)
                .credential(new AzureKeyCredential(searchApiKey))
                .indexName(indexName)
                .buildClient();
    }
    
    /**
     * Condition to check if Azure Search properties are present
     */
    public static class AzureSearchPropertiesPresent implements org.springframework.context.annotation.Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String endpoint = context.getEnvironment().getProperty("azure.search.endpoint");
            String apiKey = context.getEnvironment().getProperty("azure.search.api-key");
            return endpoint != null && !endpoint.isEmpty() && 
                   apiKey != null && !apiKey.isEmpty();
        }
    }
}
