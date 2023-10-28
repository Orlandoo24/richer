package com.astrosea.richer.constant;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeConstant {
    public static final ZonedDateTime OG_BEGIN = ZonedDateTime.of(2023, 9, 1, 17, 0, 0, 0, ZoneId.of("UTC-5"));

    public static final ZonedDateTime OG_END = ZonedDateTime.of(2023, 10, 1, 17, 59, 59, 0, ZoneId.of("UTC-5"));

    public static final ZonedDateTime WL_BEGIN = ZonedDateTime.of(2023, 10, 1, 18, 0, 0, 0, ZoneId.of("UTC-5"));

    public static final ZonedDateTime WL_END = ZonedDateTime.of(2023, 10, 1, 18, 59, 59, 0, ZoneId.of("UTC-5"));

    public static final ZonedDateTime PUB_BEGIN = ZonedDateTime.of(2023, 11, 1, 19, 0, 0, 0, ZoneId.of("UTC-5"));

    public static final String OG_TIME = "OG_TIME";

    public static final String WL_TIME = "WL_TIME";

    public static final String PUB_TIME = "PUB_TIME";

}
