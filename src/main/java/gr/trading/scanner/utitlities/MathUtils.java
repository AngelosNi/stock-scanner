package gr.trading.scanner.utitlities;

import org.apache.commons.math.stat.StatUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MathUtils {

    public double getAverage(List<BigDecimal> points) {
        return StatUtils.mean(points.stream().mapToDouble(BigDecimal::doubleValue).toArray());
    }

    public double getAverageFromDoubles(List<Double> points) {
        return StatUtils.mean(points.stream().mapToDouble(a -> a).toArray());
    }
}
