package gr.trading.scanner.repositories.stockdata;

import gr.trading.scanner.model.Interval;

import java.time.LocalDateTime;
import java.util.List;

public class YfinanceDataRepository implements StockDataRepository {

    @Override
    public List findStockDataBySymbolAndDates(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        return null;
    }
}
