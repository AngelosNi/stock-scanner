package gr.trading.scanner.services;

import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.OhlcPlusBar;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class ParallelSymbolHandler {

    private SymbolHandler symbolHandler;

    public List<OhlcPlusBar> findAndEnhanceOhlcBarsForSymbols(List<String> symbols, LocalDateTime start, LocalDateTime end, Interval interval) throws ExecutionException, InterruptedException {

        List<Future<List<OhlcPlusBar>>> tasks = new ArrayList<>();
        for (String symbol : symbols) {
            tasks.add(symbolHandler.findAndEnhanceOhlcBars(symbol, start, LocalDateTime.now(), Interval.M5));
        }

        List<OhlcPlusBar> bars = new ArrayList<>();
        for (Future<List<OhlcPlusBar>> task : tasks) {
            bars.addAll(task.get());
        }


        return bars;
    }
}
