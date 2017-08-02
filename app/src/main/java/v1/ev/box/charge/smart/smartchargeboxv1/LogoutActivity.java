package v1.ev.box.charge.smart.smartchargeboxv1;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import v1.ev.box.charge.smart.smartchargeboxv1.broadcast_receivers.AlarmBroadcastReceiver;
import v1.ev.box.charge.smart.smartchargeboxv1.database.ConstVals;
import v1.ev.box.charge.smart.smartchargeboxv1.database.InnerDatabase;
import v1.ev.box.charge.smart.smartchargeboxv1.events.AuthTokenEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.intro.LoginFragment;
import v1.ev.box.charge.smart.smartchargeboxv1.menu.MenuActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_logout);

        clearData();
        LoginFragment loginFragment = new LoginFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean("logout", true);
        loginFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, loginFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe
    public void authResult(AuthTokenEvent obj) {
        PreferencesManager.getInstance(getApplicationContext()).writeString(PreferencesManager.TOKEN, obj.getToken());
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void clearData() {
        PreferencesManager.getInstance(getApplicationContext()).clearData();
        SQLiteDatabase db = InnerDatabase.getInstance(getApplicationContext()).getWritableDatabase();
        String query = "SELECT " + ConstVals.intent_id + ", " + ConstVals.reservationId + ", "
                + ConstVals.stationId + ", " + ConstVals.startTime + ", " + ConstVals.endTime + ", " + ConstVals.action
                + "  FROM " + ConstVals.pending_intents_table;
        Cursor cursor = db.rawQuery(query, null);
        try {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            while (cursor.moveToNext()) {
                int intentId = cursor.getInt(0);
                String reservationId = cursor.getString(1);
                String stationId = cursor.getString(2);
                long startTime = cursor.getLong(3);
                long endTime = cursor.getLong(4);
                String action = cursor.getString(5);
                Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
                intent.setAction(action);
                intent.putExtra("stationId", stationId);
                intent.putExtra("reservationId", reservationId);
                intent.putExtra("startTime", startTime);
                intent.putExtra("endTime", endTime);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), intentId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                try {
                    alarmManager.cancel(pendingIntent);
                    Log.d("SDGYSYODGSDDSDSDSD", "OK " + intentId);
                } catch (Exception e) {
                    Log.e("SDGYSYODGSDDSDSDSD", "AlarmManager update was not canceled. " + e.toString() + " " + intentId);
                }
            }
        } finally {
            cursor.close();
        }
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(CurrentNotification.id);
        db.delete(ConstVals.pending_intents_table, null, null);
    }
}
