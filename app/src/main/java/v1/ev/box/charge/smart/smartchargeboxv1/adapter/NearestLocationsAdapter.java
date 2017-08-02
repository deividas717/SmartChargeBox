package v1.ev.box.charge.smart.smartchargeboxv1.adapter;

import android.location.Location;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.LocationDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.LocationListSelected;

import static v1.ev.box.charge.smart.smartchargeboxv1.R.id.progressBar;

/**
 * Created by Deividas on 2017-04-21.
 */

public class NearestLocationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<LocationDataModel> locationDataModels;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 8;
    private Location location = new Location("CurrentLocation");
    private Location targetLocation = new Location("TargetLocation");

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private float radius;
    private DecimalFormat formater = new DecimalFormat("#.0");

    public interface OnLoadMoreListener {
        void onLoadMore(int totalItemCount);
    }

    public NearestLocationsAdapter(ArrayList<LocationDataModel> locationDataModels, RecyclerView recyclerView) {
        this.locationDataModels = locationDataModels;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore(totalItemCount);
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public void setLoaded() {
        loading = false;
    }

    public void setRadius(float r) {
        radius = r;
    }

    @Override
    public int getItemViewType(int position) {
        if(locationDataModels == null) return VIEW_PROG;
        return locationDataModels.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setLatLng(double lat, double lng) {
        location.setLatitude(lat);
        location.setLongitude(lng);
    }

    public void setData(ArrayList<LocationDataModel> locationDataModels) {
        this.locationDataModels = locationDataModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if(viewType == VIEW_ITEM) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_data_item, parent, false);
            vh = new ViewHolder(layoutView);
        } else {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);
            vh = new ProgressViewHolder(layoutView);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final LocationDataModel data = locationDataModels.get(position);
        if(holder instanceof ViewHolder) {
            ((ViewHolder) holder).name.setText(data.getAddress());
            targetLocation.setLatitude(data.getLat());
            targetLocation.setLongitude(data.getLng());

            double distance = getDistance();
            if(distance < 1) {
                ((ViewHolder) holder).distance.setText("0" + formater.format(distance) + " m.");
            } else {
                ((ViewHolder) holder).distance.setText(formater.format(distance) + " km.");
            }

            if(distance < radius) {
                ((ViewHolder) holder).outerCircle.setVisibility(View.VISIBLE);
                if(distance < radius / 2) {
                    ((ViewHolder) holder).innerCircle.setVisibility(View.VISIBLE);
                } else {
                    ((ViewHolder) holder).innerCircle.setVisibility(View.GONE);
                }
            } else {
                ((ViewHolder) holder).innerCircle.setVisibility(View.GONE);
                ((ViewHolder) holder).outerCircle.setVisibility(View.GONE);
            }

            ((ViewHolder) holder).counter.setText("#" + (position + 1));
            ((ViewHolder) holder).mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new LocationListSelected(data.getId(), data.getAddress()));
                }
            });
            String url = "http://maps.google.com/maps/api/staticmap?center=" + data.getLat() + "," + data.getLng()
                    + "&markers=icon:http://tinyurl.com/2ftvtt6|" + data.getLat() + "," + data.getLng()
                    + "&zoom=16&size=" + 150 + "x" + 150 + "&scale=2&key=AIzaSyAkhdgOZJHMoSLTv62fgmfaEpIboQNUMQU";
            ImageView img = ((ViewHolder) holder).mapImg;
            Glide.with(img.getContext()).load(url).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                   // progressBar.setVisibility(View.GONE);
                    Log.d("SDSDHUSDSD", e.getMessage() + "");
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    //progressBar.setVisibility(View.GONE);
                    Log.d("SDSDHUSDSD", "OKK");
                    return false;
                }
            }).into(img);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public double getDistance() {
        double distance = location.distanceTo(targetLocation) / 1000;
        return distance;
    }

    @Override
    public int getItemCount() {
        return locationDataModels.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView distance;
        public ImageView mapImg;
        public TextView counter;
        public View mainView;
        public ImageView innerCircle, outerCircle;

        public ViewHolder(View itemView) {
            super(itemView);

            mainView = itemView;
            name = (TextView) itemView.findViewById(R.id.name);
            distance = (TextView) itemView.findViewById(R.id.distance);
            mapImg = (ImageView) itemView.findViewById(R.id.map_img);
            counter = (TextView) itemView.findViewById(R.id.num_counter);
            innerCircle = (ImageView) itemView.findViewById(R.id.inner_circle);
            outerCircle = (ImageView) itemView.findViewById(R.id.outer_circle);

            innerCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(innerCircle.getContext(), "Su dabartiniu baterijos lygiu, galima nuvažiuoti iki krovimo stotelės ir grįžti į dabartinę poziciją", Toast.LENGTH_SHORT).show();
                }
            });

            outerCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(innerCircle.getContext(), "Su dabartiniu baterijos lygiu, galima nuvažiuoti iki krovimo stotelės", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }
}
