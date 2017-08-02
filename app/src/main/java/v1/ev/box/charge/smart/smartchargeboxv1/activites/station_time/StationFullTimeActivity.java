package v1.ev.box.charge.smart.smartchargeboxv1.activites.station_time;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.broadcast_receivers.AlarmBroadcastReceiver;
import v1.ev.box.charge.smart.smartchargeboxv1.custom.ReservationCollapsesDialogFragment;
import v1.ev.box.charge.smart.smartchargeboxv1.custom.TimeRangePickerDialog;
import v1.ev.box.charge.smart.smartchargeboxv1.custom.WriteCommentDialog;
import v1.ev.box.charge.smart.smartchargeboxv1.database.ConstVals;
import v1.ev.box.charge.smart.smartchargeboxv1.database.InnerDatabase;
import v1.ev.box.charge.smart.smartchargeboxv1.events.DataAdded;
import v1.ev.box.charge.smart.smartchargeboxv1.events.DataChanged;
import v1.ev.box.charge.smart.smartchargeboxv1.events.DataRemoved;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationActionEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationParsedResponse;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationRemovedEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationTimesEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReserveOneMoreTime;
import v1.ev.box.charge.smart.smartchargeboxv1.events.TimeRangeSelectedEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.array_list_events.CollapseEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.menu.menu_detail_activities.MyReservationsActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.parsers.ReservationDDP_Parser;
import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

public class StationFullTimeActivity extends AppCompatActivity implements WeekView.EventClickListener {

    private ArrayList<ReservationTimesEvent> array;
    private ReservationTimesEvent objToRemove;
    private WeekView mWeekView;
    public static final String DATA = "DATA";
    public static final String STATION_ID = "STATION_ID";
    public static final String LOCATION_ID = "LOCATION_ID";
    public static final String ACTIVE_ID = "ACTIVE_ID";
    private Calendar startCalendar = Calendar.getInstance();
    long offset = startCalendar.get(Calendar.ZONE_OFFSET) + startCalendar.get(Calendar.DST_OFFSET);
    private String userId;
    private String stationId;
    private int meteorDDPCounter = 0;
    private boolean skipDDPValidation = false;
    private String activeId;

    private int reservationsCounter = (int) (System.currentTimeMillis() & 0xfffffff);
    private int pendingIntentId = (int) (System.currentTimeMillis() & 0xfffffff);

