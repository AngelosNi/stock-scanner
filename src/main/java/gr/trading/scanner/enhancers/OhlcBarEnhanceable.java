package gr.trading.scanner.enhancers;

import gr.trading.scanner.model.OhlcPlusBar;

import java.util.List;

public interface OhlcBarEnhanceable {

    List<OhlcPlusBar> enhance(List<OhlcPlusBar> bar);
}
