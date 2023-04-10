package gr.trading.scanner.mappers;

import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.entities.DataEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OhlcBarToStockDataEntityMapper {

    public DataEntity map(OhlcBar ohlcBar, Interval interval) {
        DataEntity dataEntity = new DataEntity();

        dataEntity.setId(new DataEntity.Id(ohlcBar.getSymbol(), ohlcBar.getTime(), interval));
        dataEntity.setClosePrice(ohlcBar.getClose());
        dataEntity.setHighPrice(ohlcBar.getHigh());
        dataEntity.setVolume(ohlcBar.getVolume());
        dataEntity.setLowPrice(ohlcBar.getLow());

        return dataEntity;
    }
}
