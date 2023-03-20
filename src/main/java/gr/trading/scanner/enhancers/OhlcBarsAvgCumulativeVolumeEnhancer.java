package gr.trading.scanner.enhancers;

import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class OhlcBarsAvgCumulativeVolumeEnhancer implements OhlcBarEnhanceable {

    private static final int DAYS_TO_INCLUDE_FOR_5_MIN_AVG = 10;

    private final DateTimeUtils dateTimeUtils;

    @Override
    public List<OhlcPlusBar> enhance(List<OhlcPlusBar> bars) {
        List<OhlcPlusBar> cVolEnhancedBars = splitByDays(bars).values().stream()
                .map(this::enhanceWithCVol)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return enhanceWithDaysCumulativeAverageVolume(cVolEnhancedBars);
    }

    private List<OhlcPlusBar> enhanceWithCVol(List<OhlcPlusBar> bars) {
        List<OhlcPlusBar> orderedBars = bars.stream()
                .sorted((b1, b2) -> {
                    if (b1.getTime().isAfter(b2.getTime())) {
                        return 1;
                    } else if (b1.getTime().isBefore(b2.getTime())) {
                        return -1;
                    } else {
                        return 0;
                    }
                })
                .collect(Collectors.toList());

        // Init 1st element
        orderedBars.get(0).setCumulativeVolume(orderedBars.get(0).getVolume());
        for (int i = 1; i < bars.size(); i++) {
            orderedBars.get(i).setCumulativeVolume(orderedBars.get(i - 1).getCumulativeVolume().add(orderedBars.get(i).getVolume()));
        }

        return orderedBars;
    }

    private List<OhlcPlusBar> enhanceWithDaysCumulativeAverageVolume(List<OhlcPlusBar> bars) {
        List<OhlcPlusBar> barsCopy = new ArrayList<>(bars);
        barsCopy.stream()
                .filter(bar -> bar.getTime().isAfter(dateTimeUtils.getNowDay()))    // Get today's dailyBars
                .forEach(bar -> bar.setAverageCumulativeVolumeAcrossDays(calculateAvgCumulativeFromPreviousDays(bars, bar)));

        return barsCopy;
    }

    private BigDecimal calculateAvgCumulativeFromPreviousDays(List<OhlcPlusBar> bars, OhlcBar forBar) {
        return BigDecimal.valueOf(bars.stream()
                .filter(bar -> bar.getTime().isAfter(dateTimeUtils.subtractDaysSkippingWeekends(dateTimeUtils.getNowDay(), DAYS_TO_INCLUDE_FOR_5_MIN_AVG))) // Only include N previous days for average calculations
                .filter(bar -> bar.getTime().toLocalTime().equals(forBar.getTime().toLocalTime()))      // Only include the specific time of "forBar" from the days included
                .map(OhlcPlusBar::getCumulativeVolume)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElseThrow());
    }

    private Map<LocalDate, List<OhlcPlusBar>> splitByDays(List<OhlcPlusBar> bars) {
        return bars.stream()
                .collect(Collectors.groupingBy(bar -> bar.getTime().toLocalDate()));
    }
}
