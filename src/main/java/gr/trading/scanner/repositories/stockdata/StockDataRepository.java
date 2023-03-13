package gr.trading.scanner.repositories.stockdata;

import gr.trading.scanner.model.Interval;

import java.time.LocalDateTime;
import java.util.List;

public interface StockDataRepository<T> {

    List<T> findStockBySymbolAndDates(String symbol, LocalDateTime start, LocalDateTime end, Interval interval);
}
