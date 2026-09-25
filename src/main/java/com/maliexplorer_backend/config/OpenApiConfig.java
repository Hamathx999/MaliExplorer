package com.maliexplorer_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI maliExplorerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MaliExplorer API REST")
                        .description("API REST pour la plateforme de découverte, valorisation et partenariats du Mali.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Équipe MaliExplorer")
                                .email("contact@maliexplorer.ml"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
