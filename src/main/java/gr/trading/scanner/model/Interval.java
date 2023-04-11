package gr.trading.scanner.model;

import lombok.Getter;

@Getter
public enum Interval {

    M5("5min", yahoofinance.histquotes.Interval.FIVEMIN),
    D1("1day", yahoofinance.histquotes.Interval.DAILY);

    private String twelveDataInterval;

    private yahoofinance.histquotes.Interval yfinanceInterval;

    Interval(String twelveDataInterval, yahoofinance.histquotes.Interval yfinanceInterval) {
        this.twelveDataInterval = twelveDataInterval;
        this.yfinanceInterval = yfinanceInterval;
    }
}
