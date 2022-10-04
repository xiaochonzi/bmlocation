package com.zy.bmlocation;

public class Constants {
    public static final String RESULT_KEY = "result";
    public static final String ERROR_KEY = "errorCode";

    /**
     * channel名
     */
    public static class MethodChannelName {

        /**
         * channel
         */
        public static final String LOCATION_CHANNEL = "bmlocation_channel";
        public static final String LOCATION_STREAM = "bmlocation_location_stream";
    }

    /**
     * Method名
     */
    public static class MethodID {

        public static final String LOCATION_SETAGREEPRIVACY = MethodChannelName.LOCATION_CHANNEL +
                "/setAgreePrivacy";

        public static final String LOCATION_SETOPTIONS = MethodChannelName.LOCATION_CHANNEL +
                "/setOptions";

        public static final String LOCATION_SERIESLOC = MethodChannelName.LOCATION_CHANNEL +
                "/seriesLocation";

        public static final String LOCATION_STOPLOC = MethodChannelName.LOCATION_CHANNEL +
                "/stopLocation";

        public static final String LOCATION_NERWORKSTATE = MethodChannelName.LOCATION_CHANNEL +
                "/networkState";

        public static final String LOCATION_STARTHEADING = MethodChannelName.LOCATION_CHANNEL +
                "/startUpdatingHeading";

        public static final String LOCATION_STOPHEADING = MethodChannelName.LOCATION_CHANNEL +
                "/stopUpdatingHeading";
    }
}
