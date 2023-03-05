package gr.trading.scanner.model;

import lombok.Getter;

@Getter
public enum Interval {

    M5("5 mins"),
    D1("1 day");

    private String twsInterval;

    Interval(String twsInterval) {
        this.twsInterval = twsInterval;
    }
}
