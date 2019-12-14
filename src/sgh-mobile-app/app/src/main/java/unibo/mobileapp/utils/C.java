package unibo.mobileapp.utils;

public class C {

    public static final String APP_LOG_TAG = "BT CLN";

    public class bluetooth {
        public static final int ENABLE_BT_REQUEST = 1;
        public static final String BT_DEVICE_ACTING_AS_SERVER_NAME = "HC-06"; //MODIFICARE QUESTA COSTANTE CON IL NOME DEL DEVICE CHE FUNGE DA SERVER
        public static final String BT_SERVER_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    }

    public class message {
        public static final String CONNECTION_ENABLE = "connection_enable";
        public static final String CONNECTION_DISABLED = "connection_disabled";
        public static final String PUMPS_OFF = "pumps_off";
        public static final String PUMPS_ON = "pumps_on";
        public static final String MINIMUM_FLOW = "minimum_flow";
        public static final String MEDIUM_FLOW = "medium_flow";
        public static final String MAXIMUM_FLOW = "maximum_flow";
    }

    public class utility {
        public static final String MANUAL_MODE = "Manual Mode";
        public static final String AUTOMATIC_MODE = "Automatic Mode";
        public static final String PUMPS_ON = "Pumps On";
        public static final String PUMPS_OFF = "Pumps Off";
        public static final String MINIMUM = "Minimum";
        public static final String MEDIUM = "Medium";
        public static final String MAXIMUM = "Maximum";
    }

}
