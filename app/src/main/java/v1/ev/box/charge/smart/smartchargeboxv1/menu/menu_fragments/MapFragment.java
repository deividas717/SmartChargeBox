package v1.ev.box.charge.smart.smartchargeboxv1.menu.menu_fragments;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.Algorithm;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.algo.PreCachingAlgorithmDecorator;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.activites.detaill_location.DetailLocationActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.cluster_rendering.ClusterCustomRendering;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.MarkerItemData;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.QueryData;
import v1.ev.box.charge.smart.smartchargeboxv1.events.AuthorizationErrorEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.LocationAddressEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.LocationUpdate;
import v1.ev.box.charge.smart.smartchargeboxv1.events.PointersDownlaodEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.TimeCalculatedEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.google_api.TimeGoogleApiTask;
import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;
import v1.ev.box.charge.smart.smartchargeboxv1.services.LocationService;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        ClusterManager.OnClusterItemClickListener, GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    private Location location;
    private LatLngBounds mBounds;
    private Algorithm algorithm;
    private ClusterManager<MarkerItemData> mClusterManager;
    private ClusterCustomRendering customRenderer;
    private boolean bound = false;
    private boolean isAppStarted = true;
    private LocationService locationService;
    private Handler handler;
    private int currentLocation = -1;
    private LatLng currentLocationCoordinates;
    private BottomSheetData bottomSheetData;
    private MapCircle mapCircle;
    private FloatingActionButton positionMe;
    private FloatingActionButton powerIndicator;
    private VerticalSeekBar verticalSeekBar;
    private boolean seekBarVisible = false;
    private LatLngBounds bounds;
    private FloatingSearchView searchView;
    private static final int MULTIPLY_FACTOR = 1800;
    private float radius = 0;
    private TextView seekBarValue;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("DSGUISPDSDSD", "wtf");
            if(msg != null && msg.obj != null && msg.obj.toString().length() > 1) {
                Intent intent = new Intent(getContext(), NetworkingService.class);
                intent.setAction(NetworkingService.SEARCH_BAR_QUERY);
                intent.putExtra("query", msg.obj.toString());
                getActivity().startService(intent);
            }
        }
    };

    private class BottomSheetData {
        BottomSheetBehavior mBottomSheetBehavior;
        TextView address;
        TextView distance;
        TextView time;
        ImageView addressImg;
        ImageView travelTimeImg;
        ImageView distanceImg;
    }

    public class MapCircle {
        Circle outerCircle, innerCircle;
        int progress;
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

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                mBounds = null;
            } catch (Exception e) {

            } finally{
                handler.postDelayed(this, 10000);
            }
        }
    };

    //------------------------------Subscribe methods-----------------------------------------------

    @Subscribe
    public void onLocationUpdate(LocationUpdate location) {
        this.location.setLatitude(location.lat);
        this.location.setLongitude(location.lng);
        if(isAppStarted && mMap != null) {
            // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(this.location, 16));
            isAppStarted = false;
        }
    }

    @Subscribe
    public void onPointersDownladed(PointersDownlaodEvent obj) {
        Log.d("SDSGDULIDSD", obj.pointersJson);

        new PointersParser().execute(obj.pointersJson);
    }

    @Subscribe
    public void onTimeCalculated(TimeCalculatedEvent obj) {
        bottomSheetData.time.setText(obj.timeFormatted);
    }

    @Subscribe
    public void onLocationAddressArrived(LocationAddressEvent obj) {
        bottomSheetData.address.setText(obj.address);
    }

    @Subscribe
    public void onQueryDataParsed(ArrayList<QueryData> list) {
        Log.d("DSSGDUIDSD", "asdasd " + list.size());
        searchView.swapSuggestions(list);
    }

    @Subscribe
    public void authorizationFailedEvent(AuthorizationErrorEvent obj) {
        Toast.makeText(getContext(), obj.getError(), Toast.LENGTH_SHORT).show();
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        location = new Location("CurrentLocation");
        location.setLatitude(-1);
        location.setLongitude(-1);

        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final View bottomSheet = view.findViewById(R.id.bottom_sheet);
        LinearLayout sheet_head = (LinearLayout) bottomSheet.findViewById(R.id.sheet_head);

        bottomSheetData = new BottomSheetData();
        mapCircle = new MapCircle();

        bottomSheetData.mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetData.mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetData.address = (TextView) sheet_head.findViewById(R.id.address);
        bottomSheetData.distance = (TextView) sheet_head.findViewById(R.id.distance);
        bottomSheetData.time = (TextView) sheet_head.findViewById(R.id.travel_time);

        searchView = (FloatingSearchView) view.findViewById(R.id.floating_search_view);
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                Log.d("SDSDGdfgdg", newQuery + "");
                mHandler.removeMessages(100);
                mHandler.sendMessageDelayed(mHandler.obtainMessage(100, newQuery), 1000);
            }
        });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                QueryData data = (QueryData) searchSuggestion;
                int id = data.getId();
                double lat = data.getLat();
                double lng = data.getLng();
                String address = data.getAddress();
                animateCamera(new LatLng(lat, lng));
                onSearchQueryListItemSelected(id, lat, lng, address);
            }

            @Override
            public void onSearchAction(String currentQuery) {

            }
        });

        seekBarValue = (TextView) view.findViewById(R.id.seekBarValues);

        powerIndicator = (FloatingActionButton) view.findViewById(R.id.show_power_indicator);
        powerIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verticalSeekBar.getVisibility() == View.INVISIBLE) {
                    verticalSeekBar.setVisibility(View.VISIBLE);
                    positionMe.hide();
                    powerIndicator.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null)));
                    drawCircles();
                } else {
                    drawCircles();
                    verticalSeekBar.setVisibility(View.INVISIBLE);
                    positionMe.show();
                    powerIndicator.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#aaaaaa")));
                }

                if(bottomSheetData.mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetData.mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

        verticalSeekBar = (VerticalSeekBar) view.findViewById(R.id.vertical_seek_bar);
        verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarValue.setText(String.valueOf(i) + "%");
                mapCircle.progress = i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                radius = getRadius(seekBar.getProgress());
                PreferencesManager.getInstance(getContext()).writeFloat(PreferencesManager.RADIUS, radius);
                PreferencesManager.getInstance(getContext()).writeFloat(PreferencesManager.BATTERLY_LEVEL, seekBar.getProgress());
                if (mapCircle.outerCircle != null && mapCircle.innerCircle != null) {
                    mapCircle.outerCircle.setRadius(radius);
                    mapCircle.innerCircle.setRadius(mapCircle.outerCircle.getRadius() / 2);
                }
                drawCircles();
                animateToLocation();
            }
        });

        positionMe = (FloatingActionButton) view.findViewById(R.id.position_me);
        positionMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateToLocation();
            }
        });

        sheet_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DetailLocationActivity.class);
                intent.putExtra("locationId", currentLocation);
                intent.putExtra("address", bottomSheetData.address.getText());
                startActivity(intent);
            }
        });

        Log.d("DSGSDUISDSD", "sgdfgfdgdfg");

