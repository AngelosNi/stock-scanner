package gr.trading.scanner.criterias.fivemin;

import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.OhlcPlusBar;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@AllArgsConstructor
public class OverPreviousDaysEdgesCriteria implements OhlcPlus5MinBarCriteria {

    private final BiPredicate<OhlcBar, OhlcBar> isOverPreviousDaysEdges;

    @Override
    public boolean apply(List<OhlcPlusBar> d1Bars, List<OhlcPlusBar> min5Bars) throws NoRecentDataException {
        List<OhlcBar> bars = min5Bars.stream()
                .map(b -> (OhlcBar) b)
                .collect(Collectors.toList());

        return isOverPreviousDaysEdges.test(bars.get(bars.size() - 1), d1Bars.get(d1Bars.size() - 1));
    }
}
