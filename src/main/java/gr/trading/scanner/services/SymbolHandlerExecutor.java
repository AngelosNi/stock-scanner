package gr.trading.scanner.services;

import gr.trading.scanner.model.Interval;
import gr.trading.scanner.services.scanners.Scanner;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static gr.trading.scanner.controllers.StockDataController.LOOK_BACK_DAYS_FOR_DAILY;

@Service
@AllArgsConstructor
@Slf4j
public class SymbolHandlerExecutor {

    private final Scanner dailyScanner;

    private final Scanner min5Scanner;

    private final DateTimeUtils dateTimeUtils;

    @Async
    public Future<Map<String, List<String>>> findSymbolsByCriteria(List<String> symbols, LocalDateTime start, Interval interval) {
        Map<String, List<String>> filteredSymbols = switch (interval) {
            case D1 -> findDailySymbolsByCriteria(symbols, start);
            case M5 -> find5MinSymbolsByCriteria(symbols, start);
        };

        return CompletableFuture.completedFuture(filteredSymbols);
    }

    private Map<String, List<String>> find5MinSymbolsByCriteria(List<String> symbols, LocalDateTime start) {
        LocalDateTime dailiesStart = dateTimeUtils.subtractDaysSkippingWeekends(dateTimeUtils.getNowDayAtSessionStart(), LOOK_BACK_DAYS_FOR_DAILY);
        return Map.of("Bullish", min5Scanner.filterBullish(dailyScanner.filterBullish(symbols, dailiesStart), start),
                "Bearish", min5Scanner.filterBearish(dailyScanner.filterBearish(symbols, dailiesStart), start));
    }

    private Map<String, List<String>> findDailySymbolsByCriteria(List<String> symbols, LocalDateTime start) {
        return Map.of("Bullish", dailyScanner.filterBullish(symbols, start),
                "Bearish", dailyScanner.filterBearish(symbols, start));
    }
}

