package gr.trading.scanner.criterias.daily.bearish;

import gr.trading.scanner.criterias.daily.HADailyBarsTrendingCriteria;
import gr.trading.scanner.criterias.daily.OhlcPlusDailyBarCriteria;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.ta.TaTools;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("BearishCriteria")
public class HADailyBarsTrendingBearishCriteria implements OhlcPlusDailyBarCriteria {

    private final HADailyBarsTrendingCriteria haDailyBarsTrendingCriteria;

    public HADailyBarsTrendingBearishCriteria(TaTools taTools) {
        this.haDailyBarsTrendingCriteria = new HADailyBarsTrendingCriteria(taTools, bar -> bar.getClose() < bar.getOpen());
    }

    @Override
    public boolean apply(List<OhlcPlusBar> d1Bars) {
        return this.haDailyBarsTrendingCriteria.apply(d1Bars);
    }
}