//        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorlayout);
//        View bottomView = coordinatorLayout.findViewById(R.id.bottom_sheet);
//        bottomSheet = BottomSheetBehaviorGoogleMapsLike.from(bottomView);
//        bottomSheet.setState(BottomSheetBehaviorGoogleMapsLike.STATE_HIDDEN);
//
//        LinearLayout navigate = (LinearLayout) bottomView.findViewById(R.id.navigate);
//        navigate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String to = currentLocationCoordinates.latitude + "," + currentLocationCoordinates.longitude;
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + to));
//                startActivity(intent);
//            }
//        });
        return view;
    }

    private float getRadius(double value) {
        return (float) (180 * value) / 100;
    }

    private void animateCamera(LatLng position) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
    }

    private void animateToLocation() {
        if(statusCheck()) {
            if(location != null && location.getLatitude() != -1 && location.getLatitude() != -1) {
//                        int padding = 30; // offset from edges of the map in pixels
//                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//                        mMap.animateCamera(cu);
                if(bounds != null) {
                    int padding = 30; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cu);
                } else {
                    animateCamera(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            } else {

            }
        }
    }

    private void drawCircles() {
        if(mapCircle.progress > 0) {
            if(mapCircle.outerCircle == null) {
                if(mMap != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mapCircle.outerCircle = mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(mapCircle.progress * MULTIPLY_FACTOR)
                            .strokeWidth(0)
                            .fillColor(0x66aaaFFF));

                    Log.d("DSHUDISDSD", mapCircle.progress + "");

                    mapCircle.innerCircle = mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(mapCircle.outerCircle.getRadius() / 2)
                            .strokeWidth(0)
                            .fillColor(0x66aaaFFF));
                    bounds = circleCenterAndRadiusToBounds(latLng, mapCircle.progress * MULTIPLY_FACTOR);
                }
            } else {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mapCircle.outerCircle.remove();
                mapCircle.innerCircle.remove();

                if(mMap != null) {
                    mapCircle.outerCircle = mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(mapCircle.progress * MULTIPLY_FACTOR)
                            .strokeWidth(0)
                            .fillColor(0x66aaaFFF));

                    mapCircle.innerCircle = mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(mapCircle.outerCircle.getRadius() / 2)
                            .strokeWidth(0)
                            .fillColor(0x66aaaFFF));
                    bounds = circleCenterAndRadiusToBounds(latLng, mapCircle.progress * MULTIPLY_FACTOR);
                }
            }
        } else {
            bounds = null;
            if(mapCircle.outerCircle != null && mapCircle.innerCircle != null) {
                mapCircle.outerCircle.remove();
                mapCircle.innerCircle.remove();
            }
        }
    }

    private LatLngBounds circleCenterAndRadiusToBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    private boolean statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("GPS yra išjungtas. Ar norite įjungti GPS?")
                .setCancelable(false)
                .setPositiveButton("Taip", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onCameraIdle() {
        LatLngBounds currentBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        if (mBounds == null) {
            Intent intent = getBoundsIntent(currentBounds);
            if(intent != null) {
                getActivity().startService(intent);
                mBounds = currentBounds;
            }
        } else if (!(mBounds.contains(currentBounds.northeast)
                || mBounds.contains(currentBounds.southwest))) {
            Intent intent = getBoundsIntent(currentBounds);
            if(intent != null) {
                getActivity().startService(intent);
                mBounds = mBounds.including(currentBounds.northeast);
                mBounds = mBounds.including(currentBounds.southwest);
            }
        }
    }

    private Intent getBoundsIntent(LatLngBounds currentBounds) {
        try {
            Intent intent = new Intent(getContext(), NetworkingService.class);
            intent.setAction(NetworkingService.GET_MARKERS);
            intent.putExtra("leftLongitude", currentBounds.southwest.longitude);
            intent.putExtra("leftLatitude", currentBounds.southwest.latitude);
            intent.putExtra("rightLongitude", currentBounds.northeast.longitude);
            intent.putExtra("rightLatitude", currentBounds.northeast.latitude);
            return intent;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("SDSGDUISD", "sdfsdf");
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(54.897892, 23.889988) , 9.0f) );

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        setUpCluster();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                manageVerticalSeekBarState();
                if(bottomSheetData.mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetData.mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

        mMap.setOnCameraIdleListener(this);
    }

    private void setUpCluster() {
        mClusterManager = new ClusterManager<>(getContext(), mMap);
        algorithm = new PreCachingAlgorithmDecorator<>(new GridBasedAlgorithm<MarkerItemData>());
        //algorithm = new NonHierarchicalDistanceBasedAlgorithm();
        //clusterMng.setAlgorithm(new PreCachingAlgorithmDecorator<MyClusterItem>(new GridBasedAlgorithm<MyClusterItem>()));
        mClusterManager.setAlgorithm(algorithm);
        customRenderer = new ClusterCustomRendering(getContext(), mMap, mClusterManager);
        mClusterManager.setRenderer(customRenderer);

        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MarkerItemData>() {
            @Override
            public boolean onClusterClick(Cluster<MarkerItemData> cluster) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        cluster.getPosition(), (float) Math.floor(mMap
                                .getCameraPosition().zoom + 1)), 1,
                        null);
                return true;
            }
        });

        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
    }

    private class PointersParser extends AsyncTask<String, Void, ArrayList<MarkerItemData>> {

        @Override
        protected ArrayList<MarkerItemData> doInBackground(String... strings) {
            ArrayList<MarkerItemData> list = null;
            Log.d("SDSPDSD", strings[0] + "");
            if(algorithm != null) {
                try {
                    Collection<MarkerItemData> mapMarkers = algorithm.getItems();
                    JSONObject jsonObject = new JSONObject(strings[0]);
                    JSONArray respondArray = jsonObject.getJSONArray("respond");
                    Log.d("SDSPDSD", respondArray.length() + "");
                    list = new ArrayList();
                    for (int i = 0; i < respondArray.length(); i++) {
                        JSONObject station = respondArray.getJSONObject(i);
                        int id = station.getInt("_id");
                        boolean skip = false;
                        for (final MarkerItemData m : mapMarkers) {
                            if (m.getId() == id) {
                                skip = true;
                                break;
                            }

                            if (m.getId() == id) {
                                final BitmapDescriptor iconBitmap = BitmapDescriptorFactory.fromResource(R.drawable.available);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Collection<Marker> list = mClusterManager.getMarkerCollection().getMarkers();
                                        for (Marker masd : list) {
                                            if (masd.getPosition().latitude == m.getPosition().latitude &&
                                                    masd.getPosition().longitude == m.getPosition().longitude) {
                                                masd.setIcon(iconBitmap);
                                            }

                                        }
                                    }
                                });
                                skip = true;
                                break;
                            }
                        }
                        if (!skip) {
                            boolean criticalData = station.has("latitude") && station.has("longitude");
                            if (criticalData) {
                                double lat = station.getDouble("latitude");
                                double lng = station.getDouble("longitude");
                                int state = station.getInt("state");
                                Log.d("DUGSPDIDD", "sdfdsfasdfagf " + state + " id " + id);
                                list.add(new MarkerItemData(id, lat, lng, state, ""));
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.d("DSHISOD", e.getMessage());
                    e.printStackTrace();
                }
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<MarkerItemData> markerItemDatas) {
            super.onPostExecute(markerItemDatas);

            if(markerItemDatas != null && markerItemDatas.size() > 0) {
                mClusterManager.addItems(markerItemDatas);
                mClusterManager.cluster();
            }
        }
    }

    private void manageVerticalSeekBarState() {
        if(verticalSeekBar.getVisibility() == View.VISIBLE) {
            verticalSeekBar.setVisibility(View.INVISIBLE);
            positionMe.show();
            powerIndicator.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#aaaaaa")));
        }
    }

    @Override
    public boolean onClusterItemClick(ClusterItem clusterItem) {
        manageVerticalSeekBarState();
        MarkerItemData markerItemData = (MarkerItemData) clusterItem;
        currentLocation = markerItemData.getId();
        currentLocationCoordinates = markerItemData.getPosition();

        Log.d("SDSDGYODSDSD", currentLocation + "");

        String distance = getDistance(clusterItem.getPosition());

        Intent intent = new Intent(getContext(), NetworkingService.class);
        intent.setAction(NetworkingService.GET_LOCATION_ADDRESS);
        intent.putExtra("locationId", currentLocation);
        getActivity().startService(intent);

        bottomSheetData.distance.setText(distance);
        String uri = getDirectionsUrl(location, clusterItem.getPosition());
        new TimeGoogleApiTask().execute(uri);

        if(bottomSheetData.mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetData.mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        return false;
    }

    private void onSearchQueryListItemSelected(int id, double lat, double lng, String address) {
        currentLocation = id;
        currentLocationCoordinates = new LatLng(lat, lng);

        bottomSheetData.address.setText(address);
        String distance = getDistance(currentLocationCoordinates);

        bottomSheetData.distance.setText(distance);
        String uri = getDirectionsUrl(location, currentLocationCoordinates);
        new TimeGoogleApiTask().execute(uri);

        if(bottomSheetData.mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetData.mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }


    private String getDirectionsUrl(Location origin, LatLng dest){
        String str_origin = "origin="+origin.getLatitude()+","+origin.getLongitude();
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    public String getDistance(LatLng point) {
        if (location.getLatitude() != -1 && location.getLatitude() != -1) {
            Location target = new Location("Target");
            target.setLatitude(point.latitude);
            target.setLongitude(point.longitude);
            DecimalFormat formater = new DecimalFormat("#.0");
            double distance = location.distanceTo(target) / 1000;
            if(distance < 1) {
                return formater.format(distance) + " m.";
            } else {
                return formater.format(distance) + " km.";
            }
        }
        return "";
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!bound) {
            Intent intent = new Intent(getContext(), LocationService.class);
            getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

        handler.post(runnable);

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mapCircle.progress = (int) PreferencesManager.getInstance(getContext()).getBatteryLevel();
        if(mapCircle.progress > 0) {
            drawCircles();
            seekBarValue.setText(String.valueOf(mapCircle.progress) + "%");
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (bound) {
            getActivity().unbindService(connection);
            bound = false;
        }

        handler.removeCallbacks(runnable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("SDSGDUISDD", "sdfsfsdf");
    }
}
