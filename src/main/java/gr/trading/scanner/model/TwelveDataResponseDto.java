package gr.trading.scanner.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class TwelveDataResponseDto {

    @JsonAnySetter
    private Map<String, String> meta;

    private List<Bar> values = new ArrayList<>();

    private String status;

    @Data
    public static class Bar {

        @JsonProperty("datetime")
        private String dateTime;

        private BigDecimal open;

        private BigDecimal high;

        private BigDecimal low;

        private BigDecimal close;

        private BigDecimal volume;
    }
}
