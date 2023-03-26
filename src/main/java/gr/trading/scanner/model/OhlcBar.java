package gr.trading.scanner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OhlcBar implements Serializable {

    private LocalDateTime time;

    private Double open;

    private Double high;

    private Double low;

    private Double close;

    private Double volume;
}
