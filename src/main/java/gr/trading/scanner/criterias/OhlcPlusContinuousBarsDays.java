package gr.trading.scanner.criterias;

import gr.trading.scanner.model.OhlcPlusBar;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Math.max;

@Component
public class OhlcPlusContinuousBarsDays implements OhlcPlus1DayBarCriteria {

    private static final int LOOK_BEHIND_PERIOD = 3;

    @Override
    public boolean apply(List<OhlcPlusBar> d1Bars) {
        boolean isBullish = true;
        boolean isBearish = true;

        for (int i = d1Bars.size() - 1; i > max(d1Bars.size() - 1 - LOOK_BEHIND_PERIOD, 0) ; i--) {
            if (d1Bars.get(i).getClose() < d1Bars.get(i).getOpen()) {
                isBullish = false;
            } else {
                isBearish = false;
            }
        }

        return isBullish || isBearish;
    }
}
