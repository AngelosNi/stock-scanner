package gr.trading.scanner.services;

import gr.trading.scanner.criterias.OhlcPlusDailyBarCriteria;
import gr.trading.scanner.enhancers.OhlcBarEnhanceable;
import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.repositories.stockdata.StockDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SymbolHandler {

    private StockDataRepository<OhlcBar> repository;

    private List<OhlcBarEnhanceable> ohlcBarEnhancers;

    private List<OhlcPlusDailyBarCriteria> ohlcPlusDailyBarCriteria;

    public List<OhlcPlusBar> findAndEnhanceDailyBars(String symbol, LocalDateTime start, LocalDateTime end) {
        List<OhlcBar> bars = repository.findStockBySymbolAndDates(symbol, start, end, Interval.D1);

        List<OhlcPlusBar> plusBars = bars.stream()
                .map(OhlcPlusBar::new)
                .collect(Collectors.toList());
        for (OhlcBarEnhanceable enhancer : ohlcBarEnhancers) {
            plusBars = enhancer.enhance(plusBars);
        }
        return plusBars.stream()
                .sorted((b1, b2) -> {
                    if (b1.getTime().isAfter(b2.getTime())) {
                        return 1;
                    } else if (b1.getTime().isBefore(b2.getTime())) {
                        return -1;
                    } else {
                        return 0;
                    }
                })
                .collect(Collectors.toList());
    }

    public boolean dailyCriteriasApply(List<OhlcPlusBar> plusBars) {
        for (OhlcPlusDailyBarCriteria criteria : ohlcPlusDailyBarCriteria) {
            if (!criteria.apply(plusBars)) {
                return false;
            }
        }
        return true;
    }
}
