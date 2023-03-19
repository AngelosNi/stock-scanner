package gr.trading.scanner.services;

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
    public Future<Map<String, List<String>>> findSymbolsByCriteria(List<String> symbols, LocalDateTime start, LocalDateTime end) {
        Map<String, List<String>> filteredSymbols = Map.of("Bullish", min5Scanner.filterBullish(dailyScanner.filterBullish(List.of("GILD"), start.minusDays(200)), start),
                "Bearish", min5Scanner.filterBearish(dailyScanner.filterBearish(List.of("GILD"), start.minusDays(200)), start));

        return CompletableFuture.completedFuture(filteredSymbols);
    }
}

