package gr.trading.scanner.services;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@AllArgsConstructor
public class ParallelExecutor {

    private final SymbolHandlerExecutor symbolHandlerExecutor;

    public List<Map<String, List<String>>> findSymbolsByCriteriaParallel(List<String> symbols, LocalDateTime start, LocalDateTime end) throws ExecutionException, InterruptedException {

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
