package gr.trading.scanner.services.twelvedata;

import gr.trading.scanner.model.TwelveDataResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
@Slf4j
public class TwelveDataClient {

    private WebClient twelveDataWebClient;

    public TwelveDataResponseDto getStocksMarketData(List<String> symbols, LocalDateTime startDate, LocalDateTime endDate, String interval) throws ExecutionException, InterruptedException {
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
}
