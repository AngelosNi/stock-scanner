package gr.trading.scanner.services;

import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@AllArgsConstructor
public class ParallelSymbolHandler {

    private final SymbolHandler symbolHandler;

    private final DateTimeUtils dateTimeUtils;

    public List<OhlcPlusBar> findAndEnhanceOhlcBarsForSymbols(List<String> symbols, LocalDateTime start, LocalDateTime end, Interval interval) throws ExecutionException, InterruptedException {
Thread.sleep(10000);
        List<Future<List<OhlcPlusBar>>> tasks = new ArrayList<>();
        List<Future<List<OhlcBar>>> dailyTasks = new ArrayList<>();
        for (String symbol : symbols) {
            tasks.add(symbolHandler.findAndEnhanceOhlcBars(symbol, start, dateTimeUtils.getNowDayTime(), Interval.M5));
            dailyTasks.add(symbolHandler.findOhlcBars(symbol, start, dateTimeUtils.getNowDay(), Interval.D1));
        }

        List<OhlcPlusBar> bars = new ArrayList<>();
        List<OhlcBar> dailyBars = new ArrayList<>();

        for (Future<List<OhlcPlusBar>> task : tasks) {
            bars.addAll(task.get());
        }

        for (Future<List<OhlcBar>> task : dailyTasks) {
            dailyBars.addAll(task.get());
        }

        return bars;
    }
}

