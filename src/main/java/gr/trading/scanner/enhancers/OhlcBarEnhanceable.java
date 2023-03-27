package gr.trading.scanner.enhancers;

import gr.trading.scanner.model.OhlcPlusBar;

import java.util.List;

public interface OhlcBarEnhanceable {

    List<OhlcPlusBar> enhanceDailies(List<OhlcPlusBar> bars);

    List<OhlcPlusBar> enhance5Mins(List<OhlcPlusBar> bar);
}
