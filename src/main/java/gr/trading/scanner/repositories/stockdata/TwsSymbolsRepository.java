package gr.trading.scanner.repositories.stockdata;

import com.ib.client.Bar;
import com.ib.client.Contract;
import gr.trading.scanner.mappers.BarToOhlcBarMapper;
import gr.trading.scanner.model.Interval;
import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.services.tws.TwsMessageHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

//@Component
@Slf4j
@AllArgsConstructor
public class TwsSymbolsRepository implements StockDataRepository<OhlcBar> {

    private TwsMessageHandler twsMessageHandler;

    private BarToOhlcBarMapper barToOhlcBarMapper;

    @Override
    public List<OhlcBar> findStockBySymbolAndDates(String symbol, LocalDateTime start, LocalDateTime end, Interval interval) {
        Contract contract = new Contract();
        contract.symbol(symbol);
        contract.secType("STK");
        contract.currency("USD");
        contract.exchange("SMART");

        try {
            List<Bar> bars = twsMessageHandler.reqHistoricalData(contract, end.format(DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss")), "10 D", interval.getTwsInterval());
            return bars.stream()
                    .map(barToOhlcBarMapper::map)
                    .collect(Collectors.toList());
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Error Retrieving data.");
        }
    }
}
