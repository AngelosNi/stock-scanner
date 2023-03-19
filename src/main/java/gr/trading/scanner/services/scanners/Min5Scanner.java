package gr.trading.scanner.services.scanners;

import gr.trading.scanner.criterias.fivemin.OhlcPlus5MinBarCriteria;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.services.SymbolEnhancer;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class Min5Scanner implements Scanner {

    private final SymbolEnhancer symbolEnhancer;

    private final DateTimeUtils dateTimeUtils;

    @Qualifier("BullishCriteria")
    private List<OhlcPlus5MinBarCriteria> bullishCriteria;

    @Qualifier("BearishCriteria")
    private List<OhlcPlus5MinBarCriteria> bearishCriteria;

    @Qualifier("CommonCriteria")
    private List<OhlcPlus5MinBarCriteria> commonCriteria;

    @Override
    public List<String> filterBullish(List<String> symbols, LocalDateTime start) {
        return constructBars(symbols, start).stream()
                .filter(min5SymbolData -> {
                    try {
                        return commonCriteriaApply(min5SymbolData.min5Bars(), min5SymbolData.dailyBars());
                    } catch (OhlcPlus5MinBarCriteria.NoRecentDataException e) {
                        log.warn("No recent data was found for 5 min {}", min5SymbolData.name());
                        return false;
                    }
                })
                .filter(min5SymbolData -> {
                    try {
                        return bullishCriteriaApply(min5SymbolData.min5Bars(), min5SymbolData.dailyBars());
                    } catch (OhlcPlus5MinBarCriteria.NoRecentDataException e) {
                        log.warn("No recent data was found for 5 min {}", min5SymbolData.name());
                        return false;
                    }
                })
                .map(Min5SymbolData::name)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> filterBearish(List<String> symbols, LocalDateTime start) {
        return constructBars(symbols, start).stream()
                .filter(min5SymbolData -> {
                    try {
                        return bearishCriteriaApply(min5SymbolData.min5Bars(), min5SymbolData.dailyBars());
                    } catch (OhlcPlus5MinBarCriteria.NoRecentDataException e) {
                        log.warn("No recent data was found for 5 min {}", min5SymbolData.name());
                        return false;
                    }
                })
                .map(Min5SymbolData::name)
                .collect(Collectors.toList());
    }

    private List<Min5SymbolData> constructBars(List<String> symbols, LocalDateTime start) {
        return symbols.stream()
                .map(symbol -> new Min5SymbolData(symbol, symbolEnhancer.findAndEnhanceDailyBars(symbol, start, dateTimeUtils.getNowDay()),
                        symbolEnhancer.findAndEnhance5MinBars(symbol, start, dateTimeUtils.getNowDay())))
                .filter(min5SymbolData -> {
                    if (min5SymbolData.dailyBars().isEmpty()) {
                        log.warn("{} is empty", min5SymbolData);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private boolean bullishCriteriaApply(List<OhlcPlusBar> min5PlusBars, List<OhlcPlusBar> dailyPlusBars) throws OhlcPlus5MinBarCriteria.NoRecentDataException {
        for (OhlcPlus5MinBarCriteria criteria : bullishCriteria) {
            if (!criteria.apply(min5PlusBars, dailyPlusBars)) {
                return false;
            }
        }
        return true;
    }

    private boolean bearishCriteriaApply(List<OhlcPlusBar> min5PlusBars, List<OhlcPlusBar> dailyPlusBars) throws OhlcPlus5MinBarCriteria.NoRecentDataException {
        for (OhlcPlus5MinBarCriteria criteria : bearishCriteria) {
            if (!criteria.apply(min5PlusBars, dailyPlusBars)) {
                return false;
            }
        }
        return true;
    }

    private boolean commonCriteriaApply(List<OhlcPlusBar> plusBars, List<OhlcPlusBar> dailyPlusBars) throws OhlcPlus5MinBarCriteria.NoRecentDataException {
        for (OhlcPlus5MinBarCriteria criteria : commonCriteria) {
            if (!criteria.apply(plusBars, dailyPlusBars)) {
                return false;
            }
        }
        return true;
    }

    public record Min5SymbolData(String name, List<OhlcPlusBar> dailyBars, List<OhlcPlusBar> min5Bars) {
    }
}
