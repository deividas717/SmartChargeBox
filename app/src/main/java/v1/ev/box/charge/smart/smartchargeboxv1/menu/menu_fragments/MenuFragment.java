package v1.ev.box.charge.smart.smartchargeboxv1.menu.menu_fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import v1.ev.box.charge.smart.smartchargeboxv1.LogoutActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.activites.StatisticsActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.activites.charge_activity.ChargeActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ChargingFromFragmentEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.CurrentChargingSessionEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.charging_sessions.ChargingSessionDataAdded;
import v1.ev.box.charge.smart.smartchargeboxv1.events.charging_sessions.ChargingSessionDataRemoved;
import v1.ev.box.charge.smart.smartchargeboxv1.menu.menu_detail_activities.FavoriteLocationsActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.menu.menu_detail_activities.MyReservationsActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;
import v1.ev.box.charge.smart.smartchargeboxv1.services.LocationService;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

import static v1.ev.box.charge.smart.smartchargeboxv1.R.id.textView;

public class MenuFragment extends Fragment {
    private LocationService locationService;
    private boolean bound = false;
    private CardView chargingSessions;
    private ValueAnimator colorAnimation;
    private boolean chargingSessionActive = false;

    public MenuFragment() {
        // Required empty public constructor
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            locationService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        ImageView profileImage = (ImageView) view.findViewById(R.id.profileImage);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView email = (TextView) view.findViewById(R.id.email);

        colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.WHITE, Color.parseColor("#ffd54f"));

        Log.d("SDBDSDSDSDSD", PreferencesManager.getInstance(getContext()).getPrefValue(PreferencesManager.IMG_URL) + " dfg");
        Glide.with(this)
                .load(PreferencesManager.getInstance(getContext()).getPrefValue(PreferencesManager.IMG_URL))
                //.diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(profileImage);
        name.setText(PreferencesManager.getInstance(getContext()).getPrefValue(PreferencesManager.USER_NAME));
        email.setText(PreferencesManager.getInstance(getContext()).getPrefValue(PreferencesManager.USER_EMAIL));

        Button logout = (Button) view.findViewById(R.id.logout_btn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LogoutActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        CardView myReservations = (CardView) view.findViewById(R.id.reservationsCard);
        myReservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MyReservationsActivity.class);
                startActivity(intent);
            }
        });

        chargingSessions = (CardView) view.findViewById(R.id.chargeSessions);
        chargingSessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NetworkingService.class);
                intent.setAction(NetworkingService.CURRENT_CHARGING_SESSION);
                intent.putExtra("fromMenu", true);
                getActivity().startService(intent);
            }
        });

        CardView favoriteLocations = (CardView) view.findViewById(R.id.favoriteLocations);
        favoriteLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FavoriteLocationsActivity.class);
                startActivity(intent);
            }
        });

        CardView chargeStats = (CardView) view.findViewById(R.id.chargeStats);
        chargeStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), StatisticsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if(!bound) {
            Intent intent = new Intent(getContext(), LocationService.class);
            getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

        Intent intent = new Intent(getContext(), NetworkingService.class);
        intent.setAction(NetworkingService.CURRENT_CHARGING_SESSION);
        getActivity().startService(intent);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (bound) {
            getActivity().unbindService(connection);
            bound = false;
        }
    }

    @Subscribe
    public void onChargingSessionActive(CurrentChargingSessionEvent obj) {
        Log.d("SUSIUPDSDD", obj.getId() + "");
        if(obj.getId() == null) {
            Log.d("TURISAS", "null");
            if(colorAnimation != null) {
                chargingSessionActive = false;
            }
        }
        if(obj.isFromMenu()) {
            if(obj.getId() == null) {
                chargingSessionActive = false;
                Toast.makeText(getContext(), "Krovimo sesijų nėra", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("SDSHDUISD", obj.getId() + "");
            Intent intent = new Intent(getContext(), ChargeActivity.class);
            intent.putExtra("stationId", obj.getStationId());
            intent.putExtra("reservationId", obj.getReservationId());
            intent.putExtra("startTime", obj.getStartTime());
            intent.putExtra("endTime", obj.getEndTime());
            startActivity(intent);
        } else {
            EventBus.getDefault().post(new ChargingFromFragmentEvent(obj.getReservationId()));
        }
        if(obj.isActualCharging()) {
            if(colorAnimation != null) {
                Log.d("TURISAS", " noppeee");
                chargingSessionActive = true;
                startAnimation();
            }
        }
    }

    private void startAnimation() {
        colorAnimation.setDuration(1000); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if(chargingSessionActive) {
                    chargingSessions.setBackgroundColor((int) animator.getAnimatedValue());
                } else {
                    chargingSessions.setBackgroundColor(Color.parseColor("#bababa"));
                }

            }
        });

        colorAnimation.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimation.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimation.start();
    }
}