package v1.ev.box.charge.smart.smartchargeboxv1.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.StationDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ShowStationFullTime;

/**
 * Created by Deividas on 2017-04-09.
 */

public class StationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder > {
    private List<StationDataModel> dataList;

    public StationAdapter(List<StationDataModel> dataList) {
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_view, parent, false);
        return new StationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final StationDataModel data = dataList.get(position);
        final StationViewHolder viewHolder = (StationViewHolder) holder;
        viewHolder.bind(data);
    }

    @Override
    public int getItemCount() {
        if(dataList != null) {
            return dataList.size();
        }
        return 0;
    }

    private class StationViewHolder extends RecyclerView.ViewHolder {
        private TextView stationId;
        private String id;
        private LinearLayout reserve_layout;

        private StationViewHolder(View itemView) {
            super(itemView);

            stationId = (TextView) itemView.findViewById(R.id.station_id);
            reserve_layout = (LinearLayout) itemView.findViewById(R.id.reserve_layout);
            reserve_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new ShowStationFullTime(id));
                }
            });

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    EventBus.getDefault().post(new StationPressed(id));
//                }
//            });
        }

        public void bind(StationDataModel obj) {
            stationId.setText(obj.getId());
            id = obj.getId();
        }
    }
}
