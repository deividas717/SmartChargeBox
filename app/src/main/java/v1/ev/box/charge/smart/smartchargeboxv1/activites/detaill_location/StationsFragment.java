package v1.ev.box.charge.smart.smartchargeboxv1.activites.detaill_location;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.activites.station_time.StationFullTimeActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.adapter.StationAdapter;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.LocationDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.StationDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.database.ConstVals;
import v1.ev.box.charge.smart.smartchargeboxv1.database.InnerDatabase;
import v1.ev.box.charge.smart.smartchargeboxv1.events.FragmentDataToActivityEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.PointersDownlaodEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationTimesArrayWrapperEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationTimesEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ShowStationFullTime;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;


public class StationsFragment extends Fragment  {

    private ImageView mapImg;
    private RecyclerView stationsRecyclerView;
    private TextView addressFull;
    private double lat;
    private double lng;
   // private ImageView heartImg;
    private SQLiteDatabase db;
    private int locationId;
    private MaterialRatingBar starBar;
    private LinearLayout navigateLayout;

    public StationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.fragment_stations, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            locationId = bundle.getInt("locationId");
            Intent intent = new Intent(getContext(), NetworkingService.class);
            intent.setAction(NetworkingService.GET_DETAIL_LOCATION_INFO);
            intent.putExtra("locationId", locationId);
            getActivity().startService(intent);
        }
        //heartImg = (ImageView) convertView.findViewById(R.id.heart_img);
        db = InnerDatabase.getInstance(getContext()).getWritableDatabase();

        stationsRecyclerView = (RecyclerView) convertView.findViewById(R.id.stations_recycler_view);
        stationsRecyclerView.setNestedScrollingEnabled(false);
        mapImg = (ImageView) convertView.findViewById(R.id.map_img);
        mapImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent finishIntent = new Intent();
                finishIntent.setAction("finish");
                finishIntent.putExtra("lat", lat);
                finishIntent.putExtra("lng", lng);
                getActivity().setResult(Activity.RESULT_OK, finishIntent);
                getActivity().finish();
            }
        });
        addressFull = (TextView) convertView.findViewById(R.id.address_full);
        navigateLayout = (LinearLayout) convertView.findViewById(R.id.navigate);
        navigateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lat != -1 && lng != -1) {
                    Intent navigation = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + lng));
                    startActivity(navigation);
                }
            }
        });

        starBar = (MaterialRatingBar) convertView.findViewById(R.id.star_bar);

        return convertView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe
    public void onDataArrived(LocationDataModel obj) {
        lat = obj.getLat();
        lng = obj.getLng();

        Log.d("SDGSUDISD", obj.getUrl()+"");
        String url = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lng
                + "&markers=icon:http://tinyurl.com/2ftvtt6|" + lat + "," + lng
                + "&zoom=16&size=" + 150 + "x" + 150 + "&scale=2&key=AIzaSyAkhdgOZJHMoSLTv62fgmfaEpIboQNUMQU";
        Glide.with(getContext()).load(url).centerCrop().into(mapImg);
        addressFull.setText(obj.getAddress());
        ArrayList<StationDataModel> stationDataModels = obj.getStations();
        StationAdapter adapter = new StationAdapter(stationDataModels);
        stationsRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        stationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        EventBus.getDefault().post(new FragmentDataToActivityEvent(obj.getUrl(), obj.getPhone(), obj.getRating(), obj.getIsFav(), lat, lng));

        if(starBar != null) {
            starBar.setRating((float) obj.getRating());
            starBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
                @Override
                public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                    Log.d("SDSGUIDSD", "sdfsdf " + rating);
                    Log.d("SDSDGPSDDSD", "SIUNCIA");
                    Intent intent = new Intent(getContext(), NetworkingService.class);
                    intent.setAction(NetworkingService.SEND_RATING);
                    intent.putExtra("rating", rating);
                    intent.putExtra("locationId", String.valueOf(locationId));
                    getActivity().startService(intent);
                }
            });
        }
    }

    @Subscribe
    public void onStationTimeClicked(ShowStationFullTime obj) {
        Intent intent = new Intent(getContext(), NetworkingService.class);
        intent.setAction(NetworkingService.GET_STATION_RESERVATION_TIMES);
        intent.putExtra("stationId", obj.id);
        getActivity().startService(intent);
    }

    @Subscribe
    public void onReservationResultParsed(ReservationTimesArrayWrapperEvent obj) {
        Intent intent = new Intent(getContext(), StationFullTimeActivity.class);
        intent.putExtra(StationFullTimeActivity.LOCATION_ID, locationId);
        intent.putExtra(StationFullTimeActivity.STATION_ID, obj.stationId);
        intent.putExtra(StationFullTimeActivity.DATA, obj.list);
        intent.putExtra(StationFullTimeActivity.ACTIVE_ID, obj.activeId);
        getActivity().startActivity(intent);
    }
}