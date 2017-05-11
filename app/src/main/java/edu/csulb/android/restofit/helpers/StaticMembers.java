package edu.csulb.android.restofit.helpers;

import java.util.Calendar;

public class StaticMembers {

    public static final String CHILD_REVIEWS = "REVIEWS_";
    public static final String CHILD_FOOD_TIMES = "FOOD_TIMES_";
    public static final String CHILD_BREAKFAST = "BREAKFAST";
    public static final String CHILD_LUNCH = "LUNCH";
    public static final String CHILD_DINNER = "DINNER";

    public class IntentFlags {
        public static final String FENCE_RECEIVER_ACTION = "FENCE_RECEIVER_ACTION";
        public static final String RESTAURANT = "RESTAURANT";
        public static final String CATEGORY = "CATEGORY";
        public static final String RESTAURANT_ID = "RESTAURANT_ID";
    }

    public class FenceKeys {
        public static final String HEADPHONES_RUNNING_OR_PLUGGED_IN = "HEADPHONES_RUNNING_OR_PLUGGED_IN";
        public static final String LOCATION = "LOCATION";
    }

    // Returns the hour based on a 24 hour clock.
    public static int getCurrentHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }
}