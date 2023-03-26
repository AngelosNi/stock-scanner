package gr.trading.scanner.services;

import gr.trading.scanner.model.Interval;
import gr.trading.scanner.services.scanners.Scanner;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@AllArgsConstructor
@Slf4j
public class SymbolHandlerExecutor {

    private final Scanner dailyScanner;

    private final Scanner min5Scanner;

    @Async
    public Future<Map<String, List<String>>> findSymbolsByCriteria(List<String> symbols, LocalDateTime start, LocalDateTime end, Interval interval) {
        Map<String, List<String>> filteredSymbols = switch (interval) {
            case D1 -> findDailySymbolsByCriteria(symbols, start, end);
            case M5 -> find5MinSymbolsByCriteria(symbols, start, end);
        };

        return CompletableFuture.completedFuture(filteredSymbols);
    }

    private Map<String, List<String>> find5MinSymbolsByCriteria(List<String> symbols, LocalDateTime start, LocalDateTime end) {
        return Map.of("Bullish", min5Scanner.filterBullish(dailyScanner.filterBullish(symbols, start), start),
                "Bearish", min5Scanner.filterBearish(dailyScanner.filterBearish(symbols, start), start));
    }

    private Map<String, List<String>> findDailySymbolsByCriteria(List<String> symbols, LocalDateTime start, LocalDateTime end) {
        return Map.of("Bullish", dailyScanner.filterBullish(symbols, start),
                "Bearish", dailyScanner.filterBearish(symbols, start));
    }
}

