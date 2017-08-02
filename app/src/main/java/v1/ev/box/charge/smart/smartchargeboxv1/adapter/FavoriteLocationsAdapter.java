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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.LocationDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.LocationListSelected;

/**
 * Created by Deividas on 2017-04-23.
 */

public class FavoriteLocationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<LocationDataModel> locationDataModels;
    private Location location = new Location("CurrentLocation");
    private Location targetLocation = new Location("TargetLocation");

    public FavoriteLocationsAdapter(ArrayList<LocationDataModel> locationDataModels) {
        this.locationDataModels = locationDataModels;

        location.setLatitude(-1);
        location.setLongitude(-1);
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
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_data_item, parent, false);
        return new FavoriteLocationsAdapter.ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final LocationDataModel data = locationDataModels.get(position);
        if(holder instanceof FavoriteLocationsAdapter.ViewHolder) {
            ((FavoriteLocationsAdapter.ViewHolder) holder).name.setText(data.getAddress());
            if(location.getLatitude() != -1 && location.getLongitude() != -1) {
                targetLocation.setLatitude(data.getLat());
                targetLocation.setLongitude(data.getLng());
                ((FavoriteLocationsAdapter.ViewHolder) holder).distance.setText(getDistance());
            }
            ((FavoriteLocationsAdapter.ViewHolder) holder).counter.setText("#" + (position + 1));
            ((FavoriteLocationsAdapter.ViewHolder) holder).mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new LocationListSelected(data.getId(), data.getAddress()));
                }
            });
            String url = "http://maps.google.com/maps/api/staticmap?center=" + data.getLat() + "," + data.getLng()
                    + "&markers=icon:http://tinyurl.com/2ftvtt6|" + data.getLat() + "," + data.getLng()
                    + "&zoom=16&size=" + 150 + "x" + 150 + "&scale=2&key=AIzaSyAkhdgOZJHMoSLTv62fgmfaEpIboQNUMQU";
            ImageView img = ((FavoriteLocationsAdapter.ViewHolder) holder).mapImg;
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
        }
    }

    public String getDistance() {
        DecimalFormat formater = new DecimalFormat("#.0");
        double distance = location.distanceTo(targetLocation) / 1000;
        if(distance < 1) {
            return formater.format(distance) + " m.";
        }
        return formater.format(distance) + " km.";
    }

    @Override
    public int getItemCount() {
        return locationDataModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView distance;
        public ImageView mapImg;
        public TextView counter;
        public View mainView;

        public ViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            name = (TextView) itemView.findViewById(R.id.name);
            distance = (TextView) itemView.findViewById(R.id.distance);
            mapImg = (ImageView) itemView.findViewById(R.id.map_img);
            counter = (TextView) itemView.findViewById(R.id.num_counter);
        }
    }
}
