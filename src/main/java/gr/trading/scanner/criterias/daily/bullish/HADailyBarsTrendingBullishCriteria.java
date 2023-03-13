package gr.trading.scanner.criterias.daily.bullish;

import gr.trading.scanner.criterias.daily.HADailyBarsTrendingCriteria;
import gr.trading.scanner.criterias.daily.OhlcPlusDailyBarCriteria;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.ta.TaTools;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("BullishCriteria")
public class HADailyBarsTrendingBullishCriteria implements OhlcPlusDailyBarCriteria {

    private static final int LOOK_BEHIND_PERIOD = 3;

    private final TaTools taTools;

    private final HADailyBarsTrendingCriteria haDailyBarsTrendingCriteria;

    public HADailyBarsTrendingBullishCriteria(TaTools taTools) {
        this.taTools = taTools;
        this.haDailyBarsTrendingCriteria = new HADailyBarsTrendingCriteria(taTools, bar -> bar.getClose() > bar.getOpen());
    }

    @Override
    public boolean apply(List<OhlcPlusBar> d1Bars) {
        return this.haDailyBarsTrendingCriteria.apply(d1Bars);
    }
}
