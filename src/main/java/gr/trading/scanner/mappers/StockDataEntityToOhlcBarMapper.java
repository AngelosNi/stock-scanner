package gr.trading.scanner.mappers;

import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.entities.DataEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StockDataEntityToOhlcBarMapper {

    public OhlcBar map(DataEntity dataEntity) {
        OhlcBar ohlcBar = new OhlcBar();

        ohlcBar.setClose(dataEntity.getClosePrice());
        ohlcBar.setHigh(dataEntity.getHighPrice());
        ohlcBar.setOpen(dataEntity.getOpenPrice());
        ohlcBar.setLow(dataEntity.getLowPrice());
        ohlcBar.setVolume(dataEntity.getVolume());
        ohlcBar.setTime(dataEntity.getId().getBarDateTime());

        return ohlcBar;
    }
}
