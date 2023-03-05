package gr.trading.scanner.criterias;

import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.ta.TaTools;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

@Component
@AllArgsConstructor
public class CurrentRangeUnderAtrCriteria implements OhlcPlusBarCriteria {

    private final TaTools taTools;

    @Override
    public boolean apply(List<OhlcPlusBar> d1Bars, List<OhlcPlusBar> min5Bars) throws NoRecentDataException {
        // Recent data are considered those in the last 10 minutes
        if (min5Bars.get(min5Bars.size() - 1).getTime().isBefore(LocalDateTime.now().minusMinutes(10))) {
            throw new NoRecentDataException("No recent data available");
        }
        double currentRange = abs(getCloseOfPreviousDay(d1Bars) - min5Bars.get(min5Bars.size() - 1).getClose());

        return currentRange < taTools.calculateAtr(d1Bars);
    }

    public double getCloseOfPreviousDay(List<OhlcPlusBar> d1Bars) {
        return d1Bars.stream()
                .filter(bar -> bar.getTime().isAfter(LocalDate.now().atStartOfDay().minusDays(1)))
                .collect(Collectors.toList())
                .get(0)
                .getClose();
    }
}
