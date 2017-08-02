package v1.ev.box.charge.smart.smartchargeboxv1.adapter;

import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import lecho.lib.hellocharts.model.Line;
import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.ReservationCollapsesDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationActionEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReserveOneMoreTime;
import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;

/**
 * Created by Deividas on 2017-05-06.
 */

public class ReservationsCollideAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder > {
    private ArrayList<ReservationCollapsesDataModel> list;
    private String activeId;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public void setData(ArrayList<ReservationCollapsesDataModel> list, String activeId) {
        this.list = list;
        this.activeId = activeId;
    }

    public void removeItem(String reservationId) {
        if(list != null) {
            for(int i=0; i<list.size(); i++) {
                if(list.get(i).getReservationId() != null && list.get(i).getReservationId().equals(reservationId)) {
                    list.remove(i);
                    break;
                }
            }
            if(list.isEmpty()) {
                EventBus.getDefault().post(new ReserveOneMoreTime());
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_reservation_collapse_item, parent, false);
        return new MyReservationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ReservationCollapsesDataModel data = list.get(position);
        final MyReservationViewHolder viewHolder = (MyReservationViewHolder) holder;
        viewHolder.stationId.setText("#" + data.getStationId());


        if(activeId != null) {
            if(activeId.equals(data.getReservationId())) {
                viewHolder.reservationStartEndDate.setText(dateFormat.format(data.getStartTime()) + "\n" + dateFormat.format(data.getEndTime()));
                viewHolder.cancel_reservation_text.setText("  Atšaukti\n  krovimą");
                viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#DAF7A6"));
                viewHolder.reservationStartEndDate.setVisibility(View.GONE);
                viewHolder.date_label.setVisibility(View.GONE);
                viewHolder.chargeIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.reservationStartEndDate.setText(dateFormat.format(data.getStartTime()) + "\n" + dateFormat.format(data.getEndTime()));
                viewHolder.cancel_reservation_text.setText("  Atšaukti\nrezervaciją");
                viewHolder.cardView.setCardBackgroundColor(Color.WHITE);
                viewHolder.reservationStartEndDate.setVisibility(View.VISIBLE);
                viewHolder.date_label.setVisibility(View.VISIBLE);
                viewHolder.chargeIcon.setVisibility(View.GONE);
            }
        } else {
            viewHolder.reservationStartEndDate.setText(dateFormat.format(data.getStartTime()) + "\n" + dateFormat.format(data.getEndTime()));
            viewHolder.cancel_reservation_text.setText("  Atšaukti\nrezervaciją");
            viewHolder.cardView.setCardBackgroundColor(Color.WHITE);
            viewHolder.reservationStartEndDate.setVisibility(View.VISIBLE);
            viewHolder.date_label.setVisibility(View.VISIBLE);
            viewHolder.chargeIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if(list != null) {
            return list.size();
        }
        return 0;
    }

    private class MyReservationViewHolder extends RecyclerView.ViewHolder {
        private TextView stationId;
        private TextView reservationStartEndDate;
        private TextView cancel_reservation_text;
        private CardView cardView;
        private TextView date_label;
        private ImageView chargeIcon;

        private MyReservationViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card);
            stationId = (TextView) cardView.findViewById(R.id.station_id);
            RelativeLayout cancelReservation = (RelativeLayout) cardView.findViewById(R.id.cancel_reservation);
            reservationStartEndDate = (TextView) cardView.findViewById(R.id.reservation_start_end_date);
            cancel_reservation_text = (TextView) cardView.findViewById(R.id.cancel_reservation_text);
            date_label = (TextView) cardView.findViewById(R.id.date_label);
            chargeIcon = (ImageView)  cardView.findViewById(R.id.chargeIcon);

            cancelReservation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        int adapterPosition = getAdapterPosition();
                        String stationId = list.get(adapterPosition).getStationId();
                        String reservationId = list.get(adapterPosition).getReservationId();
                        if(activeId != null && activeId.equals(list.get(adapterPosition).getReservationId())) {
                            EventBus.getDefault().post(new ReservationActionEvent(99, stationId, reservationId, adapterPosition));
                        } else {
                            EventBus.getDefault().post(new ReservationActionEvent(1, stationId, reservationId, adapterPosition));
                        }
                    } catch (Exception e) {
                        Log.d("SDGSODSD", e.getMessage());
                    }
                }
            });
        }
    }
}
