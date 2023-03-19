package gr.trading.scanner.controllers;

import gr.trading.scanner.repositories.tickers.TickersRepository;
import gr.trading.scanner.services.ParallelExecutor;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@AllArgsConstructor
@Slf4j
public class StockDataController {

    private final ParallelExecutor parallelExecutor;

    private final DateTimeUtils dateTimeUtils;

    private final TickersRepository tickersRepository;

    @GetMapping("/stock")
    public List<Map<String, List<String>>> getSymbols() throws ExecutionException, InterruptedException, IOException {
        long startTime = System.nanoTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse("2023-02-20", formatter);

        List<String> symbols = tickersRepository.findAll();

        List<Map<String, List<String>>> filteredSymbols = parallelExecutor.findSymbolsByCriteriaParallel(symbols, dateTimeUtils.subtractDaysSkippingWeekends(LocalDate.now().atTime(9, 30), 10), dateTimeUtils.getNowDayTime().minusHours(7));

        long elapsedTime = System.nanoTime() - startTime;
        log.info("Time elapsed (ms): {}", elapsedTime / 1000000);

        return filteredSymbols;
    }
}
