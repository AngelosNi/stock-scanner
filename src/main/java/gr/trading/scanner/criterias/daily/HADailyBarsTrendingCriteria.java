package gr.trading.scanner.criterias.daily;

import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.ta.TaTools;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Math.max;

@AllArgsConstructor
public class HADailyBarsTrendingCriteria implements OhlcPlusDailyBarCriteria {

    private static final int LOOK_BEHIND_PERIOD = 3;

    private final TaTools taTools;

    private final Predicate<OhlcBar> isTrendingCriteria;

    public boolean apply(List<OhlcPlusBar> d1Bars) {
        boolean isTrending = true;

        List<OhlcBar> bars = d1Bars.stream()
                .map(b -> (OhlcBar) b)
                .collect(Collectors.toList());

        List<OhlcBar> haBars = taTools.convertToHeikinAshi(bars);

        for (int i = haBars.size() - 1; i > max(haBars.size() - 1 - LOOK_BEHIND_PERIOD, 0) ; i--) {
            if (!isTrendingCriteria.test(haBars.get(i))) {
                isTrending = false;
            }
        }

        return isTrending;
    }
}
