package gr.trading.scanner.model;

import lombok.Data;

@Data
public class OhlcPlusBar extends OhlcBar {

    private Double raAverageVolume;

    private Double daysAverageVolume;

    public OhlcPlusBar(OhlcBar ohlcBar) {
        this.setClose(ohlcBar.getClose());
        this.setHigh(ohlcBar.getHigh());
        this.setLow(ohlcBar.getLow());
        this.setOpen(ohlcBar.getOpen());
        this.setVolume(ohlcBar.getVolume());
        this.setTime(ohlcBar.getTime());
    }
}
