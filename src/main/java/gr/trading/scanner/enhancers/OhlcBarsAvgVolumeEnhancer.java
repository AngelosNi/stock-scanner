package gr.trading.scanner.enhancers;

import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.utitlities.MathUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Component
@AllArgsConstructor
public class OhlcBarsAvgVolumeEnhancer implements OhlcBarEnhanceable {

    private static final int WINDOW_SIZE_5_MIN_ROLL_AVG = 10;

    private static final int DAYS_TO_INCLUDE_FOR_5_MIN_AVG = 2;

    private MathUtils mathUtils;

    @Override
    public List<OhlcPlusBar> enhance(List<OhlcPlusBar> bars) {
        List<OhlcPlusBar> raEnhancedBars = splitByDays(bars).values().stream()
                .map(this::enhanceWithRaVolume)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return enhanceWithDaysAverageVolume(raEnhancedBars);
    }

    private List<OhlcPlusBar> enhanceWithDaysAverageVolume(List<OhlcPlusBar> bars) {
        List<OhlcPlusBar> barsCopy = new ArrayList<>(bars);
        barsCopy.stream()
                .filter(bar -> bar.getTime().isAfter(LocalDate.now().atStartOfDay()))    // Get today's bars
                .forEach(bar -> bar.setDaysAverageVolume(calculateAvgsFromPreviousDays(bars, bar)));

        return barsCopy;
    }

    private double calculateAvgsFromPreviousDays(List<OhlcPlusBar> bars, OhlcBar forBar) {
        return bars.stream()
                .filter(bar -> bar.getTime().isAfter(LocalDate.now().atStartOfDay().minusDays(OhlcBarsAvgVolumeEnhancer.DAYS_TO_INCLUDE_FOR_5_MIN_AVG))) // Only include N previous days for average calculations
                .filter(bar -> bar.getTime().toLocalTime().equals(forBar.getTime().toLocalTime()))      // Only include the specific time of "forBar" from the days included
                .map(OhlcPlusBar::getRaAverageVolume)
                .mapToDouble(a -> a)
                .average()
                .orElseThrow();
    }

    private List<OhlcPlusBar> enhanceWithRaVolume(List<OhlcPlusBar> bars) {
        List<OhlcPlusBar> plusBars = new ArrayList<>();
        for (int i = 0; i < bars.size(); i++) {
            List<BigDecimal> windowedCloses = bars.subList(max(0, i - WINDOW_SIZE_5_MIN_ROLL_AVG / 2), min(i + WINDOW_SIZE_5_MIN_ROLL_AVG / 2, bars.size()))
                    .stream()
                    .map(OhlcBar::getVolume)
                    .collect(Collectors.toList());
            OhlcPlusBar ohlcPlusBar = new OhlcPlusBar((bars.get(i)));
            ohlcPlusBar.setRaAverageVolume(mathUtils.getAverage(windowedCloses));
            plusBars.add(i, ohlcPlusBar);
        }

        return plusBars;
    }

    private Map<LocalDate, List<OhlcPlusBar>> splitByDays(List<OhlcPlusBar> bars) {
        return bars.stream()
                .collect(Collectors.groupingBy(bar -> bar.getTime().toLocalDate()));
    }
}
