package gr.trading.scanner.services;

import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SymbolHandlerExecutor {

    private final SymbolHandler symbolHandler;

    private final DateTimeUtils dateTimeUtils;

//    public List<OhlcPlusBar> findAndEnhanceOhlcBarsParallel(List<String> symbols, LocalDateTime start, LocalDateTime end, Interval interval) throws ExecutionException, InterruptedException {
//
//        List<Future<List<OhlcPlusBar>>> tasks = new ArrayList<>();
//        List<Future<List<OhlcBar>>> dailyTasks = new ArrayList<>();
//        for (String symbol : symbols) {
//            tasks.add(CompletableFuture.completedFuture(symbolHandler.findAndEnhanceDailyBars(symbol, start, dateTimeUtils.getNowDayTime(), Interval.M5)));
//            dailyTasks.add(CompletableFuture.completedFuture(symbolHandler.findOhlcBars(symbol, start, dateTimeUtils.getNowDay(), Interval.D1)));
//        }
//
//        List<OhlcPlusBar> bars = new ArrayList<>();
//        List<OhlcBar> dailyBars = new ArrayList<>();
//
//        for (Future<List<OhlcPlusBar>> task : tasks) {
//            bars.addAll(task.get());
//        }
//
//        for (Future<List<OhlcBar>> task : dailyTasks) {
//            dailyBars.addAll(task.get());
//        }
//
//        return bars;
//    }

    public Map<String, List<String>> findSymbolsByCriterias(List<String> symbols, LocalDateTime start, LocalDateTime end) {
        List<SymbolData> filteredDailySymbols = symbols.stream()
                .map(sym -> new SymbolData(sym, symbolHandler.findAndEnhanceDailyBars(sym, start, dateTimeUtils.getNowDay())))
                .filter(symbolData -> {
                    if (symbolData.bars().isEmpty()) {
                        log.error("{} is empty", symbolData);
                        return false;
                    }
                    return true;
                })
                .filter(symbolData -> symbolHandler.dailyCriteriaApply(symbolData.bars()))
                .collect(Collectors.toList());

        List<String> bullishDailySymbols = filteredDailySymbols.stream()
                .filter(symbolData -> symbolHandler.dailyBullishCriteriaApply(symbolData.bars()))
                .map(SymbolData::name)
                .collect(Collectors.toList());

        List<String> bearishDailySymbols = filteredDailySymbols.stream()
                .filter(symbolData -> symbolHandler.dailyBearishCriteriaApply(symbolData.bars()))
                .map(SymbolData::name)
                .collect(Collectors.toList());

        return Map.of("Bullish", bullishDailySymbols, "Bearish", bearishDailySymbols);
    }

    public record SymbolData(String name, List<OhlcPlusBar> bars) {
    }

}

