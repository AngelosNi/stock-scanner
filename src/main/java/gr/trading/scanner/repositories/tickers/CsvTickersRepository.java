package gr.trading.scanner.repositories.tickers;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import gr.trading.scanner.model.TickerInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CsvTickersRepository implements TickersRepository {

    @Value("classpath:tickers.csv")
    private Resource csvResource;

    private final CsvMapper mapper = new CsvMapper();

    @Override
    public List<String> findAll() throws IOException {
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        MappingIterator<TickerInfo> it = mapper.readerFor(TickerInfo.class)
                .with(schema)
                .readValues(csvResource.getFile());

        List<TickerInfo> tickers = it.readAll();

        return tickers.stream()
                .map(TickerInfo::getSymbol)
                .collect(Collectors.toList());
    }
}
