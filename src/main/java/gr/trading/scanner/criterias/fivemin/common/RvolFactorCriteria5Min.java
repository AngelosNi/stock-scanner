package gr.trading.scanner.criterias.fivemin.common;

import gr.trading.scanner.criterias.fivemin.OhlcPlus5MinBarCriteria;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Qualifier("CommonCriteria")
@Slf4j
public class RvolFactorCriteria5Min implements OhlcPlus5MinBarCriteria {

    private final DateTimeUtils dateTimeUtils;

    @Override
    public boolean apply(List<OhlcPlusBar> d1Bars, List<OhlcPlusBar> min5Bars) throws NoRecentDataException {
        // Recent data are considered those in the last 10 minutes
        if (min5Bars.get(min5Bars.size() - 1).getTime().isBefore(dateTimeUtils.getNowDayTime().minusMinutes(30))) {
            throw new NoRecentDataException("No recent data available");
        }
        if (min5Bars.get(min5Bars.size() - 1).getAverageCumulativeVolumeAcrossDays() == null) {
            log.warn("AverageCumulativeVolumeAcrossDays was null");
        }
        return min5Bars.get(min5Bars.size() - 1).getCumulativeVolume().compareTo(min5Bars.get(min5Bars.size() - 1).getAverageCumulativeVolumeAcrossDays() * 0.8) > 0;
    }
}
