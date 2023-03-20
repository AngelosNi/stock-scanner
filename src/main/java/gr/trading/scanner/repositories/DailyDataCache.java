package gr.trading.scanner.repositories;

import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.entities.DataEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DailyDataCache {

    private Map<String, Map<DataEntity.Id, DataEntity>> dailyDataCache;

    public void initCache(List<DataEntity> entities) {
        dailyDataCache = entities.stream()
                .collect(Collectors.groupingBy(de -> de.getId().getSymbol(), Collectors.toList()))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> listToMap(entry.getValue())));

    }

    public Optional<List<DataEntity>> getSymbolDataEntities(String symbol, Interval interval, LocalDateTime start) {
        List<DataEntity> list = dailyDataCache.getOrDefault(symbol, Map.of()).entrySet().stream()
                .filter(entry -> entry.getKey().getBarInterval().equals(interval) && (entry.getKey().getBarDateTime().isAfter(start) || entry.getKey().getBarDateTime().equals(start)))
                .map(Map.Entry::getValue)
                .sorted((b1, b2) -> {
                    if (b1.getId().getBarDateTime().isAfter(b2.getId().getBarDateTime())) {
                        return 1;
                    } else if (b1.getId().getBarDateTime().isBefore(b2.getId().getBarDateTime())) {
                        return -1;
                    } else {
                        return 0;
                    }
                })
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(list);
    }

    private Map<DataEntity.Id, DataEntity> listToMap(List<DataEntity> dataEntities) {
        return dataEntities.stream()
                .collect(Collectors.toMap(DataEntity::getId, de -> de));
    }
}
