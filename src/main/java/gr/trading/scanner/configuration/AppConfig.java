package gr.trading.scanner.configuration;

import gr.trading.scanner.model.Interval;
import gr.trading.scanner.repositories.DailyDataCache;
import gr.trading.scanner.repositories.stockdata.DbStockDataRepository;
import gr.trading.scanner.repositories.tickers.TickersRepository;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class AppConfig {

    private static final int MAX_DAYS_TO_CACHE = 300;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.initialize();
        return executor;
    }

    @Bean
    DailyDataCache dailyDataCache(DbStockDataRepository dbStockDataRepository, TickersRepository tickersRepository, DateTimeUtils dateTimeUtils) throws IOException {
        log.info("Initializing Cache");

        DailyDataCache cache = new DailyDataCache();
        cache.initCache(dbStockDataRepository.findBySymbolsAndIntervalAndStartDate(tickersRepository.findAll(), Interval.D1, dateTimeUtils.subtractDaysSkippingWeekends(dateTimeUtils.getNowDayAtSessionStart(), MAX_DAYS_TO_CACHE)));

        log.info("Cache initialized and filled");
        return cache;
    }

}
