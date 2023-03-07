package gr.trading.scanner.services;

import gr.trading.scanner.enhancers.OhlcBarEnhanceable;
import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.repositories.stockdata.StockDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SymbolHandler {

    private StockDataRepository<OhlcBar> repository;

    private List<OhlcBarEnhanceable> ohlcBarEnhancers;

    @Async
    public Future<List<OhlcPlusBar>> findAndEnhanceOhlcBars(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        List<OhlcBar> bars = repository.findStockBySymbolAndDates(symbol, start, end, interval);

        List<OhlcPlusBar> plusBars = bars.stream()
                .map(OhlcPlusBar::new)
                .collect(Collectors.toList());
        for (OhlcBarEnhanceable enhancer : ohlcBarEnhancers) {
            plusBars = enhancer.enhance(plusBars);
        }
        return CompletableFuture.completedFuture(plusBars);
    }

    @Async
    public Future<List<OhlcBar>> findOhlcBars(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        return CompletableFuture.completedFuture(repository.findStockBySymbolAndDates(symbol, start, end, interval));
    }
}
