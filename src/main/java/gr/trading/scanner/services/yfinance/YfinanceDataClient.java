package gr.trading.scanner.services.yfinance;

import com.google.common.util.concurrent.RateLimiter;
import gr.trading.scanner.configuration.YfinanceDataConfigProperties;
import gr.trading.scanner.model.Interval;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class YfinanceDataClient {

    private final RateLimiter rateLimiter;

    public YfinanceDataClient(YfinanceDataConfigProperties properties) {
        this.rateLimiter = RateLimiter.create(properties.getRequestRate());
    }

    public List<HistoricalQuote> getStocksMarketData(String symbol, LocalDateTime startDate, LocalDateTime endDate, Interval interval) {
        spendQuota(1);

        try {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(Date.from(startDate.toInstant(ZoneOffset.UTC)));

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(Date.from(endDate.toInstant(ZoneOffset.UTC)));

            return YahooFinance.get(symbol, startCalendar, endCalendar, interval.getYfinanceInterval()).getHistory();
        } catch (IOException e) {
            log.warn("Could not retrieve stock data for {}", symbol);
        }

        return Collections.emptyList();
    }

    private synchronized void spendQuota(int quota) {
        rateLimiter.acquire(quota);
    }
}
