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
import java.util.Objects;
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
                .filter(data -> !data.dailyBars().isEmpty() && !data.min5Bars().isEmpty())
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
                .filter(data -> !data.dailyBars().isEmpty() && !data.min5Bars().isEmpty())
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
                .map(symbol -> new Min5SymbolData(symbol, symbolEnhancer.findDailyBars(symbol, start, dateTimeUtils.getNowDayAtSessionStart()),
                        symbolEnhancer.findAndEnhance5MinBars(symbol, start, dateTimeUtils.getNowDayAtSessionStart())))
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
            if (!criteria.apply(dailyPlusBars, min5PlusBars)) {
                return false;
            }
        }
        return true;
    }

    private boolean bearishCriteriaApply(List<OhlcPlusBar> min5PlusBars, List<OhlcPlusBar> dailyPlusBars) throws OhlcPlus5MinBarCriteria.NoRecentDataException {
        for (OhlcPlus5MinBarCriteria criteria : bearishCriteria) {
            if (!criteria.apply(dailyPlusBars, min5PlusBars)) {
                return false;
            }
        }
        return true;
    }

    private boolean commonCriteriaApply(List<OhlcPlusBar> plusBars, List<OhlcPlusBar> dailyPlusBars) throws OhlcPlus5MinBarCriteria.NoRecentDataException {
        for (OhlcPlus5MinBarCriteria criteria : commonCriteria) {
            if (!criteria.apply(dailyPlusBars, plusBars)) {
                return false;
            }
        }
        return true;
    }

    public static final class Min5SymbolData {
        private final String name;
        private final List<OhlcPlusBar> dailyBars;
        private final List<OhlcPlusBar> min5Bars;

        public Min5SymbolData(String name, List<OhlcPlusBar> dailyBars, List<OhlcPlusBar> min5Bars) {
            this.name = name;

            this.dailyBars = dailyBars.stream()
                    .sorted((b1, b2) -> {
                        if (b1.getTime().isAfter(b2.getTime())) {
                            return 1;
                        } else if (b1.getTime().isBefore(b2.getTime())) {
                            return -1;
                        } else {
                            return 0;
                        }
                    })
                    .collect(Collectors.toList());

            this.min5Bars = min5Bars.stream()
                    .sorted((b1, b2) -> {
                        if (b1.getTime().isAfter(b2.getTime())) {
                            return 1;
                        } else if (b1.getTime().isBefore(b2.getTime())) {
                            return -1;
                        } else {
                            return 0;
                        }
                    })
                    .collect(Collectors.toList());;
        }

        public String name() {
            return name;
        }

        public List<OhlcPlusBar> dailyBars() {
            return dailyBars;
        }

        public List<OhlcPlusBar> min5Bars() {
            return min5Bars;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Min5SymbolData) obj;
            return Objects.equals(this.name, that.name) &&
                    Objects.equals(this.dailyBars, that.dailyBars) &&
                    Objects.equals(this.min5Bars, that.min5Bars);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, dailyBars, min5Bars);
        }

        @Override
        public String toString() {
            return "Min5SymbolData[" +
                    "name=" + name + ", " +
                    "dailyBars=" + dailyBars + ", " +
                    "min5Bars=" + min5Bars + ']';
        }

    }
}
