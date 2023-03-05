package gr.trading.scanner.criterias;

import gr.trading.scanner.model.OhlcBar;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.ta.TaTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentRangeUnderAtrCriteriaTest {

    @Mock
    private TaTools taTools;

    @InjectMocks
    private CurrentRangeUnderAtrCriteria criteria;

    private OhlcBar beforeYesterdayOhlcBar = OhlcBar.builder()
            .close(52.1123)
            .high(78.12)
            .low(49.456)
            .open(68.1233)
            .time(LocalDateTime.now().minusDays(2))
            .build();

    private OhlcBar yesterdayOhlcBar = OhlcBar.builder()
            .close(52.2123)
            .high(78.12)
            .low(49.456)
            .open(68.1233)
            .time(LocalDateTime.now().minusDays(1))
            .build();

    private OhlcBar todayOhlcBar = OhlcBar.builder()
            .close(52.3123)
            .high(78.12)
            .low(49.456)
            .open(68.1233)
            .time(LocalDateTime.now())
            .build();

    private OhlcBar last5mOhlcBar = OhlcBar.builder()
            .close(60.3123)
            .high(78.12)
            .low(49.456)
            .open(68.1233)
            .time(LocalDateTime.now())
            .build();

    @BeforeEach
    void setup() {
        criteria = new CurrentRangeUnderAtrCriteria(new TaTools());
    }

    @Test
    void applyTestSuccess() throws OhlcPlusBarCriteria.NoRecentDataException {
        List<OhlcPlusBar> d1Bars = List.of(new OhlcPlusBar(beforeYesterdayOhlcBar), new OhlcPlusBar(yesterdayOhlcBar), new OhlcPlusBar(todayOhlcBar));
        List<OhlcPlusBar> m5Bars = List.of(new OhlcPlusBar(last5mOhlcBar));

        when(taTools.calculateAtr(anyList())).thenReturn(10.0);

        assert criteria.apply(d1Bars, m5Bars);
    }

    @Test
    void applyTestFail() throws OhlcPlusBarCriteria.NoRecentDataException {
        List<OhlcPlusBar> d1Bars = List.of(new OhlcPlusBar(beforeYesterdayOhlcBar), new OhlcPlusBar(yesterdayOhlcBar), new OhlcPlusBar(todayOhlcBar));
        List<OhlcPlusBar> m5Bars = List.of(new OhlcPlusBar(last5mOhlcBar));

        when(taTools.calculateAtr(anyList())).thenReturn(8.0);

        assert criteria.apply(d1Bars, m5Bars);
    }

    @Test
    void getCloseOfPreviousDayTest() {
        List<OhlcPlusBar> bars = List.of(new OhlcPlusBar(beforeYesterdayOhlcBar), new OhlcPlusBar(yesterdayOhlcBar), new OhlcPlusBar(todayOhlcBar));
        assert criteria.getCloseOfPreviousDay(bars) != 52.1123;
        assert criteria.getCloseOfPreviousDay(bars) != 52.3123;

        assert criteria.getCloseOfPreviousDay(bars) == 52.2123;
    }

}