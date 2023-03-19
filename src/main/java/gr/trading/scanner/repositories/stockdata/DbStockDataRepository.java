package gr.trading.scanner.repositories.stockdata;

import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.entities.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DbStockDataRepository extends JpaRepository<DataEntity, DataEntity.Id> {

    List<DataEntity> findByIdSymbolAndIdBarIntervalAndIdBarDateTimeGreaterThanEqual(String symbol, Interval interval, LocalDateTime start);

    @Query("select de from DataEntity de" +
            " where de.id.symbol in (:symbols)" +
            " and de.id.barInterval = :interval" +
            " and de.id.barDateTime >= :start")
    List<DataEntity> findBySymbolsAndIntervalAndStartDate(@Param("symbols") List<String> symbols, @Param("interval") Interval interval, @Param("start") LocalDateTime start);
}
