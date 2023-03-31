package gr.trading.scanner.ta;

import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TaTools {

    private static final int ATR_LENGTH = 10;

    private final DateTimeUtils dateTimeUtils;

    public <T extends OhlcBar> double calculateAtr(List<T> bars) {
        BarSeries series = convertToBarSeries(bars);

        return new ATRIndicator(series, ATR_LENGTH).getValue(series.getEndIndex()).doubleValue();
    }

    public <T extends OhlcBar> double calculateSma(List<T> bars, int period) {
        BarSeries series = convertToBarSeries(bars);

        return new SMAIndicator(new ClosePriceIndicator(series), period).getValue(series.getEndIndex()).doubleValue();
    }

    public <T extends OhlcBar> double calculateVwap(List<T> bars) {
        double num = 0;
        double denom = 0;

        List<T> lastDayBars = bars.stream()
                .filter(b -> b.getTime().toLocalDate().isEqual(dateTimeUtils.getNowDayAtSessionStart().toLocalDate()))
                .collect(Collectors.toList());
        for (T bar : lastDayBars) {
            num += (bar.getHigh() + bar.getLow() + bar.getClose()) / 3 * bar.getVolume();
            denom += bar.getVolume();
        }
        return num / denom;
    }

    public <T extends OhlcBar> List<T> convertToHeikinAshi(List<T> ohlcBars) {
        List<T> haBars = new ArrayList<>();
        T haBar = (T) new OhlcBar();
        double haOpen = (ohlcBars.get(0).getOpen() + ohlcBars.get(0).getClose()) / 2;
        double haClose = (ohlcBars.get(0).getOpen() + ohlcBars.get(0).getHigh() + ohlcBars.get(0).getLow() + ohlcBars.get(0).getClose()) / 4;
        haBar.setTime(ohlcBars.get(0).getTime());
        haBar.setOpen(haOpen);
        haBar.setHigh(haOpen);
        haBar.setLow(haOpen);
        haBar.setClose(haClose);
        haBar.setVolume(ohlcBars.get(0).getVolume());

        for (int i = 1; i < ohlcBars.size(); i++) {
            OhlcBar ob = ohlcBars.get(i);
            double haOpenPrev = haBar.getOpen();
            double haClosePrev = haBar.getClose();
            haOpen = (haOpenPrev + haClosePrev) / 2;
            haClose = (ob.getOpen() + ob.getHigh() + ob.getLow() + ob.getClose()) / 4;
            double haHigh = Math.max(Math.max(ob.getHigh(), haOpen), haClose);
            double haLow = Math.min(Math.min(ob.getLow(), haOpen), haClose);
            haBar = (T) new OhlcBar();
            haBar.setTime(ob.getTime());
            haBar.setOpen(haOpen);
            haBar.setHigh(haHigh);
            haBar.setLow(haLow);
            haBar.setClose(haClose);
            haBar.setVolume(ob.getVolume());
            haBars.add(haBar);
        }

        return haBars;
    }

    private <T extends OhlcBar> BarSeries convertToBarSeries(List<T> bars) {
        BarSeries series = new BaseBarSeriesBuilder().build();
        bars.forEach(bar -> series.addBar(bar.getTime().atZone(ZoneId.of("America/New_York")), bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose()));

        return series;
    }
}
