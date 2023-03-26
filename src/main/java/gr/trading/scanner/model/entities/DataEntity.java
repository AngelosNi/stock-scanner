package gr.trading.scanner.model.entities;

import gr.trading.scanner.model.Interval;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

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

    private double volume;

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Id implements Serializable {

        private String symbol;

        private LocalDateTime barDateTime;

        @Enumerated(EnumType.STRING)
        private Interval barInterval;
    }
}
