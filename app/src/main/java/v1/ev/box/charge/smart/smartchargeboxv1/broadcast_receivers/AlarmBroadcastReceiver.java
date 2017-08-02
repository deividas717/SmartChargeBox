package v1.ev.box.charge.smart.smartchargeboxv1.broadcast_receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import v1.ev.box.charge.smart.smartchargeboxv1.CurrentNotification;
import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.activites.charge_activity.ChargeActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.services.CountDownService;

/**
 * Created by Deividas on 2017-04-10.
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        final int uniqueInt = intent.getIntExtra("uniqueId", -1);

        CurrentNotification.id = uniqueInt;
        String stationId = intent.getStringExtra("stationId");
        String reservationId = intent.getStringExtra("reservationId");

        long startTime = intent.getLongExtra("startTime", -1);
        long endTime = intent.getLongExtra("endTime", -1);

        Intent cancelIntent = new Intent(context, CancelChargeBroadcastReceiver.class);
        cancelIntent.putExtra("notificationId", uniqueInt);
        cancelIntent.putExtra("stationId", stationId);
        cancelIntent.putExtra("reservationId", reservationId);
        PendingIntent pendingCancelIntent = PendingIntent.getBroadcast(context, uniqueInt, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent chargeIntent = new Intent(context, ChargeActivity.class);
        chargeIntent.putExtra("stationId", stationId);
        chargeIntent.putExtra("reservationId", reservationId);
        chargeIntent.putExtra("startTime", startTime);
        chargeIntent.putExtra("endTime", endTime);
        chargeIntent.putExtra("notificationId", uniqueInt);

        Log.d("VISISSYDSDSDSD", "RECEIVERISS " + stationId + " " + reservationId + " " + startTime + " " + endTime + " " + uniqueInt);

        Log.d("sdfhsdfsdfg", stationId + " " + reservationId + " " + startTime + " " + endTime);
        Log.d("SDGYSYODGSDDSDSDSD", "IR SPEK KA TROM " + uniqueInt);

        final long FIFTEEN_MIN = 900000;
        Calendar calendar = Calendar.getInstance();
        long millisUntilFinished = calendar.getTimeInMillis();
        calendar.setTimeInMillis(millisUntilFinished + FIFTEEN_MIN);
        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.smart_charge_box_logo)
                .setContentTitle("Krovimas turi būti pradėtas")
                .addAction(R.drawable.ic_power_black_24dp, "Krauti",
                        PendingIntent.getActivity(context, 0, chargeIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(R.drawable.ic_close_black_24dp, "Atšaukti", pendingCancelIntent)
                .setContentText("Automatinis atšaukimas: " + time);
        mBuilder.setOngoing(true);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(uniqueInt, mBuilder.build());



//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                manager.cancel(uniqueInt);
//            }
//        }, FIFTEEN_MIN);
    }
}