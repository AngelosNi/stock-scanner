package gr.trading.scanner.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("y-finance")
@Data
public class YfinanceDataConfigProperties {

    private Double requestRate;
}
