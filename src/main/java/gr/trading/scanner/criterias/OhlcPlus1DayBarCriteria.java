package gr.trading.scanner.criterias;

import gr.trading.scanner.model.OhlcPlusBar;

import java.util.List;

public interface OhlcPlus1DayBarCriteria {

    boolean apply(List<OhlcPlusBar> d1Bars) ;
}
