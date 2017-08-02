package v1.ev.box.charge.smart.smartchargeboxv1.cluster_rendering;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.MarkerItemData;

/**
 * Created by Deividas on 2017-04-08.
 */

public class ClusterCustomRendering extends DefaultClusterRenderer<MarkerItemData> {


    public ClusterCustomRendering(Context context, GoogleMap map, ClusterManager<MarkerItemData> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MarkerItemData item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);

//        if (item.isActive()) {
//            BitmapDescriptor iconBitmap = BitmapDescriptorFactory.fromResource(R.drawable.availableaaa);
//            markerOptions.icon(iconBitmap);
//        } else {
//            BitmapDescriptor iconBitmap = BitmapDescriptorFactory.fromResource(R.drawable.notavailableaaa);
//            markerOptions.icon(iconBitmap);
//        }

        BitmapDescriptor iconBitmap = BitmapDescriptorFactory.fromResource(R.drawable.availableaaa);
        markerOptions.icon(iconBitmap);
    }
}
