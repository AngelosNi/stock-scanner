package gr.trading.scanner.ta;

import gr.trading.scanner.model.OhlcPlusBar;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.ATRIndicator;

import java.time.ZoneId;
import java.util.List;

@Component
public class TaTools {

    public static final int ATR_LENGTH = 10;

    public double calculateAtr(List<OhlcPlusBar> bars) {
        BarSeries series = new BaseBarSeriesBuilder().build();
        bars.forEach(bar -> series.addBar(bar.getTime().atZone(ZoneId.of("America/New_York")), bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose()));

        return new ATRIndicator(series, ATR_LENGTH).getValue(series.getBarCount() - 1).doubleValue();
    }
}
