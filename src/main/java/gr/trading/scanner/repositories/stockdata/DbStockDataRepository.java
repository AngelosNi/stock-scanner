package gr.trading.scanner.repositories.stockdata;

import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.entities.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DbStockDataRepository extends JpaRepository<DataEntity, DataEntity.Id> {

    List<DataEntity> findByIdSymbolAndIdBarInterval(String symbol, Interval interval);
}
