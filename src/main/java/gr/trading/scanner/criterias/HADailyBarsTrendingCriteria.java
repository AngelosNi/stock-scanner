package gr.trading.scanner.criterias;

import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.ta.TaTools;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.max;

@Component
@AllArgsConstructor
public class HADailyBarsTrendingCriteria implements OhlcPlusDailyBarCriteria {

    private static final int LOOK_BEHIND_PERIOD = 3;

    private final TaTools taTools;

    @Override
    public boolean apply(List<OhlcPlusBar> d1Bars) {
        boolean isBullish = true;
        boolean isBearish = true;

        List<OhlcBar> bars = d1Bars.stream()
                .map(b -> (OhlcBar) b)
                .collect(Collectors.toList());

        List<OhlcBar> haBars = taTools.convertToHeikinAshi(bars);

        for (int i = haBars.size() - 1; i > max(haBars.size() - 1 - LOOK_BEHIND_PERIOD, 0) ; i--) {
            if (haBars.get(i).getClose() < haBars.get(i).getOpen()) {
                isBullish = false;
            } else {
                isBearish = false;
            }
        }

        return isBullish || isBearish;
    }
}
