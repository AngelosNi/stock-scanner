package gr.trading.scanner.criterias.fivemin.bullish;

import gr.trading.scanner.criterias.fivemin.OhlcPlus5MinBarCriteria;
import gr.trading.scanner.criterias.fivemin.OverPreviousDaysEdgesCriteria;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.ta.TaTools;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("BullishCriteria")
public class HigherThanPreviousDaysHigh5MinCriteria implements OhlcPlus5MinBarCriteria {

    private final OverPreviousDaysEdgesCriteria overPreviousDaysEdgesCriteria;

    public HigherThanPreviousDaysHigh5MinCriteria(TaTools taTools) {
        this.overPreviousDaysEdgesCriteria = new OverPreviousDaysEdgesCriteria((bar5Min, barDaily) -> bar5Min.getClose() > barDaily.getHigh());
    }

    @Override
    public boolean apply(List<OhlcPlusBar> d1Bars, List<OhlcPlusBar> min5Bars) throws NoRecentDataException {
        return overPreviousDaysEdgesCriteria.apply(d1Bars, min5Bars);
    }
}
