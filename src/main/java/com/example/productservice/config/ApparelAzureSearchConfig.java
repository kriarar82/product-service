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
public class ApparelAzureSearchConfig {
    
    @Value("${azure.apparel.search.endpoint:}")
    private String apparelSearchEndpoint;
    
    @Value("${azure.apparel.search.api-key:}")
    private String apparelSearchApiKey;
    
    @Value("${azure.apparel.search.index-name:apparel-products}")
    private String apparelIndexName;
    
    @Bean("apparelSearchClient")
    @Conditional(ApparelAzureSearchPropertiesPresent.class)
    public SearchClient apparelSearchClient() {
        if (apparelSearchEndpoint == null || apparelSearchEndpoint.isEmpty() || 
            apparelSearchApiKey == null || apparelSearchApiKey.isEmpty()) {
            return null;
        }
        
        return new SearchClientBuilder()
                .endpoint(apparelSearchEndpoint)
                .credential(new AzureKeyCredential(apparelSearchApiKey))
                .indexName(apparelIndexName)
                .buildClient();
    }
    
    /**
     * Condition to check if Apparel Azure Search properties are present
     */
    public static class ApparelAzureSearchPropertiesPresent implements org.springframework.context.annotation.Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String endpoint = context.getEnvironment().getProperty("azure.apparel.search.endpoint");
            String apiKey = context.getEnvironment().getProperty("azure.apparel.search.api-key");
            return endpoint != null && !endpoint.isEmpty() && 
                   apiKey != null && !apiKey.isEmpty();
        }
    }
}
