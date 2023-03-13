package gr.trading.scanner.repositories.stockdata;

import gr.trading.scanner.mappers.StockDataEntityToOhlcBarMapper;
import gr.trading.scanner.mappers.TwelveDataBarToDataEntityMapper;
import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.entities.DataEntity;
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

    @Override
    public List<OhlcBar> findStockDataBySymbolAndDates(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        List<DataEntity> dailyBars = new ArrayList<>(dbStockDataRepository.findByIdSymbolAndIdBarInterval(symbol, interval));

        boolean isDataMissing = !dailyBars.stream()
                .map(e -> e.getId().getBarDateTime())
                .collect(Collectors.toList())
                .containsAll(dateTimeUtils.getInBetweenDates(start.toLocalDate(), end.toLocalDate()));

        if (isDataMissing) {
            dailyBars = fetchDataFromTwelveEndpoint(symbol, start, end, interval);
            dailyBars.forEach(s -> {
                dbStockDataRepository.save(s);
                log.info("Inserted to DB symbol: {}, action_date: {}", s.getId().getSymbol(), s.getId().getBarDateTime());
            });
        } else {
            log.info("All data for symbol {} already exist", symbol);
        }

        return dailyBars.stream()
                .map(stockDataEntityToOhlcBarMapper::map)
                .collect(Collectors.toList());
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
