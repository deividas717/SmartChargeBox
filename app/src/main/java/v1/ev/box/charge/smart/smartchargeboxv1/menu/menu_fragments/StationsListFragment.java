package v1.ev.box.charge.smart.smartchargeboxv1.menu.menu_fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.activites.detaill_location.DetailLocationActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.adapter.NearestLocationsAdapter;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.LocationDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.LocationListSelected;
import v1.ev.box.charge.smart.smartchargeboxv1.events.LocationUpdate;
import v1.ev.box.charge.smart.smartchargeboxv1.events.NearestLocationListEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;
import v1.ev.box.charge.smart.smartchargeboxv1.services.LocationService;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

public class StationsListFragment extends Fragment {
    private LocationService locationService;
    private boolean bound = false;
    private boolean locationDataAvailable = false;
    private NearestLocationsAdapter mAdapter;
    private ArrayList<LocationDataModel> list;
    private double lat = -1;
    private double lng = -1;
    private ViewSwitcher viewSwitcher;
    private ProgressBar loadingIndicator;

    public StationsListFragment() {

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
        View view = inflater.inflate(R.layout.fragment_stations_list, container, false);

        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.viewSwitcher);
        loadingIndicator = (ProgressBar) view.findViewById(R.id.loading_indicator);

        list = new ArrayList<>();
        RecyclerView recyclerView  = (RecyclerView) view.findViewById(R.id.stations_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new NearestLocationsAdapter(list, recyclerView);
        recyclerView.setAdapter(mAdapter);

        Button turnOnGPSButton = (Button) viewSwitcher.findViewById(R.id.turn_on_gps_btn);
        turnOnGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        mAdapter.setRadius(PreferencesManager.getInstance(getContext()).getRadius());
        mAdapter.setOnLoadMoreListener(new NearestLocationsAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int totalItemCount) {
                list.add(null);
                mAdapter.notifyItemInserted(list.size() - 1);

                Intent intent = new Intent(getActivity(), NetworkingService.class);
                intent.setAction(NetworkingService.NEAREST_LOCATIONS);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("skip", totalItemCount);
                getActivity().startService(intent);
            }
        });

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Artimiausi krovimo taškai");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        return view;
    }

    private boolean statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return !manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if(statusCheck()) {
            viewSwitcher.setDisplayedChild(0);
        } else {
            viewSwitcher.setDisplayedChild(1);
            if(mAdapter.getItemCount() == 0) {
                loadingIndicator.setVisibility(View.VISIBLE);
            }
        }

        if(!bound) {
            Intent intent = new Intent(getContext(), LocationService.class);
            getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Subscribe
    public void onLocationUpdate(LocationUpdate location) {
        if(!locationDataAvailable) {
            lat = location.lat;
            lng = location.lng;
            Intent intent = new Intent(getActivity(), NetworkingService.class);
            intent.setAction(NetworkingService.NEAREST_LOCATIONS);
            intent.putExtra("lat", lat);
            intent.putExtra("lng", lng);
            intent.putExtra("skip", 0);
            getActivity().startService(intent);
            locationDataAvailable = true;

            mAdapter.setLatLng(lat, lng);
        }

        if (bound) {
            getActivity().unbindService(connection);
            bound = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (bound) {
            getActivity().unbindService(connection);
            bound = false;
        }
    }

    @Subscribe
    public void onDataArrivedEvent(NearestLocationListEvent locationDataModels) {
        loadingIndicator.setVisibility(View.GONE);
        if(list.size() > 0) {
            list.remove(list.size() - 1);
            mAdapter.notifyItemRemoved(list.size());
        }

        for (LocationDataModel m : locationDataModels.list) {
            list.add(m);
            mAdapter.notifyItemInserted(list.size());
        }
        mAdapter.setLoaded();
    }

    @Subscribe
    public void onLocationSelectedEvent(LocationListSelected obj) {
        Intent intent = new Intent(getContext(), DetailLocationActivity.class);
        intent.putExtra("locationId", Integer.parseInt(obj.getId()));
        intent.putExtra("address", obj.getAddress());
        startActivity(intent);
    }
}