package gr.trading.scanner.criterias;

import gr.trading.scanner.model.OhlcPlusBar;

import java.util.List;

public interface OhlcPlus5MinBarCriteria {

    boolean apply(List<OhlcPlusBar> d1Bars, List<OhlcPlusBar> min5Bars) throws NoRecentDataException;

    class NoRecentDataException extends Exception {
        public NoRecentDataException() {
        }

        public NoRecentDataException(String message) {
            super(message);
        }

        public NoRecentDataException(String message, Throwable cause) {
            super(message, cause);
        }

        public NoRecentDataException(Throwable cause) {
            super(cause);
        }

        public NoRecentDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
