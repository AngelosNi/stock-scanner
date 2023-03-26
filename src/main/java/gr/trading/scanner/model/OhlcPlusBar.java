package gr.trading.scanner.model;

import lombok.Data;

@Data
public class OhlcPlusBar extends OhlcBar implements Cloneable {

    private Double raAverageVolume;

    private Double averageVolumeAcrossDays;

    private Double cumulativeVolume;

    private Double averageCumulativeVolumeAcrossDays;

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
