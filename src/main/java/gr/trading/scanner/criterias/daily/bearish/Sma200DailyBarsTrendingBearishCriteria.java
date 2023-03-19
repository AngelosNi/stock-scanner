package gr.trading.scanner.criterias.daily.bearish;

import gr.trading.scanner.criterias.daily.OhlcPlusDailyBarCriteria;
import gr.trading.scanner.criterias.daily.Sma200DailyBarsTrendingCriteria;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.ta.TaTools;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("BearishCriteria")
public class Sma200DailyBarsTrendingBearishCriteria implements OhlcPlusDailyBarCriteria {

    private final Sma200DailyBarsTrendingCriteria sma200DailyBarsTrendingCriteria;

    public Sma200DailyBarsTrendingBearishCriteria(TaTools taTools) {
        this.sma200DailyBarsTrendingCriteria = new Sma200DailyBarsTrendingCriteria(taTools, (bar, sma200) -> bar.getClose() < sma200);
    }

    @Override
    public boolean apply(List<OhlcPlusBar> d1Bars) {
        return this.sma200DailyBarsTrendingCriteria.apply(d1Bars);
    }
}
