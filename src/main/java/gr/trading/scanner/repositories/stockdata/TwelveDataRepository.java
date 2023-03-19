package gr.trading.scanner.repositories.stockdata;

import gr.trading.scanner.mappers.StockDataEntityToOhlcBarMapper;
import gr.trading.scanner.mappers.TwelveDataBarToDataEntityMapper;
import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.entities.DataEntity;
import gr.trading.scanner.repositories.DailyDataCache;
import gr.trading.scanner.services.twelvedata.TwelveDataClient;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class TwelveDataRepository implements StockDataRepository<OhlcBar> {

    private final TwelveDataClient client;

    private final StockDataEntityToOhlcBarMapper stockDataEntityToOhlcBarMapper;

    private final TwelveDataBarToDataEntityMapper twelveDataBarToDataEntityMapper;

    private final DbStockDataRepository dbStockDataRepository;

    private final DateTimeUtils dateTimeUtils;

    private final DailyDataCache dailyDataCache;

    @Override
    public List<OhlcBar> findStockDataBySymbolAndDates(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        List<DataEntity> bars;

        if (interval == Interval.D1) {
            bars = checkCacheAndFetch(symbol, start, end, interval);
        } else {
            bars = fetchDataFromTwelveEndpoint(symbol, start, end, interval);
        }

        return bars.stream()
                .map(stockDataEntityToOhlcBarMapper::map)
                .collect(Collectors.toList());
    }

    private List<DataEntity> checkCacheAndFetch(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        List<DataEntity> bars = new ArrayList<>(dailyDataCache.getSymbolDataEntities(symbol, interval, start)
                .orElseGet(() -> {
                    log.info("Cache miss on {} and interval {}", symbol, interval);
                    return dbStockDataRepository.findByIdSymbolAndIdBarIntervalAndIdBarDateTimeGreaterThanEqual(symbol, interval, start);
                }));

        boolean isDataMissing = !bars.stream()
                .map(e -> e.getId().getBarDateTime())
                .collect(Collectors.toList())
                .containsAll(dateTimeUtils.getInBetweenTimes(start, end, interval));

        if (isDataMissing) {
            bars = fetchDataFromTwelveEndpoint(symbol, start, end, interval);
            bars.forEach(s -> {
                dbStockDataRepository.save(s);
                log.debug("Inserted to DB symbol: {}, interval: {}, action_date: {}", s.getId().getSymbol(), interval.name(), s.getId().getBarDateTime());
            });
        } else {
            log.debug("All data for symbol {} and interval {} already exist", symbol, interval.name());
        }
        return bars;
    }

    private List<DataEntity> fetchDataFromTwelveEndpoint(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        List<DataEntity> symbols;
        try {
            symbols = client.getStocksMarketData(List.of(symbol), start, end, interval.getTwelveDataInterval())
                    .getValues().stream()
                    .map(bar -> twelveDataBarToDataEntityMapper.map(bar, symbol, interval))
                    .collect(Collectors.toList());

        } catch (ExecutionException | InterruptedException e) {
            log.error("Error while retrieving data from TwelveData", e);
            throw new RuntimeException(e);
        }

        return symbols;
    }
}
