package gr.trading.scanner.services;

import com.google.common.collect.Lists;
import gr.trading.scanner.model.Interval;
import gr.trading.scanner.repositories.DailyDataCache;
import gr.trading.scanner.repositories.stockdata.DbStockDataRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@AllArgsConstructor
@Slf4j
public class ParallelExecutor {

    private final SymbolHandlerExecutor symbolHandlerExecutor;

    private DbStockDataRepository dbStockDataRepository;

    private DailyDataCache dailyDataCache;

    public List<Map<String, List<String>>> findSymbolsByCriteriaParallel(List<String> symbols, LocalDateTime start, LocalDateTime end) throws ExecutionException, InterruptedException {
        dailyDataCache.initCache(dbStockDataRepository.findBySymbolsAndIntervalAndStartDate(symbols, Interval.D1, start.toLocalDate().atStartOfDay()));
        log.info("Cache filled");

        List<Future<Map<String, List<String>>>> tasks = new ArrayList<>();
        for (List<String> symbolsSubList : Lists.partition(symbols, 10)) {
            tasks.add(symbolHandlerExecutor.findSymbolsByCriteria(symbolsSubList, start, end));
        }

        List<Map<String, List<String>>> bars = new ArrayList<>();

        for (Future<Map<String, List<String>>> task : tasks) {
            bars.add(task.get());
        }

        return bars;
    }
}
