package gr.trading.scanner.criterias.daily;

import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.ta.TaTools;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Sma200DailyBarsTrendingCriteria implements OhlcPlusDailyBarCriteria {

    private final TaTools taTools;

    private final BiPredicate<OhlcBar, Double> isTrendingCriteria;

    public boolean apply(List<OhlcPlusBar> d1Bars) {
        List<OhlcBar> bars = d1Bars.stream()
                .map(b -> (OhlcBar) b)
                .collect(Collectors.toList());

        Double sma200 = taTools.calculateSma(bars, 200);

        return isTrendingCriteria.test(bars.get(bars.size() - 1), sma200);
    }
}
