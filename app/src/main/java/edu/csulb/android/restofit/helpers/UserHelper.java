package edu.csulb.android.restofit.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import edu.csulb.android.restofit.receivers.AlarmReceiver;

public class UserHelper {

    private static final int REQUEST_CODE = 801;
    private static final int REQUEST_CODE2 = 802;
    private static final int REQUEST_CODE3 = 803;

    private int breakfast;
    private int lunch;
    private int dinner;

    /**
     * Credits: http://stackoverflow.com/a/31557052/2058134
     *
     * @param context
     */
    public void startAlarm(final Context context) {

        final AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, AlarmReceiver.class);

        final Calendar firstTurn = Calendar.getInstance();
        final Calendar secondTurn = Calendar.getInstance();
        final Calendar thirdTurn = Calendar.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference().child(StaticMembers.CHILD_FOOD_TIMES + user.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Default times
                breakfast = 8;
                lunch = 13;
                dinner = 20;

                int breakfastCount = 1, lunchCount = 1, dinnerCount = 1;

                for (DataSnapshot father : dataSnapshot.getChildren()) {
                    for (DataSnapshot meal : father.getChildren()) {
                        switch (father.getKey()) {
                            case StaticMembers.CHILD_BREAKFAST:
                                breakfastCount++;
                                breakfast += Integer.parseInt(meal.getValue().toString());
                                break;
                            case StaticMembers.CHILD_LUNCH:
                                lunchCount++;
                                lunch += Integer.parseInt(meal.getValue().toString());
                                break;
                            case StaticMembers.CHILD_DINNER:
                                dinnerCount++;
                                dinner += Integer.parseInt(meal.getValue().toString());
                                break;
                        }
                    }
                }

                breakfast = safeLongToInt(breakfast / breakfastCount);
                lunch = safeLongToInt(lunch / lunchCount);
                dinner = safeLongToInt(dinner / dinnerCount);
                System.out.println("breakfast: " + breakfast + " lunch: " + lunch + " dinner: " + dinner);


                // set times
                firstTurn.set(Calendar.HOUR_OF_DAY, breakfast);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                secondTurn.set(Calendar.HOUR_OF_DAY, lunch);
                PendingIntent alarmIntent2 = PendingIntent.getBroadcast(context, REQUEST_CODE2, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                thirdTurn.set(Calendar.HOUR_OF_DAY, dinner);
                PendingIntent alarmIntent3 = PendingIntent.getBroadcast(context, REQUEST_CODE3, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                alarmMgr.cancel(alarmIntent);
                alarmMgr.cancel(alarmIntent2);
                alarmMgr.cancel(alarmIntent3);

                alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstTurn.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
                alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, secondTurn.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent2);
                alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, thirdTurn.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent3);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Credits: http://stackoverflow.com/a/1590842/2058134
     */
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
