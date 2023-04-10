package gr.trading.scanner.repositories.stockdata;

import gr.trading.scanner.mappers.StockDataEntityToOhlcBarMapper;
import gr.trading.scanner.mappers.TwelveDataBarToDataEntityMapper;
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

@Component("twelveDataRepository")
@AllArgsConstructor
@Slf4j
public class TwelveDataRepository implements StockDataRepository<OhlcBar> {

    private final TwelveDataClient client;

    private final TwelveDataBarToDataEntityMapper twelveDataBarToDataEntityMapper;

    private final StockDataEntityToOhlcBarMapper stockDataEntityToOhlcBarMapper;

    @Override
    public List<OhlcBar> findStockDataBySymbolAndDates(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        List<OhlcBar> symbols;
        try {
            symbols = client.getStocksMarketData(List.of(symbol), start, end, interval.getTwelveDataInterval())
                    .getValues().stream()
                    .map(bar -> twelveDataBarToDataEntityMapper.map(bar, symbol, interval))
                    .map(stockDataEntityToOhlcBarMapper::map)
                    .collect(Collectors.toList());

        } catch (ExecutionException | InterruptedException e) {
            log.error("Error while retrieving data from TwelveData", e);
            throw new RuntimeException(e);
        }

        return symbols;
    }
}
