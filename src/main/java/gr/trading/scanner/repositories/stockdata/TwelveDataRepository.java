package gr.trading.scanner.repositories.stockdata;

import gr.trading.scanner.mappers.TwelveDataBarToDataEntityMapper;
import gr.trading.scanner.mappers.StockDataEntityToOhlcBarMapper;
import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.entities.DailyDataEntity;
import gr.trading.scanner.services.twelvedata.TwelveDataClient;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    private final DailyStockDataRepository repository;

    private final DateTimeUtils dateTimeUtils;

    @Override
    public List<OhlcBar> findStockBySymbolAndDates(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        List<DailyDataEntity> symbols = repository.findBySymbolDateIdSymbol(symbol);

        boolean isDataMissing = !symbols.stream()
                .map(e -> e.getSymbolDateId().getActionDate())
                .collect(Collectors.toList())
                .containsAll(dateTimeUtils.getInBetweenDates(start.toLocalDate(), end.toLocalDate()));
        log.info("All data for symbol {} already exist", symbol);

        if (isDataMissing) {
            try {
                symbols = client.getStocksMarketData(List.of(symbol), start, end, interval.getTwelveDataInterval())
                        .getValues().stream()
                        .map(bar -> twelveDataBarToDataEntityMapper.map(bar, symbol))
                        .collect(Collectors.toList());


                symbols.forEach(s -> {
                    repository.save(s);
                    log.info("Inserted to DB symbol: {}, action_date: {}", s.getSymbolDateId().getSymbol(), s.getSymbolDateId().getActionDate());
                });

            } catch (ExecutionException | InterruptedException e) {
                log.error("Error while retrieving data from TwelveData", e);
                throw new RuntimeException(e);
            }
        }
        return symbols.stream()
                .map(stockDataEntityToOhlcBarMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<OhlcBar> findMultipleStocksBySymbolsAndDates(List<String> symbols, LocalDateTime start, LocalDateTime end, Interval interval) {
        return null;
    }
}