    private SQLiteDatabase db;
    private AlarmManager alarmManager;
    private TimeRangeSelectedEvent obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_full_time);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = InnerDatabase.getInstance(getApplicationContext()).getWritableDatabase();

        final int locationId = getIntent().getIntExtra(LOCATION_ID, -1);
        stationId = getIntent().getStringExtra(STATION_ID);
        array = getIntent().getParcelableArrayListExtra(DATA);
        activeId = getIntent().getStringExtra(ACTIVE_ID);

        getSupportActionBar().setTitle("#" + stationId);

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
        intent.setAction(NetworkingService.SUB_STATION_TIME);
        intent.putExtra("stationId", stationId);
        startService(intent);

        Log.d("DSHSUDSDSD", "sdfsd  " + activeId);
        //array = new ArrayList<>();
        userId = PreferencesManager.getInstance(getApplicationContext()).getPrefValue(PreferencesManager.USER_ID);

        mWeekView = (WeekView) findViewById(R.id.weekView);
        mWeekView.setOnEventClickListener(this);
        mWeekView.setNumberOfVisibleDays(5);

        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                List<WeekViewEvent> matchedEvents = new ArrayList<>();

                if(array != null) {
                for (int i = 0; i < array.size(); i++) {
                    Calendar startTime = Calendar.getInstance();
                    Calendar endTime = (Calendar) startTime.clone();
                    ReservationTimesEvent obj = array.get(i);
                    startTime.setTimeInMillis(obj.start);
                    endTime.setTimeInMillis(obj.end);
                    WeekViewEvent event = new WeekViewEvent(obj.id, "Rezervuota", startTime, endTime);
                    Log.d("SDSGDUSD", "asdasdasdAAA " + activeId + " " + obj.reservationId);
                    if (userId != null && userId.equals(array.get(i).userId)) {
                        Log.d("SDSGDUSD", "AAA " + activeId + " " + obj.reservationId);
                        if (activeId != null && activeId.equals(obj.reservationId)) {
                            event.setColor(Color.parseColor("#03a9f4"));
                        } else {
                            event.setColor(Color.parseColor("#81c784"));
                        }
                    } else {
                        Log.d("SDSGDUSD", "BBBB " + activeId + " " + obj.reservationId);
                        if (activeId != null && activeId.equals(obj.reservationId)) {
                            event.setColor(Color.parseColor("#ffc107"));
                        } else {
                            event.setColor(Color.parseColor("#ff5722"));
                        }
                    }
                    if (eventMatches(event, newYear, newMonth)) {
                        matchedEvents.add(event);
                    }
                }
                }
                return matchedEvents;
            }
        });

        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        mWeekView.post(new Runnable() {
            @Override
            public void run() {
                int height = mWeekView.getHeight();
                int textSize = mWeekView.getTextSize();
                int padding = mWeekView.getHeaderRowPadding();
                height = height - textSize - (2 * padding);
                mWeekView.setHourHeight(height / 24);
            }
        });

        FloatingActionButton reserveBtn = (FloatingActionButton) findViewById(R.id.reserve_btn);
        reserveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCalendarAndTimeDialogs(stationId);
            }
        });

         alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onEventClick(final WeekViewEvent event, RectF eventRect) {
        int deleteId = (int) event.getId();
        for (int i = 0; i < array.size(); i++) {
            final ReservationTimesEvent e = array.get(i);
            if(e.id == deleteId && userId.equals(e.userId)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String start = sdf.format(event.getStartTime().getTime());
                String end = sdf.format(event.getEndTime().getTime());

                AlertDialog.Builder dialog = new AlertDialog.Builder(StationFullTimeActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle("Rezervacijos atšaukimas");
                dialog.setMessage("Ar tikrai norite ištrinti rezervaciją?\npradžia " + start + "\npabaiga " + end);
                dialog.setPositiveButton("Ištrinti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        objToRemove = e;
                        Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
                        intent.setAction(NetworkingService.REMOVE_STATION_RESERVATION);
                        intent.putExtra("reservationId", e.reservationId);
                        intent.putExtra("stationId", stationId);
                        startService(intent);
                    }
                })
                .setNegativeButton("Atšaukti ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                final AlertDialog alert = dialog.create();
                alert.show();
                break;
            }
        }
    }

    public void setCalendarAndTimeDialogs(final String id) {
        Calendar now = Calendar.getInstance();
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, null
                , year, month, day);
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        final Date date = new Date();
        date.setDate(now.get(Calendar.DAY_OF_MONTH) + 7);
        datePickerDialog.getDatePicker().setMaxDate(date.getTime());
        datePickerDialog.show();
        date.setDate(now.get(Calendar.DAY_OF_MONTH) + 7);
        final Calendar futureCal = Calendar.getInstance();
        futureCal.setTimeInMillis(date.getTime());

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        datePickerDialog.getDatePicker().clearFocus();

                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            String day = String.valueOf(datePickerDialog.getDatePicker().getDayOfMonth());
                            String year = String.valueOf(datePickerDialog.getDatePicker().getYear());
                            String month = String.valueOf(datePickerDialog.getDatePicker().getMonth() + 1);

                            TimeRangePickerDialog timeDialog = TimeRangePickerDialog.newInstance();
                            timeDialog.setCalendarValues(year, month, day);
                            timeDialog.setId(id);
                            timeDialog.show(getSupportFragmentManager(), "Time");
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
        intent.setAction(NetworkingService.UNSUB_STATION_TIME);
        startService(intent);
    }


    public boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year
                && event.getStartTime().get(Calendar.MONTH) == month-1)
                || (event.getEndTime().get(Calendar.YEAR) == year
                && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }

    @Subscribe
    public void onTimeSelected(TimeRangeSelectedEvent obj) {
        this.obj = obj;
        Log.d("SDGUDIPSDDS", "AAAAAAAAA");
        Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
        intent.setAction(NetworkingService.RESERVE_STATION);
        obj.setToken(PreferencesManager.getInstance(getApplicationContext()).getPrefValue(PreferencesManager.TOKEN));
        intent.putExtra("reserveStr", obj.getReserveString());
        startService(intent);
    }

    @Subscribe
    public void onUserSuccessfullyReserved(ReservationParsedResponse obj) {
        if(obj.success) {
            long startTime = obj.startTime - offset;
            long endTime = obj.endTime - offset;
            Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
            String action = Long.toString(System.currentTimeMillis());
            Log.d("SDSFYUDSDSD", obj.stationId + " " + obj.reservationId + " " + startTime + " " + endTime);
            intent.setAction(action);
            intent.putExtra("stationId", obj.stationId);
            intent.putExtra("reservationId", obj.reservationId);
            intent.putExtra("startTime", startTime);
            intent.putExtra("endTime", endTime);
            intent.putExtra("uniqueId", pendingIntentId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), pendingIntentId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
            saveIntentDataToDatabase(action, obj.reservationId, obj.stationId, endTime, startTime);

            pendingIntentId++;
            Toast.makeText(getApplicationContext(), "Rezervacija sėkminga", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("SDSGUIDSDD", "sfsdfodsfdsf");
            Toast.makeText(getApplicationContext(), "Klaida, kitas vartotojas jau rezervavo šį laiką!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveIntentDataToDatabase(String action, String reservationId, String stationId, long endTime, long startTime) {
        ContentValues values = new ContentValues();
        values.put(ConstVals.intent_id, pendingIntentId);
        values.put(ConstVals.reservationId, reservationId);
        values.put(ConstVals.stationId, stationId);
        values.put(ConstVals.startTime, startTime);
        values.put(ConstVals.endTime, endTime);
        values.put(ConstVals.action, action);
        db.insert(ConstVals.pending_intents_table, null, values);
    }

    @Subscribe
    public void onDataAdded(DataAdded obj) {
        if(array.size() == 0) {
            skipDDPValidation = true;
        }
        if (!skipDDPValidation && array != null && meteorDDPCounter < array.size()) {
            meteorDDPCounter++;
            if (meteorDDPCounter == array.size()) {
                skipDDPValidation = true;
                return;
            }
        }
        if (skipDDPValidation) {
            new ReservationDDP_Parser().execute(obj.newValuesJson, obj.documentID);
        }
    }

    @Subscribe
    public void onDataChanged(DataChanged obj) {

    }

    @Subscribe
    public void onDataRemoved(DataRemoved obj) {
        for (int i = 0; i < array.size(); i++) {
            ReservationTimesEvent e = array.get(i);
            if(e.reservationId != null && e.reservationId.equals(obj.documentID)) {
                array.remove(e);
                mWeekView.notifyDatasetChanged();
                break;
            }
        }
    }

    @Subscribe
    public void onReservationRemoved(ReservationRemovedEvent obj) {
        if(objToRemove != null) {
            if ("1".equals(obj.getResultTag())) {
                array.remove(objToRemove);
                mWeekView.notifyDatasetChanged();
                removePendingIntent(obj.getReservationId());
            } else {
                Toast.makeText(this, "Klaida " + obj.getResultTag(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Subscribe
    public void onDDPReservationParsed(ReservationTimesEvent obj) {
        long startTime = obj.start - offset;
        long endTime = obj.end - offset;
        obj.id = reservationsCounter;
        obj.start = startTime;
        obj.end = endTime;
        reservationsCounter++;
        Log.d("SDSGUIPDSDSD", "inserrtes " + obj.id);
        array.add(obj);
        mWeekView.notifyDatasetChanged();
    }

    @Subscribe
    public void onCollapseReservation(CollapseEvent obj) {
        ReservationCollapsesDialogFragment dialog = ReservationCollapsesDialogFragment.newInstance();
        dialog.setData(obj.getList(), obj.getActiveId());
        dialog.show(getFragmentManager(), "ReservationCollapses");
    }

    @Subscribe
    public void onReservationCollapseRemove(ReservationActionEvent obj) {
        Log.d("SDSGDUISDSD", "sdfdsfsf " + obj.getAction() + " " + obj.getReservationId() + " " + obj.getStationId());
        if(obj.getAction() == 1) {
            Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
            intent.setAction(NetworkingService.REMOVE_STATION_RESERVATION);
            intent.putExtra("reservationId", obj.getReservationId());
            intent.putExtra("stationId", obj.getStationId());
            startService(intent);
        } else if (obj.getAction() == 99) {
            createCancelChargerConfirmDialog(obj.getReservationId(), obj.getStationId());
        }
    }

    private void createCancelChargerConfirmDialog(final String reservationId, final String stationId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(StationFullTimeActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Dėmesio! Krovimas aktyvus");
        dialog.setMessage("Ar tikrai norite atšaukti krovimą ir rezervaciją");
        dialog.setPositiveButton("Gerai", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
                intent.setAction(NetworkingService.REMOVE_STATION_RESERVATION);
                intent.putExtra("reservationId", reservationId);
                intent.putExtra("stationId", stationId);
                startService(intent);
            }
        })
        .setNegativeButton("Uždaryti ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    @Subscribe
    public void tryReserveOneMoreTime(ReserveOneMoreTime obj) {
        onTimeSelected(this.obj);
    }

    private void removePendingIntent(String id) {
        String query = "SELECT " + ConstVals.intent_id + ", " + ConstVals.reservationId + ", "
                + ConstVals.stationId + ", " + ConstVals.startTime + ", " + ConstVals.endTime + ", " + ConstVals.action
                + "  FROM " + ConstVals.pending_intents_table + " WHERE " + ConstVals.reservationId
                + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{id});
        int intentId = -1;
        if(cursor.moveToFirst()) {
            intentId = cursor.getInt(0);
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
            intent.putExtra("uniqueId", intentId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), intentId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                alarmManager.cancel(pendingIntent);
            } catch (Exception e) {

            }
        }
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(intentId);
        cursor.close();
        db.delete(ConstVals.pending_intents_table, ConstVals.intent_id + "=?", new String[]{String.valueOf(intentId)});
    }
}