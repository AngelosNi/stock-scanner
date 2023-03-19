package gr.trading.scanner.model;

import lombok.Getter;

@Getter
public enum Interval {

    M5("5min"),
    D1("1day");

    private String twelveDataInterval;

    Interval(String twelveDataInterval) {
        this.twelveDataInterval = twelveDataInterval;
    }
}
