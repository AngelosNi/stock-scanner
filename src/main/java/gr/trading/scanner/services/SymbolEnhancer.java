package gr.trading.scanner.services;

import gr.trading.scanner.enhancers.OhlcBarEnhanceable;
import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.repositories.stockdata.StockDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SymbolEnhancer {

    private StockDataRepository<OhlcBar> repository;

    private List<OhlcBarEnhanceable> ohlcBarEnhancers;

    public SymbolEnhancer(StockDataRepository<OhlcBar> repository,
                          List<OhlcBarEnhanceable> ohlcBarEnhancers) {
        this.repository = repository;
        this.ohlcBarEnhancers = ohlcBarEnhancers;
    }

    public List<OhlcPlusBar> findDailyBars(String symbol, LocalDateTime start, LocalDateTime end) {
        // All daily bars should set their time at 0:00:00
        return findBars(symbol, start.toLocalDate().atStartOfDay(), end, Interval.D1);
    }

    public List<OhlcPlusBar> findAndEnhance5MinBars(String symbol, LocalDateTime start, LocalDateTime end) {
        return findAndEnhanceBars(symbol, start, end, Interval.M5);
    }

    private List<OhlcPlusBar> findBars(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        List<OhlcBar> bars = repository.findStockDataBySymbolAndDates(symbol, start, end, interval);

        return bars.stream()
                .map(OhlcPlusBar::new)
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

    private List<OhlcPlusBar> findAndEnhanceBars(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        List<OhlcPlusBar> plusBars = findBars(symbol, start, end, interval);
        for (OhlcBarEnhanceable enhancer : ohlcBarEnhancers) {
            plusBars = enhancer.enhance(plusBars);
        }

        return plusBars;
    }
}
