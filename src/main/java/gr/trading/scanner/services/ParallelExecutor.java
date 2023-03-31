package gr.trading.scanner.services;

import com.google.common.collect.Lists;
import gr.trading.scanner.model.Interval;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ParallelExecutor {

    private final SymbolHandlerExecutor symbolHandlerExecutor;

    public Map<String, List<String>> findSymbolsByCriteriaParallel(List<String> symbols, LocalDateTime start, Interval interval) throws ExecutionException, InterruptedException {
        List<Future<Map<String, List<String>>>> tasks = new ArrayList<>();
        for (List<String> symbolsSubList : Lists.partition(symbols, 10)) {
            tasks.add(symbolHandlerExecutor.findSymbolsByCriteria(symbolsSubList, start, interval));
        }

        List<Map<String, List<String>>> bars = new ArrayList<>();

        for (Future<Map<String, List<String>>> task : tasks) {
            bars.add(task.get());
        }

        return bars.stream()
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (l1, l2) -> {
                            List<String> l = new ArrayList<>(l1);
                            l.addAll(l2);
                            return l;
                        }));
    }
}
