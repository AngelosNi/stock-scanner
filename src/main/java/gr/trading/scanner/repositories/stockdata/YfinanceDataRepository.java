package gr.trading.scanner.repositories.stockdata;

import gr.trading.scanner.mappers.StockDataEntityToOhlcBarMapper;
import gr.trading.scanner.mappers.YfinanceBarToDataEntityMapper;
import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.services.yfinance.YfinanceDataClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component("yfinanceDataRepository")
@AllArgsConstructor
@Slf4j
public class YfinanceDataRepository implements StockDataRepository<OhlcBar> {

    private final YfinanceBarToDataEntityMapper yfinanceBarToDataEntityMapper;

    private final StockDataEntityToOhlcBarMapper stockDataEntityToOhlcBarMapper;

    private final YfinanceDataClient yfinanceDataClient;

    @Override
    public List<OhlcBar> findStockDataBySymbolAndDates(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        return yfinanceDataClient.getStocksMarketData(symbol, start, end, interval).stream()
                .map(quote -> yfinanceBarToDataEntityMapper.map(quote, interval))
                .map(stockDataEntityToOhlcBarMapper::map)
                .collect(Collectors.toList());
    }
}
