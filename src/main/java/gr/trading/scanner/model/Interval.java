package gr.trading.scanner.model;

import lombok.Getter;

@Getter
public enum Interval {

    M5("5 mins", "5min"),
    D1("1 day", "1day");

    private String twsInterval;

    private String twelveDataInterval;

    Interval(String twsInterval, String twelveDataInterval) {
        this.twsInterval = twsInterval;
        this.twelveDataInterval = twelveDataInterval;
    }
}
