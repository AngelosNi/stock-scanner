package gr.trading.scanner.enhancers;

import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.utitlities.DateTimeUtils;
import gr.trading.scanner.utitlities.MathUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class OhlcBarsAvgVolumeEnhancerTest {

    private OhlcBarsAvgVolumeEnhancer enhancer = new OhlcBarsAvgVolumeEnhancer(new MathUtils(), new DateTimeUtils());

    LocalDateTime start = LocalDate.now().atTime(9, 35);

    private List<OhlcBar> testBars = new ArrayList<>();

    @BeforeEach
    void setup() {
        final int DAYS = 15;
        final int MIN_5_BARS = 78;

        double initClose = 52.1123;
        double initHigh = 78.12;
        double initLow = 49.456;
        double initOpen = 68.1233;
        BigDecimal volume = new BigDecimal("5000.2");

        LocalDateTime now = start;
        for (int day = 0; day < DAYS; day++) {
            for (int bar = 0; bar < MIN_5_BARS; bar++) {
                volume = volume.add(BigDecimal.ONE);
                testBars.add(OhlcPlusBar.builder().close(initClose++)
                        .high(initHigh++)
                        .low(initLow++)
                        .open(initOpen++)
                        .volume(volume)
                        .time(now)
                        .build());
                now = now.plusMinutes(5);
            }
            now = now.minusDays(1).toLocalDate().atTime(LocalTime.of(9, 35));
        }
    }

    @Test
    void enhanceTest() {
        List<OhlcPlusBar> plusBars = testBars.stream()
                .map(OhlcPlusBar::new)
                .collect(Collectors.toList());
        enhancer.enhance(plusBars);
    }

}