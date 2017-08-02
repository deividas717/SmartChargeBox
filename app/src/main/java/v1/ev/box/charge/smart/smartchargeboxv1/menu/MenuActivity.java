package v1.ev.box.charge.smart.smartchargeboxv1.menu;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import v1.ev.box.charge.smart.smartchargeboxv1.OCPI;
import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.broadcast_receivers.AlarmBroadcastReceiver;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.MyReservationsDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.database.ConstVals;
import v1.ev.box.charge.smart.smartchargeboxv1.database.InnerDatabase;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ChargingFromFragmentEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.CurrentChargingSessionEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.array_list_events.ReservationsListHandlerEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.charging_sessions.ChargingSessionDataAdded;
import v1.ev.box.charge.smart.smartchargeboxv1.events.charging_sessions.ChargingSessionDataRemoved;
import v1.ev.box.charge.smart.smartchargeboxv1.menu.menu_fragments.MapFragment;
import v1.ev.box.charge.smart.smartchargeboxv1.menu.menu_fragments.MenuFragment;
import v1.ev.box.charge.smart.smartchargeboxv1.menu.menu_fragments.StationsListFragment;
import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

public class MenuActivity extends AppCompatActivity {

    private int lastAction = -1;
    private FragmentManager fragmentManager;
    private SQLiteDatabase db;
    private boolean initReservationData = false;
    private String activeReservationId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        boolean isPermissionGranted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if(!isPermissionGranted) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    11);
        }

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(savedInstanceState != null) {
            lastAction = savedInstanceState.getInt("lastAction", -1);
        }

        if(lastAction == -1) {
            MenuFragment menuFragment = new MenuFragment();
            fragmentTransaction.replace(R.id.main_container, menuFragment);
            fragmentTransaction.commit();
        } else {
            menuHandler(lastAction);
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return menuHandler(item.getItemId());
            }
        });

        db = InnerDatabase.getInstance(getApplicationContext()).getWritableDatabase();
    }

    private boolean menuHandler(int id) {
        switch (id) {
            case R.id.action_more:
                if (lastAction != R.id.action_more) {
                    FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                    fragmentTransaction2.replace(R.id.main_container, new MenuFragment());
                    fragmentTransaction2.commit();
                }
                lastAction = R.id.action_more;
                return true;
            case R.id.action_map:
                if (lastAction != R.id.action_map) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, new MapFragment());
                    fragmentTransaction.commit();
                }
                lastAction = R.id.action_map;
                return true;
            case R.id.action_search:
                if (lastAction != R.id.action_search) {
                    FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                    fragmentTransaction1.replace(R.id.main_container, new StationsListFragment());
                    fragmentTransaction1.commit();
                }
                lastAction = R.id.action_search;
                return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("lastAction", lastAction);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        String result = OCPI.getInstance(getApplicationContext()).reserveNow("asdasd", 1651, 5561, "sdfsdfsdf");
        Log.d("SDGSODID", result + "");
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe
    public void onSendChargingSessionData(ChargingFromFragmentEvent obj) {
        activeReservationId = obj.getId();

        Log.d("SDSGUDISDSD", activeReservationId + "");

        if(!initReservationData) {
            Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
            intent.setAction(NetworkingService.GET_RESERVATIONS);
            startService(intent);
        }
    }

    @Subscribe
    public void onReservationDataArrived(ReservationsListHandlerEvent obj) {
        if (obj.list != null && obj.list.size() > 0 && !initReservationData) {
            String query = "SELECT " + ConstVals.reservationId + "  FROM " + ConstVals.pending_intents_table;
            Cursor cursor = db.rawQuery(query, null);
            String list[] = new String[cursor.getCount()];
            int index = 0;
            try {
                while (cursor.moveToNext()) {
                    String reservationId = cursor.getString(0);
                    list[index] = reservationId;
                    Log.d("SDSsdfGDUISD", "aaaa " + reservationId);
                    index++;
                }
            } finally {
                cursor.close();
            }
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            int pendingIntentId = (int) (System.currentTimeMillis() & 0xfffffff);
            for (int i = 0; i < obj.list.size(); i++) {
                MyReservationsDataModel model = obj.list.get(i);
                boolean insert = true;
                for(int j=0; j<index; j++) {
                    Log.d("SDSHDSDD", model.getReservationId() + " " + list[j] + " " + activeReservationId);
                    if (model.getReservationId().equals(list[j])) {
                        insert = false;
                        break;
                    }
                }
                Log.d("SDSFYUSDSD", obj.list.size()  + " " + obj.list.get(i).getReservationId());
                if(insert) {
                    Log.d("SDSDUIPSDD", model.getReservationId() + " " + activeReservationId);
                    if(!model.getReservationId().equals(activeReservationId)) {
                        Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
                        String action = Long.toString(System.currentTimeMillis());
                        String reservationId = model.getReservationId();
                        String stationId = model.getStationId();
                        long startTime = model.getStartTime();
                        long endTime = model.getEndTime();
                        intent.setAction(action);
                        Log.d("SDSGDUSDD", insert + " " + startTime + " " + model.getReservationId() + " " + (model.getReservationId().equals(activeReservationId)));
                        intent.putExtra("stationId", stationId);
                        intent.putExtra("reservationId", reservationId);
                        intent.putExtra("startTime", startTime);
                        intent.putExtra("endTime", endTime);
                        intent.putExtra("uniqueId", pendingIntentId);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), pendingIntentId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, model.getStartTime(), pendingIntent);
                        saveIntentDataToDatabase(action, reservationId, stationId, endTime, startTime, pendingIntentId);
                        pendingIntentId++;
                    }
                }
            }
            initReservationData = true;
        }
    }

    private void saveIntentDataToDatabase(String action, String reservationId, String stationId, long endTime, long startTime, int pendingIntentId) {
        ContentValues values = new ContentValues();
        values.put(ConstVals.intent_id, pendingIntentId);
        values.put(ConstVals.reservationId, reservationId);
        values.put(ConstVals.stationId, stationId);
        values.put(ConstVals.startTime, startTime);
        values.put(ConstVals.endTime, endTime);
        values.put(ConstVals.action, action);
        db.insert(ConstVals.pending_intents_table, null, values);
    }
}