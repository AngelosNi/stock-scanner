package gr.trading.scanner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TickerInfo {

    @JsonProperty("Symbol")
    private String symbol;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Last Sale")
    private String lastSale;

    @JsonProperty("Net Change")
    private String netChange;

    @JsonProperty("% Change")
    private String percentChange;

    @JsonProperty("Market Cap")
    private Double marketCap;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("IPO Year")
    private int ipoYear;

    @JsonProperty("Volume")
    private Double volume;

    @JsonProperty("Sector")
    private String sector;

    @JsonProperty("Industry")
    private String industry;
}
