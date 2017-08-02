package v1.ev.box.charge.smart.smartchargeboxv1.menu.menu_detail_activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.activites.detaill_location.DetailLocationActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.adapter.FavoriteLocationsAdapter;
import v1.ev.box.charge.smart.smartchargeboxv1.adapter.NearestLocationsAdapter;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.LocationDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.database.ConstVals;
import v1.ev.box.charge.smart.smartchargeboxv1.database.InnerDatabase;
import v1.ev.box.charge.smart.smartchargeboxv1.events.LocationListSelected;
import v1.ev.box.charge.smart.smartchargeboxv1.events.LocationUpdate;
import v1.ev.box.charge.smart.smartchargeboxv1.events.NearestLocationListEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.services.LocationService;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

public class FavoriteLocationsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LocationService locationService;
    private boolean bound = false;
    private boolean locationDataAvailable = false;
    private double lat = -1;
    private double lng = -1;
    public FavoriteLocationsAdapter adapter;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_locations);

        ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(R.drawable.fav_list).into(imageView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setTitle("Mėgstamos stotelės");
        }

        recyclerView = (RecyclerView) findViewById(R.id.fav_locations_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if(!bound) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

        Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
        intent.setAction(NetworkingService.FAV_STATIONS);
        startService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Subscribe
    public void onFavDataArrived(NearestLocationListEvent locationDataModels) {
        if(locationDataModels.list.size() == 0) {
            createNoFavAlertDialog();
            return;
        }
        adapter = new FavoriteLocationsAdapter(locationDataModels.list);
        if(lat != -1 && lng != -1) {
            adapter.setLatLng(lat, lng);
        }
        recyclerView.setAdapter(adapter);
    }

    @Subscribe
    public void onLocationSelected(LocationListSelected obj) {
        Intent intent = new Intent(getApplicationContext(), DetailLocationActivity.class);
        intent.putExtra("locationId", Integer.parseInt(obj.getId()));
        intent.putExtra("address", obj.getAddress());
        startActivity(intent);
    }

    @Subscribe
    public void onLocationUpdate(LocationUpdate location) {
        if(!locationDataAvailable) {
            lat = location.lat;
            lng = location.lng;
            if(adapter != null) {
                adapter.setLatLng(lat, lng);
                adapter.notifyDataSetChanged();
            }
            locationDataAvailable = true;
        }

        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    private void createNoFavAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(FavoriteLocationsActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Jūs neturite mėgstamų lokacijų");
        dialog.setMessage("Jas pridėti galite lokacijos lange paspaudę + mygtuką");
        dialog.setPositiveButton("Uždaryti langą", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        final AlertDialog alert = dialog.create();
        alert.show();
    }
}