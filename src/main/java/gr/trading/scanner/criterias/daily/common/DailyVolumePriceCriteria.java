package gr.trading.scanner.criterias.daily.common;

import gr.trading.scanner.criterias.daily.OhlcPlusDailyBarCriteria;
import gr.trading.scanner.model.OhlcPlusBar;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("CommonCriteria")
public class DailyVolumePriceCriteria implements OhlcPlusDailyBarCriteria {

    private final double AVERAGE_THRESHOLD = 1; // in mils

    @Override
    public boolean apply(List<OhlcPlusBar> d1Bars) {
        return d1Bars.stream()
                .map(bar -> bar.getClose() * bar.getVolume().doubleValue())
                .mapToDouble(Double::doubleValue)
                .summaryStatistics()
                .getAverage() > AVERAGE_THRESHOLD * 1000000;
    }
}
