package gr.trading.scanner.mappers;

import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.entities.DataEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import yahoofinance.histquotes.HistoricalQuote;

import java.time.LocalDateTime;

@Component
@Slf4j
public class YfinanceBarToDataEntityMapper {

    public DataEntity map(HistoricalQuote bar, Interval interval) {
        DataEntity entity = new DataEntity();

        entity.setClosePrice(bar.getClose().doubleValue());
        entity.setHighPrice(bar.getHigh().doubleValue());
        entity.setOpenPrice(bar.getOpen().doubleValue());
        entity.setLowPrice(bar.getLow().doubleValue());
        entity.setVolume(bar.getVolume());

        LocalDateTime localDateTime = LocalDateTime.ofInstant(bar.getDate().toInstant(), bar.getDate().getTimeZone().toZoneId());

        entity.setId(new DataEntity.Id(bar.getSymbol(), localDateTime, interval));

        return entity;
    }
}
