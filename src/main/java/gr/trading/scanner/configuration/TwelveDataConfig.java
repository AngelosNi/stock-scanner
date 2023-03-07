package gr.trading.scanner.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TwelveDataConfig {

    @Bean
    public WebClient twelveDataWebClient(TwelveDataConfigProperties properties) {
        return WebClient.builder().baseUrl(properties.getBaseUrl())
                .defaultHeader("Authorization", "apikey 3aeaf8f2485f4d9aa20702f83228a332")
                .build();
    }
}
