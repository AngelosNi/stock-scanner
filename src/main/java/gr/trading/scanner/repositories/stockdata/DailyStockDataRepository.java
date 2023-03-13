package gr.trading.scanner.repositories.stockdata;

import gr.trading.scanner.model.entities.DailyDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyStockDataRepository extends JpaRepository<DailyDataEntity, DailyDataEntity.SymbolDateId> {

    List<DailyDataEntity> findBySymbolDateIdSymbol(String symbol);
}
