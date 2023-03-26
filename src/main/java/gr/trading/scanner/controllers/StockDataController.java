package gr.trading.scanner.controllers;

import gr.trading.scanner.model.Interval;
import gr.trading.scanner.repositories.tickers.TickersRepository;
import gr.trading.scanner.services.ParallelExecutor;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
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

    @GetMapping("/stocks/5min")
    public Map<String, List<String>> getSymbols() throws ExecutionException, InterruptedException, IOException {
        long startTime = System.nanoTime();

        List<String> symbols = tickersRepository.findAll();

        Map<String, List<String>> filteredSymbols = parallelExecutor.findSymbolsByCriteriaParallel(symbols, dateTimeUtils.subtractDaysSkippingWeekends(LocalDate.now().atTime(9, 30), 300), dateTimeUtils.getNowDayTime().minusHours(7), Interval.M5);

        writeToFile(filteredSymbols.get("Bullish"), "bullish");
        writeToFile(filteredSymbols.get("Bearish"), "bearish");

        long elapsedTime = System.nanoTime() - startTime;
        log.info("Time elapsed (ms): {}", elapsedTime / 1000000);

        return filteredSymbols;
    }

    @GetMapping("/stocks/daily")
    public Map<String, List<String>> getDailySymbols() throws ExecutionException, InterruptedException, IOException {
        long startTime = System.nanoTime();

        List<String> symbols = tickersRepository.findAll();

        Map<String, List<String>> filteredSymbols = parallelExecutor.findSymbolsByCriteriaParallel(symbols, dateTimeUtils.subtractDaysSkippingWeekends(LocalDate.now().atTime(9, 30), 300), dateTimeUtils.getNowDayTime().minusHours(7), Interval.D1);

        writeToFile(filteredSymbols.get("Bullish"), "bullish");
        writeToFile(filteredSymbols.get("Bearish"), "bearish");

        long elapsedTime = System.nanoTime() - startTime;
        log.info("Time elapsed (ms): {}", elapsedTime / 1000000);

        return filteredSymbols;
    }

    private void writeToFile(List<String> symbols, String fileName) throws IOException {
        try (FileWriter outputfile = new FileWriter("C:\\Users\\angel\\Downloads\\" + fileName + ".txt")) {
            outputfile.write(String.join(",", symbols));
        }
    }
}
