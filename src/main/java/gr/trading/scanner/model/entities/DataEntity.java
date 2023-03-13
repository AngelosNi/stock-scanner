package gr.trading.scanner.model.entities;

import gr.trading.scanner.model.Interval;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "STOCK_DATA")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DataEntity {

    @EmbeddedId
    private Id id;

    private double openPrice;

    private double closePrice;

    private double highPrice;

    private double lowPrice;

    private BigDecimal volume;

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Id implements Serializable {

        private String symbol;

        private LocalDate actionDate;

        private Interval barInterval;
    }
}
