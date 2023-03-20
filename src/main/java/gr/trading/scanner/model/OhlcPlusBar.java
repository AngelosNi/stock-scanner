package gr.trading.scanner.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OhlcPlusBar extends OhlcBar {

    private Double raAverageVolume;

    private Double averageVolumeAcrossDays;

    private BigDecimal cumulativeVolume;

    private BigDecimal averageCumulativeVolumeAcrossDays;

    public OhlcPlusBar(OhlcBar ohlcBar) {
        this.setClose(ohlcBar.getClose());
        this.setHigh(ohlcBar.getHigh());
        this.setLow(ohlcBar.getLow());
        this.setOpen(ohlcBar.getOpen());
        this.setVolume(ohlcBar.getVolume());
        this.setTime(ohlcBar.getTime());
    }
}
