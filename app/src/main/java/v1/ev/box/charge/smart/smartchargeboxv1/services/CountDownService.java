package v1.ev.box.charge.smart.smartchargeboxv1.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.activites.charge_activity.ChargeActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.events.TimerEvent;

public class CountDownService extends Service {
    private CountDownTimer countDownTimer;
    private long endTime = -1;
    private long test = -1;

    public CountDownService() {
    }

    private final LocalBinder mBinder = new CountDownService.LocalBinder();

    public class LocalBinder extends Binder {
        public CountDownService getService() {
            return CountDownService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startCountDown(long time, long endTime, final long chargeTime) {
        this.endTime = endTime;
        this.test = endTime - chargeTime;
        Log.d("SDSHDUPSDD", "this.endTime " + this.endTime);
        countDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millis) {
                Log.d("SDSYSDSD", millis + "");
                showNotification(millis);
            }

            @Override
            public void onFinish() {
                showCompleteChargeNotification();
                stopForeground(true);
                stopSelf();
            }
        };

        countDownTimer.start();
    }

    private void showNotification(long millisUntilFinished) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));



       // Log.d("SDGSIDYSDSDD", progress + " " + ((Calendar.getInstance().getTimeInMillis() * 100) / endTime));
        //(current * 100 / duration)

        int max = (int) (test / 1000);

        int progress = (int) (millisUntilFinished/1000);

        int pr = (progress * 100) / max;
        Log.d("SDSGDIUOSDSD", pr + " " + max);
        EventBus.getDefault().post(new TimerEvent(hms, pr));

        Intent notificationIntent = new Intent(this, ChargeActivity.class);
        notificationIntent.putExtra("notPathIntent", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.smart_charge_box_logo)
                .setContentTitle("SmartChargeBox")
                .setContentText("Kraunama: " + hms)
                .setContentIntent(pendingIntent).build();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //mNotificationManager.notify(1, notification);
    }

    private void showCompleteChargeNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.smart_charge_box_logo);
        mBuilder.setContentTitle("SmartChargeBox");
        mBuilder.setContentText("Krovimas pabaigtas");

        Intent resultIntent = new Intent(this, ChargeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ChargeActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, mBuilder.build());

    }

    @Override
    public void onDestroy() {
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }

    public boolean isAlive() {
        return true;
    }
}
