package gr.trading.scanner.repositories;

import gr.trading.scanner.model.Interval;

import java.time.LocalDateTime;
import java.util.List;

public interface SymbolsRepository<T> {

    List<T> findStockBySymbolAndDates(String symbol, LocalDateTime start, LocalDateTime end, Interval interval);
}
