package gr.trading.scanner.criterias.fivemin;

import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.ta.TaTools;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@AllArgsConstructor
public class VwapCriteria implements OhlcPlus5MinBarCriteria {

    private final TaTools taTools;

    private final BiPredicate<OhlcBar, Double> isTrendingCriteria;

    @Override
    public boolean apply(List<OhlcPlusBar> d1Bars, List<OhlcPlusBar> min5Bars) throws NoRecentDataException {
        List<OhlcBar> bars = min5Bars.stream()
                .map(b -> (OhlcBar) b)
                .collect(Collectors.toList());

        Double vwap = taTools.calculateVwap(bars);

        return isTrendingCriteria.test(bars.get(bars.size() - 1), vwap);
    }
}
