package gr.trading.scanner.services;

import gr.trading.scanner.criterias.daily.OhlcPlusDailyBarCriteria;
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

    @Qualifier("BullishCriteria")
    private List<OhlcPlusDailyBarCriteria> ohlcPlusDailyBarBullishCriteria;

    private List<OhlcPlusDailyBarCriteria> ohlcPlusDailyBarBearishCriteria;

    public SymbolHandler(StockDataRepository<OhlcBar> repository,
                         List<OhlcBarEnhanceable> ohlcBarEnhancers,
                         @Qualifier("BullishCriteria") List<OhlcPlusDailyBarCriteria> ohlcPlusDailyBarBullishCriteria,
                         @Qualifier("BearishCriteria") List<OhlcPlusDailyBarCriteria> ohlcPlusDailyBarBearishCriteria) {
        this.repository = repository;
        this.ohlcBarEnhancers = ohlcBarEnhancers;
        this.ohlcPlusDailyBarBullishCriteria = ohlcPlusDailyBarBullishCriteria;
        this.ohlcPlusDailyBarBearishCriteria = ohlcPlusDailyBarBearishCriteria;
    }

    public List<OhlcPlusBar> findAndEnhanceDailyBars(String symbol, LocalDateTime start, LocalDateTime end) {
        List<OhlcBar> bars = repository.findStockBySymbolAndDates(symbol, start, end, Interval.D1);

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
}
