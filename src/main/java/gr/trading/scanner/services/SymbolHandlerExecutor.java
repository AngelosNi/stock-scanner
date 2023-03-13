package gr.trading.scanner.services;

import gr.trading.scanner.criterias.fivemin.OhlcPlus5MinBarCriteria;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SymbolHandlerExecutor {

    private final SymbolHandler symbolHandler;

    private final DateTimeUtils dateTimeUtils;

    public Map<String, List<String>> findSymbolsByCriteria(List<String> symbols, LocalDateTime start, LocalDateTime end) {
        Map<String, List<DailySymbolData>> filteredDailyByCategory = findSymbolsByDailyCriteria(symbols, start, end);

        List<DailySymbolData> filteredDailyList = filteredDailyByCategory.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return findSymbolsBy5MinCriteria(filteredDailyList, start, end);
    }

    private Map<String, List<DailySymbolData>> findSymbolsByDailyCriteria(List<String> symbols, LocalDateTime start, LocalDateTime end) {
        List<DailySymbolData> filteredDailySymbols = symbols.stream()
                .map(sym -> new DailySymbolData(sym, symbolHandler.findAndEnhanceDailyBars(sym, start, dateTimeUtils.getNowDay())))
                .filter(dailySymbolData -> {
                    if (dailySymbolData.dailyBars().isEmpty()) {
                        log.error("{} is empty", dailySymbolData);
                        return false;
                    }
                    return true;
                })
                .filter(dailySymbolData -> symbolHandler.dailyCriteriaApply(dailySymbolData.dailyBars()))
                .collect(Collectors.toList());

        List<DailySymbolData> bullishDailyDailySymbolData = filteredDailySymbols.stream()
                .filter(dailySymbolData -> symbolHandler.dailyBullishCriteriaApply(dailySymbolData.dailyBars()))
                .collect(Collectors.toList());

        List<DailySymbolData> bearishDailyDailySymbolData = filteredDailySymbols.stream()
                .filter(dailySymbolData -> symbolHandler.dailyBearishCriteriaApply(dailySymbolData.dailyBars()))
                .collect(Collectors.toList());

        return Map.of("Bullish", bullishDailyDailySymbolData, "Bearish", bearishDailyDailySymbolData);
    }

    private Map<String, List<String>> findSymbolsBy5MinCriteria(List<DailySymbolData> dailySymbolsData, LocalDateTime start, LocalDateTime end) {
        List<Min5SymbolData> filtered5MinSymbols = dailySymbolsData.stream()
                .map(dailySymbolData -> new Min5SymbolData(dailySymbolData.name(), dailySymbolData.dailyBars(), symbolHandler.findAndEnhance5MinBars(dailySymbolData.name(), start, dateTimeUtils.getNowDay())))
                .filter(min5SymbolData -> {
                    if (min5SymbolData.dailyBars().isEmpty()) {
                        log.error("{} is empty", min5SymbolData);
                        return false;
                    }
                    return true;
                })
                .filter(min5SymbolData -> {
                    try {
                        return symbolHandler.min5CriteriaApply(min5SymbolData.min5Bars(), min5SymbolData.dailyBars());
                    } catch (OhlcPlus5MinBarCriteria.NoRecentDataException e) {
                        log.warn("No recent data was found for 5 min {}", min5SymbolData.name());
                        return false;
                    }
                })
                .collect(Collectors.toList());

        List<String> bullish5MinSymbols = filtered5MinSymbols.stream()
                .filter(min5SymbolData -> {
                    try {
                        return symbolHandler.min5BullishCriteriaApply(min5SymbolData.min5Bars(), min5SymbolData.dailyBars());
                    } catch (OhlcPlus5MinBarCriteria.NoRecentDataException e) {
                        log.warn("No recent data was found for 5 min {}", min5SymbolData.name());
                        return false;
                    }
                })
                .map(Min5SymbolData::name)
                .collect(Collectors.toList());

        List<String> bearish5MinSymbols = filtered5MinSymbols.stream()
                .filter(min5SymbolData -> {
                    try {
                        return symbolHandler.min5BearishCriteriaApply(min5SymbolData.min5Bars(), min5SymbolData.dailyBars());
                    } catch (OhlcPlus5MinBarCriteria.NoRecentDataException e) {
                        log.warn("No recent data was found for 5 min {}", min5SymbolData.name());
                        return false;
                    }
                })
                .map(Min5SymbolData::name)
                .collect(Collectors.toList());

        return Map.of("Bullish", bullish5MinSymbols, "Bearish", bearish5MinSymbols);
    }

    public record DailySymbolData(String name, List<OhlcPlusBar> dailyBars) {
    }

    public record Min5SymbolData(String name, List<OhlcPlusBar> dailyBars, List<OhlcPlusBar> min5Bars) {
    }

}

