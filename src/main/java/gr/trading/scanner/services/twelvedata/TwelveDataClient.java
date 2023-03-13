package gr.trading.scanner.services.twelvedata;

import com.google.common.util.concurrent.RateLimiter;
import gr.trading.scanner.configuration.TwelveDataConfigProperties;
import gr.trading.scanner.model.TwelveDataResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class TwelveDataClient {

    private final WebClient twelveDataWebClient;

    private final RateLimiter rateLimiter;

    public TwelveDataClient(WebClient twelveDataWebClient, TwelveDataConfigProperties properties) {
        this.twelveDataWebClient = twelveDataWebClient;

        this.rateLimiter = RateLimiter.create(properties.getRequestRate());
    }

    public TwelveDataResponseDto getStocksMarketData(List<String> symbols, LocalDateTime startDate, LocalDateTime endDate, String interval) throws ExecutionException, InterruptedException {
        spendQuota(symbols.size());

        return twelveDataWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("time_series")
                        .queryParam("symbol", symbols)
                        .queryParam("interval", interval)
                        .queryParam("start_date", startDate)
                        .queryParam("end_date", endDate)
                        .build())
                .retrieve()
                .toEntity(TwelveDataResponseDto.class).subscribeOn(Schedulers.boundedElastic())
                .toFuture()
                .get()
                .getBody();
    }

    private synchronized void spendQuota(int quota) {
        rateLimiter.acquire(quota);
    }
}
