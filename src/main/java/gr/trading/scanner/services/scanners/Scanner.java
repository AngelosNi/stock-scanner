package gr.trading.scanner.services.scanners;

import java.time.LocalDateTime;
import java.util.List;

public interface Scanner {

    List<String> filterBullish(List<String> symbols, LocalDateTime start);

    List<String> filterBearish(List<String> symbols, LocalDateTime start);
}
