package gr.trading.scanner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OhlcBar implements Serializable {

    private LocalDateTime time;

    private double open;

    private double high;

    private double low;

    private double close;

    private BigDecimal volume;
}
