package v1.ev.box.charge.smart.smartchargeboxv1.activites.charge_activity;

import android.animation.ValueAnimator;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.dd.CircularProgressButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.ChargeInitDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.TimerEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.charging_sessions.ChargingSessionDataAdded;
import v1.ev.box.charge.smart.smartchargeboxv1.events.charging_sessions.ChargingSessionDataRemoved;
import v1.ev.box.charge.smart.smartchargeboxv1.events.DataAdded;
import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;
import v1.ev.box.charge.smart.smartchargeboxv1.services.CountDownService;
import v1.ev.box.charge.smart.smartchargeboxv1.services.LocationService;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

public class ChargeActivity extends AppCompatActivity {
    private String chargeSessionId;
    private ViewSwitcher viewSwitcher;
    private CircularProgressButton circularButton;
    private int notificationId = -1;
    private String reservationId;
    private String stationId;
    private long startTime;
    private TextView timer;
    private CountDownService countDownService;
    private boolean bound = false;
    private long time = -1;
    private long endTime = -1;
    private ProgressBar progressBar;
    private Calendar calendar;
    private long startCharge = 0;
    private long endCharge = 0;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CountDownService.LocalBinder binder = (CountDownService.LocalBinder) service;
            countDownService = binder.getService();
            countDownService.startCountDown(time, endTime, startTime);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        stationId = getIntent().getStringExtra("stationId");
        reservationId = getIntent().getStringExtra("reservationId");
        startTime = getIntent().getLongExtra("startTime", -1);
        endTime = getIntent().getLongExtra("endTime", -1);

        notificationId = getIntent().getIntExtra("notificationId", -1);

        calendar = Calendar.getInstance();
        viewSwitcher = (ViewSwitcher) findViewById(R.id.view_switcher);
        timer = (TextView) viewSwitcher.findViewById(R.id.timer);
        progressBar = (ProgressBar) viewSwitcher.findViewById(R.id.progress_bar);
        progressBar.setMax(100);

        circularButton = (CircularProgressButton) findViewById(R.id.circularButton1);
        circularButton.setIndeterminateProgressMode(true);
        circularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(circularButton.getProgress() != 0) {
                    cancelCharging();
                    circularButton.setProgress(0);
                } else {
                    if(Calendar.getInstance().getTimeInMillis() >= endTime) {
                        Toast.makeText(getApplicationContext(), "Krovimo sesija negalioja", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
                        intent.setAction(NetworkingService.REMOVE_STATION_RESERVATION);
                        intent.putExtra("reservationId", reservationId);
                        intent.putExtra("stationId", stationId);
                        startService(intent);
                        finish();
                        return;
                    }
                    Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
                    intent.setAction(NetworkingService.INIT_CHARGING);
                    intent.putExtra("stationId", stationId);
                    intent.putExtra("startTime", startTime);
                    intent.putExtra("endTime", endTime);
                    intent.putExtra("reservationId", reservationId);
                    startService(intent);
                    circularButton.setProgress(5);
                }
            }
        });

        if(notificationId == -1) {
            circularButton.setVisibility(View.GONE);
        } else {
            circularButton.setVisibility(View.VISIBLE);
        }

        Button button = (Button) findViewById(R.id.cancel_charging_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChargeActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle("Atšaukti krovimą?");
                dialog.setMessage("Atšakus krovimą taip pat bus ištrintas jūsų likęs krovimo laikas");
                dialog.setPositiveButton("Ištrinti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        cancelCharging();
                        finish();
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
            }
        });
    }

    private void cancelCharging() {
        endCharge = Calendar.getInstance().getTimeInMillis();;
        PreferencesManager.getInstance(getApplicationContext()).clearStationId();
        Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
        intent.setAction(NetworkingService.CANCEL_CHARGING);
        intent.putExtra("time_charged", (endCharge - startCharge));
        intent.putExtra("reservationId", reservationId);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
        intent.setAction(NetworkingService.SUB_CHARGING_SESSION);
        intent.putExtra("stationId", stationId);
        startService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        try {
            if (bound) {
                unbindService(connection);
                bound = false;
            }
        } catch (Exception e) {
            Log.d("SDSDUDSDSD", e.getMessage());
        }


        Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
        intent.setAction(NetworkingService.SUB_CHARGING_SESSION);
        startService(intent);
    }

    private void simulateSuccessProgress() {
        ValueAnimator successAnim = ValueAnimator.ofInt(1, 100);
        successAnim.setDuration(1500);
        successAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        successAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                circularButton.setProgress(value);
            }
        });
        successAnim.start();
    }

    @Subscribe
    public void onChargeInitDataArrived(ChargeInitDataModel obj) {
        chargeSessionId = obj.getChargeSessionId();
    }

    @Subscribe
    public void onChargingStarted(ChargingSessionDataAdded obj) {
        if(notificationId != -1) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notificationId);
        }
        viewSwitcher.setDisplayedChild(1);

        startCharge = startTime;

        time = endTime - calendar.getTimeInMillis();
        if(!bound) {
            Intent intent = new Intent(getApplicationContext(), CountDownService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Subscribe
    public void onChargeCanceled(ChargingSessionDataRemoved obj) {
        PreferencesManager.getInstance(getApplicationContext()).clearStationId();
        Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
        intent.setAction(NetworkingService.REMOVE_STATION_RESERVATION);
        intent.putExtra("reservationId", reservationId);
        intent.putExtra("stationId", stationId);
        startService(intent);

        progressBar.setProgress(0);
        AlertDialog.Builder dialog = new AlertDialog.Builder(ChargeActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Krovimo laikas baigėsi");
        dialog.setPositiveButton("Uždaryti langą", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        final AlertDialog alert = dialog.create();
        alert.show();
    }

    @Subscribe
    public void onCounterValuesChangedEvent(TimerEvent obj) {
        timer.setText(obj.getTime());

        Log.d("SDSHIPDSDD", (obj.getProgress() + ""));
        progressBar.setProgress(obj.getProgress() + 1);
    }
}
