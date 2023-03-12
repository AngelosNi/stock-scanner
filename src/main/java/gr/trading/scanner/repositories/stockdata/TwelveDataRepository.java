package gr.trading.scanner.repositories.stockdata;

import gr.trading.scanner.mappers.TwelveDataBarToOhlcBarMapper;
import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.services.twelvedata.TwelveDataClient;
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

    private final TwelveDataBarToOhlcBarMapper mapper;

    @Override
    public List<OhlcBar> findStockBySymbolAndDates(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        try {
            return client.getStocksMarketData(List.of(symbol), start, end, interval.getTwelveDataInterval())
                    .getValues().stream()
                    .peek(v -> log.info(v.toString()))
                    .map(mapper::map)
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error while retrieving data from TwelveData", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<OhlcBar> findMultipleStocksBySymbolsAndDates(List<String> symbols, LocalDateTime start, LocalDateTime end, Interval interval) {
        return null;
    }
}
