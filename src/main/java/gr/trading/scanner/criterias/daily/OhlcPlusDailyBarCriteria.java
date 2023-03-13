package gr.trading.scanner.criterias.daily;

import gr.trading.scanner.model.OhlcPlusBar;

import java.util.List;

public interface OhlcPlusDailyBarCriteria {

    boolean apply(List<OhlcPlusBar> d1Bars) ;
}
