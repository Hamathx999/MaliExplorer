package com.maliexplorer_backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@Setter
@Configuration
public class SupabaseConfig {

    @Value("${supabase.url:https://dzhqwkpwaljqsjwoqvso.supabase.co}")
    private String supabaseUrl;

    @Value("${supabase.key:sb_publishable_7yi5d-qBrG9OnmKavJZ39Q_0OGGAciz}")
    private String supabaseKey;

    @Value("${supabase.bucket.default:maliexplorer-media}")
    private String defaultBucket;

    @Bean
    public WebClient supabaseWebClient() {
        // Supporte les fichiers volumineux (ex: panoramas 360° jusqu'à 50 Mo)
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(50 * 1024 * 1024))
                .build();

        return WebClient.builder()
                .baseUrl(supabaseUrl)
                .exchangeStrategies(strategies)
                .defaultHeader("apikey", supabaseKey)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseKey)
                .build();
    }
}
