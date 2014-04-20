package org.smart4j.plugin.cache;

public interface Expiry {

    long ETERNAL = -1;
    long ZERO = 0;
    long ONE_MINUTE = 60 * 1000;
    long FIVE_MINUTES = 5 * ONE_MINUTE;
    long TEN_MINUTES = 10 * ONE_MINUTE;
    long TWENTY_MINUTES = 20 * ONE_MINUTE;
    long THIRTY_MINUTES = 30 * ONE_MINUTE;
    long ONE_HOUR = 60 * ONE_MINUTE;
    long ONE_DAY = 24 * ONE_HOUR;
}
