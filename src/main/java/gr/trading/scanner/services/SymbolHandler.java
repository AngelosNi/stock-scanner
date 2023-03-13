package gr.trading.scanner.services;

import gr.trading.scanner.criterias.daily.OhlcPlusDailyBarCriteria;
import gr.trading.scanner.criterias.fivemin.OhlcPlus5MinBarCriteria;
import gr.trading.scanner.enhancers.OhlcBarEnhanceable;
import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.repositories.stockdata.StockDataRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SymbolHandler {

    private StockDataRepository<OhlcBar> repository;

    private List<OhlcBarEnhanceable> ohlcBarEnhancers;

    private List<OhlcPlusDailyBarCriteria> ohlcPlusDailyBarBullishCriteria;

    private List<OhlcPlusDailyBarCriteria> ohlcPlusDailyBarBearishCriteria;

    private List<OhlcPlus5MinBarCriteria> ohlcPlus5MinBarBullishCriteria;

    private List<OhlcPlus5MinBarCriteria> ohlcPlus5MinBarBearishCriteria;

    public SymbolHandler(StockDataRepository<OhlcBar> repository,
                         List<OhlcBarEnhanceable> ohlcBarEnhancers,
                         @Qualifier("BullishCriteria") List<OhlcPlusDailyBarCriteria> ohlcPlusDailyBarBullishCriteria,
                         @Qualifier("BearishCriteria") List<OhlcPlusDailyBarCriteria> ohlcPlusDailyBarBearishCriteria,
                         @Qualifier("BullishCriteria") List<OhlcPlus5MinBarCriteria> ohlcPlus5MinBarBullishCriteria,
                         @Qualifier("BearishCriteria") List<OhlcPlus5MinBarCriteria> ohlcPlus5MinBarBearishCriteria) {
        this.repository = repository;
        this.ohlcBarEnhancers = ohlcBarEnhancers;
        this.ohlcPlusDailyBarBullishCriteria = ohlcPlusDailyBarBullishCriteria;
        this.ohlcPlusDailyBarBearishCriteria = ohlcPlusDailyBarBearishCriteria;
        this.ohlcPlus5MinBarBullishCriteria = ohlcPlus5MinBarBullishCriteria;
        this.ohlcPlus5MinBarBearishCriteria = ohlcPlus5MinBarBearishCriteria;
    }

    public List<OhlcPlusBar> findAndEnhanceDailyBars(String symbol, LocalDateTime start, LocalDateTime end) {
        return findAndEnhanceBars(symbol, start, end, Interval.D1);
    }

    public List<OhlcPlusBar> findAndEnhance5MinBars(String symbol, LocalDateTime start, LocalDateTime end) {
        return findAndEnhanceBars(symbol, start, end, Interval.M5);
    }

    public boolean dailyBullishCriteriaApply(List<OhlcPlusBar> plusBars) {
        for (OhlcPlusDailyBarCriteria criteria : ohlcPlusDailyBarBullishCriteria) {
            if (!criteria.apply(plusBars)) {
                return false;
            }
        }
        return true;
    }

    public boolean dailyBearishCriteriaApply(List<OhlcPlusBar> plusBars) {
        for (OhlcPlusDailyBarCriteria criteria : ohlcPlusDailyBarBearishCriteria) {
            if (!criteria.apply(plusBars)) {
                return false;
            }
        }
        return true;
    }

    public boolean dailyCriteriaApply(List<OhlcPlusBar> plusBars) {
        return dailyBullishCriteriaApply(plusBars) || dailyBearishCriteriaApply(plusBars);
    }

    public boolean min5BullishCriteriaApply(List<OhlcPlusBar> min5PlusBars, List<OhlcPlusBar> dailyPlusBars) throws OhlcPlus5MinBarCriteria.NoRecentDataException {
        for (OhlcPlus5MinBarCriteria criteria : ohlcPlus5MinBarBullishCriteria) {
            if (!criteria.apply(min5PlusBars, dailyPlusBars)) {
                return false;
            }
        }
        return true;
    }

    public boolean min5BearishCriteriaApply(List<OhlcPlusBar> min5PlusBars, List<OhlcPlusBar> dailyPlusBars) throws OhlcPlus5MinBarCriteria.NoRecentDataException {
        for (OhlcPlus5MinBarCriteria criteria : ohlcPlus5MinBarBearishCriteria) {
            if (!criteria.apply(min5PlusBars, dailyPlusBars)) {
                return false;
            }
        }
        return true;
    }

    public boolean min5CriteriaApply(List<OhlcPlusBar> min5PlusBars, List<OhlcPlusBar> dailyPlusBars) throws OhlcPlus5MinBarCriteria.NoRecentDataException {
        return min5BullishCriteriaApply(min5PlusBars, dailyPlusBars) || min5BearishCriteriaApply(min5PlusBars, dailyPlusBars);
    }

    private List<OhlcPlusBar> findAndEnhanceBars(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        List<OhlcBar> bars = repository.findStockDataBySymbolAndDates(symbol, start, end, interval);

        List<OhlcPlusBar> plusBars = bars.stream()
                .map(OhlcPlusBar::new)
                .collect(Collectors.toList());
        for (OhlcBarEnhanceable enhancer : ohlcBarEnhancers) {
            plusBars = enhancer.enhance(plusBars);
        }
        return plusBars.stream()
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
    }
}
