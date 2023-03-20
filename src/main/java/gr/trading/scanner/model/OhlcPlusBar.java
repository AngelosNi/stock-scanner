package gr.trading.scanner.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OhlcPlusBar extends OhlcBar implements Cloneable {

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

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
