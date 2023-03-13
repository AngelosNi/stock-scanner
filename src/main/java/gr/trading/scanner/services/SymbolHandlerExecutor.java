package gr.trading.scanner.services;

import com.google.common.util.concurrent.RateLimiter;
import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SymbolHandlerExecutor {

    private final SymbolHandler symbolHandler;

    private final DateTimeUtils dateTimeUtils;

    public List<OhlcPlusBar> findAndEnhanceOhlcBarsParallel(List<String> symbols, LocalDateTime start, LocalDateTime end, Interval interval) throws ExecutionException, InterruptedException {

        List<Future<List<OhlcPlusBar>>> tasks = new ArrayList<>();
        List<Future<List<OhlcBar>>> dailyTasks = new ArrayList<>();
        for (String symbol : symbols) {
            tasks.add(CompletableFuture.completedFuture(symbolHandler.findAndEnhanceOhlcBars(symbol, start, dateTimeUtils.getNowDayTime(), Interval.M5)));
            dailyTasks.add(CompletableFuture.completedFuture(symbolHandler.findOhlcBars(symbol, start, dateTimeUtils.getNowDay(), Interval.D1)));
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

    public List<OhlcPlusBar> findAndEnhanceOhlcBarsRateLimited(List<String> symbols, LocalDateTime start, LocalDateTime end, Interval interval) throws ExecutionException, InterruptedException {

        RateLimiter rateLimiter = RateLimiter.create(0.6);
        return symbols.stream()
                .map(sym -> {
                    rateLimiter.acquire();
                    return symbolHandler.findAndEnhanceOhlcBars(sym, start, dateTimeUtils.getNowDay(), Interval.D1);
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }


}

