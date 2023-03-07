package gr.trading.scanner.repositories;

import java.io.IOException;
import java.util.List;

public interface TickersRepository {

    List<String> findAll() throws IOException;
}
