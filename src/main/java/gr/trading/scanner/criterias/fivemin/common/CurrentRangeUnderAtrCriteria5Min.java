package gr.trading.scanner.criterias.fivemin.common;

import gr.trading.scanner.criterias.fivemin.OhlcPlus5MinBarCriteria;
import gr.trading.scanner.model.OhlcPlusBar;
import gr.trading.scanner.ta.TaTools;
import gr.trading.scanner.utitlities.DateTimeUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Math.abs;

@Component
@Qualifier("CommonCriteria")
@AllArgsConstructor
public class CurrentRangeUnderAtrCriteria5Min implements OhlcPlus5MinBarCriteria {

    private final TaTools taTools;

    private final DateTimeUtils dateTimeUtils;

    @Override
    public boolean apply(List<OhlcPlusBar> d1Bars, List<OhlcPlusBar> min5Bars) throws NoRecentDataException {
        // Recent data are considered those in the last 10 minutes
        if (min5Bars.get(min5Bars.size() - 1).getTime().isBefore(dateTimeUtils.getNowDayTime().minusMinutes(30))) {
            throw new NoRecentDataException("No recent data available");
        }
        double currentRange = abs(getCloseOfPreviousDay(d1Bars) - min5Bars.get(min5Bars.size() - 1).getClose());

        return currentRange < 1.2 * taTools.calculateAtr(d1Bars);
    }

    public double getCloseOfPreviousDay(List<OhlcPlusBar> d1Bars) {
        return d1Bars.stream()
                .filter(bar -> bar.getTime().equals(dateTimeUtils.subtractDaysSkippingWeekends(dateTimeUtils.getNowDayAtSessionStart(), 1)))
                .findFirst()
                .get()
                .getClose();
    }
}
