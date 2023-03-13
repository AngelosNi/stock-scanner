package gr.trading.scanner.criterias;

import gr.trading.scanner.model.OhlcPlusBar;

import java.util.List;

public interface OhlcPlusDailyBarCriteria {

    boolean apply(List<OhlcPlusBar> d1Bars) ;
}
