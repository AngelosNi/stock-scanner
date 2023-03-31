package gr.trading.scanner.services.scanners;

import gr.trading.scanner.criterias.daily.OhlcPlusDailyBarCriteria;
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
public class DailyScanner implements Scanner {

    private final SymbolEnhancer symbolEnhancer;

    private final DateTimeUtils dateTimeUtils;

    @Qualifier("BullishCriteria")
    private List<OhlcPlusDailyBarCriteria> bullishCriteria;

    @Qualifier("BearishCriteria")
    private List<OhlcPlusDailyBarCriteria> bearishCriteria;

    @Qualifier("CommonCriteria")
    private List<OhlcPlusDailyBarCriteria> commonCriteria;

    @Override
    public List<String> filterBullish(List<String> symbols, LocalDateTime start) {
        return constructBars(symbols, start).stream()
                .filter(data -> !data.dailyBars().isEmpty())
                .filter(dailySymbolData -> commonCriteriaApply(dailySymbolData.dailyBars()))
                .filter(dailySymbolData -> bullishCriteriaApply(dailySymbolData.dailyBars()))
                .map(DailySymbolData::name)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> filterBearish(List<String> symbols, LocalDateTime start) {
        return constructBars(symbols, start).stream()
                .filter(data -> !data.dailyBars().isEmpty())
                .filter(dailySymbolData -> commonCriteriaApply(dailySymbolData.dailyBars()))
                .filter(dailySymbolData -> bearishCriteriaApply(dailySymbolData.dailyBars()))
                .map(DailySymbolData::name)
                .collect(Collectors.toList());
    }

    private List<DailySymbolData> constructBars(List<String> symbols, LocalDateTime start) {
        return symbols.stream()
                .map(sym -> new DailySymbolData(sym, symbolEnhancer.findDailyBars(sym, start, dateTimeUtils.getNowDayAtSessionStart())))
                .filter(dailySymbolData -> {
                    if (dailySymbolData.dailyBars().isEmpty()) {
                        log.warn("{} is empty", dailySymbolData);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private boolean bullishCriteriaApply(List<OhlcPlusBar> plusBars) {
        for (OhlcPlusDailyBarCriteria criteria : bullishCriteria) {
            if (!criteria.apply(plusBars)) {
                return false;
            }
        }
        return true;
    }

    private boolean bearishCriteriaApply(List<OhlcPlusBar> plusBars) {
        for (OhlcPlusDailyBarCriteria criteria : bearishCriteria) {
            if (!criteria.apply(plusBars)) {
                return false;
            }
        }
        return true;
    }

    private boolean commonCriteriaApply(List<OhlcPlusBar> plusBars) {
        for (OhlcPlusDailyBarCriteria criteria : commonCriteria) {
            if (!criteria.apply(plusBars)) {
                return false;
            }
        }
        return true;
    }

    public record DailySymbolData(String name, List<OhlcPlusBar> dailyBars) {
    }
}
