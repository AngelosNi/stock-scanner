package gr.trading.scanner.criterias.daily.common;

import gr.trading.scanner.criterias.daily.OhlcPlusDailyBarCriteria;
import gr.trading.scanner.model.OhlcPlusBar;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Qualifier("CommonCriteria")
public class DailyVolumeCriteria implements OhlcPlusDailyBarCriteria {

    private final double HEAVY_VOLUME_MULTIPLIER_THRESHOLD = 1.5;

    @Override
    public boolean apply(List<OhlcPlusBar> d1Bars) {
        Optional<OhlcPlusBar> lastDay = Optional.ofNullable(d1Bars.get(d1Bars.size() - 1));
        if (lastDay.get().getAverageVolumeAcrossDays() == null) {
            System.out.println("ds");
        }

        return lastDay.map(ld -> ld.getVolume() > HEAVY_VOLUME_MULTIPLIER_THRESHOLD * ld.getAverageVolumeAcrossDays())
                .orElse(false);
    }
}
