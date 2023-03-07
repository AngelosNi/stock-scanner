package gr.trading.scanner.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("twelve-data")
@Data
public class TwelveDataConfigProperties {

    private String baseUrl;
}
