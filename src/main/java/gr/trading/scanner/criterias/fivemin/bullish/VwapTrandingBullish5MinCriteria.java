package gr.trading.scanner.criterias.fivemin.bullish;

import gr.trading.scanner.criterias.fivemin.OhlcPlus5MinBarCriteria;
import gr.trading.scanner.criterias.fivemin.VwapCriteria;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.ta.TaTools;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("BullishCriteria")
public class VwapTrandingBullish5MinCriteria implements OhlcPlus5MinBarCriteria {

    private final VwapCriteria vwapCriteria;

    public VwapTrandingBullish5MinCriteria(TaTools taTools) {
        this.vwapCriteria = new VwapCriteria(taTools, (bar, vwap) -> bar.getClose() > vwap);
    }

    @Override
    public boolean apply(List<OhlcPlusBar> d1Bars, List<OhlcPlusBar> min5Bars) throws NoRecentDataException {
        return vwapCriteria.apply(d1Bars, min5Bars);
    }

}
