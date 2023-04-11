package gr.trading.scanner.repositories.stockdata;

import gr.trading.scanner.mappers.OhlcBarToStockDataEntityMapper;
import gr.trading.scanner.mappers.StockDataEntityToOhlcBarMapper;
import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.repositories.DailyDataCache;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("compositeStockDataRepository")
@AllArgsConstructor
@Slf4j
public class CompositeStockDataRepository implements StockDataRepository {

    @Qualifier("yfinanceDataRepository")
    private final StockDataRepository<OhlcBar> stockDataRepository;

    private final StockDataEntityToOhlcBarMapper stockDataEntityToOhlcBarMapper;

    private final OhlcBarToStockDataEntityMapper ohlcBarToStockDataEntityMapper;

    private final DbStockDataRepository dbStockDataRepository;

    private final DateTimeUtils dateTimeUtils;

    private final DailyDataCache dailyDataCache;

    @Override
    public List<OhlcBar> findStockDataBySymbolAndDates(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        List<OhlcBar> bars;

        // Only cache D1 bars, since for the 5m intervals either way we need to re-fetch in every call
        if (interval == Interval.D1) {
            bars = checkCacheAndFetch(symbol, start, end, interval);
        } else {
            bars = stockDataRepository.findStockDataBySymbolAndDates(symbol, dateTimeUtils.subtractDaysSkippingWeekends(end, 10), end.plusDays(1), interval);
        }

        return bars;
    }

    private List<OhlcBar> checkCacheAndFetch(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        List<OhlcBar> bars = dailyDataCache.getSymbolDataEntities(symbol, interval, start).map(entities -> entities.stream()
                .map(stockDataEntityToOhlcBarMapper::map)
                .collect(Collectors.toList()))
                .orElseGet(() -> {
                    log.debug("Cache miss on {} and interval {}", symbol, interval);
                    return dbStockDataRepository.findByIdSymbolAndIdBarIntervalAndIdBarDateTimeGreaterThanEqual(symbol, interval, start).stream()
                            .map(stockDataEntityToOhlcBarMapper::map)
                            .collect(Collectors.toList());
                });

        // CAUTION: Only checks if the LAST datetime matches. It may be missing the older ones though, if "start" date is changed
        // If getInBetweenTimes is to be used, it must first exclude ALL non-working days NOT only the weekends
        boolean isDataMissing = bars.isEmpty() || !bars.get(bars.size() - 1).getTime().equals(dateTimeUtils.getLastWorkingHoursDateTime(interval));

        if (isDataMissing) {
            List<OhlcBar> cachedBars = new ArrayList<>(bars);
            bars = stockDataRepository.findStockDataBySymbolAndDates(symbol, start, end, interval);
            List<OhlcBar> fetchedBars = new ArrayList<>(bars);

            // Only save to DB the missing bars
            fetchedBars.removeAll(cachedBars);
            fetchedBars.forEach(s -> {
                dbStockDataRepository.save(ohlcBarToStockDataEntityMapper.map(s, interval));
                log.debug("Inserted to DB symbol with interval: {}, action_date: {}", interval.name(), s.getTime());
            });
        } else {
            log.debug("All data for symbol {} and interval {} already exist", symbol, interval.name());
        }
        return bars;
    }
}
