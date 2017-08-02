package v1.ev.box.charge.smart.smartchargeboxv1.adapter;

import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.MyReservationsDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationActionEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.menu.menu_detail_activities.MyReservationsActivity;

/**
 * Created by Deividas on 2017-04-25.
 */

public class MyReservationsListAdapter extends RecyclerView.Adapter<MyReservationsListAdapter.ViewHolder> {
    private ArrayList<MyReservationsDataModel> reservationsList;
    private final List<ViewHolder> lstHolders;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private String activeId;

    public void setActiveId(String activeId) {
        this.activeId = activeId;
    }

    private Handler mHandler = new Handler();
    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstHolders) {
                long currentTime = System.currentTimeMillis();
                for (ViewHolder holder : lstHolders) {
                    holder.updateTimeRemaining(currentTime);
                }
            }
        }
    };

    public MyReservationsListAdapter(ArrayList<MyReservationsDataModel> reservationsList) {
        this.reservationsList = reservationsList;

        lstHolders = new ArrayList<>();
        startUpdateTimer();
    }

    private void startUpdateTimer() {
        Timer tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(updateRemainingTimeRunnable);
            }
        }, 1000, 1000);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_reservation_item, parent, false);
        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyReservationsDataModel data = reservationsList.get(position);
        synchronized (lstHolders) {
            lstHolders.add(holder);
        }
        if(activeId != null && data.getReservationId().equals(activeId)) {
            holder.cancel_reservation_or_charging.setText("Atšaukti krovimą");
            holder.until_start_charge.setVisibility(View.GONE);
            holder.card.setCardBackgroundColor(Color.parseColor("#DAF7A6"));
//            holder.cancel_reservation_img.setColorFilter(Color.WHITE);
//            holder.cancel_reservation_or_charging.setTextColor(Color.WHITE);
//            holder.date_label.setTextColor(Color.WHITE);
//            holder.reservationStartEndDate.setTextColor(Color.WHITE);
//            holder.stationId.setTextColor(Color.WHITE);
            holder.chargeIcon.setVisibility(View.VISIBLE);
            holder.timeUntilCharge.setVisibility(View.GONE);
        } else {
            holder.cancel_reservation_or_charging.setText("Atšaukti rezervaciją");
            holder.cancel_reservation_or_charging.setTextColor(Color.parseColor("#4285F4"));
            holder.cancel_reservation_img.setColorFilter(Color.parseColor("#4285F4"));
            holder.until_start_charge.setVisibility(View.VISIBLE);
            holder.card.setCardBackgroundColor(Color.WHITE);
            holder.reservationStartEndDate.setTextColor(Color.BLACK);
            holder.stationId.setTextColor(Color.BLACK);

            holder.until_start_charge.setTextColor(Color.BLACK);
            holder.date_label.setTextColor(Color.BLACK);
            holder.chargeIcon.setVisibility(View.GONE);
            holder.timeUntilCharge.setVisibility(View.VISIBLE);
        }
        holder.reservationStartEndDate.setText(dateFormat.format(data.getStartTime()) + "\n"
                + dateFormat.format(data.getEndTime()));
        holder.stationId.setText("#" + data.getStationId());

    }

    @Override
    public int getItemCount() {
        return reservationsList.size();
    }

    private String validateNumber(int num) {
        return num / 10 < 1 ? "0" + num : String.valueOf(num);
    }

    public void removeItem(int position) {
        try {
            reservationsList.remove(position);
            lstHolders.clear();
            notifyItemRemoved(position);
            notifyDataSetChanged();
            //notifyItemRangeChanged(position, reservationsList.size());
        } catch (Exception e) {

        }

    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView stationId;
        private TextView timeUntilCharge;
        private TextView reservationStartEndDate;
        private TextView cancel_reservation_or_charging;
        private ImageView cancel_reservation_img;
        private TextView until_start_charge;
        private CardView card;
        private TextView date_label;
        private ImageView chargeIcon;

        private ViewHolder(View itemView) {
            super(itemView);

            stationId = (TextView) itemView.findViewById(R.id.station_id);
            RelativeLayout cancelReservation = (RelativeLayout) itemView.findViewById(R.id.cancel_reservation);
            timeUntilCharge = (TextView) itemView.findViewById(R.id.time_until_charge_session_opens);
            reservationStartEndDate = (TextView) itemView.findViewById(R.id.reservation_start_end_date);
            cancel_reservation_or_charging = (TextView) itemView.findViewById(R.id.cancel_reservation_or_charging);
            cancel_reservation_img = (ImageView) itemView.findViewById(R.id.cancel_reservation_img);
            until_start_charge = (TextView) itemView.findViewById(R.id.until_start_charge);
            card = (CardView) itemView.findViewById(R.id.card);
            date_label = (TextView) itemView.findViewById(R.id.date_label);
            chargeIcon = (ImageView) itemView.findViewById(R.id.chargeIcon) ;

            cancelReservation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        int adapterPosition = getAdapterPosition();
                        String stationId = reservationsList.get(adapterPosition).getStationId();
                        String reservationId = reservationsList.get(adapterPosition).getReservationId();
                        long diff = Calendar.getInstance().getTimeInMillis() - reservationsList.get(adapterPosition).getStartTime();
                        if(activeId != null && reservationId != null && reservationId.equals(activeId)) {
                            EventBus.getDefault().post(new ReservationActionEvent(MyReservationsActivity.DELETE_CHARGING, stationId, reservationId, adapterPosition, diff));
                        } else {
                            EventBus.getDefault().post(new ReservationActionEvent(MyReservationsActivity.CANCEL_RESERVATION, stationId, reservationId, adapterPosition));
                        }

                    } catch (Exception e) {

                    }
                }
            });

            chargeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new ReservationActionEvent(MyReservationsActivity.MAKE_TOAST));
                }
            });
        }

        void updateTimeRemaining(long currentTime) {
            try {
                long timeDiff = reservationsList.get(getAdapterPosition()).getStartTime() - currentTime;
                if (timeDiff > 0) {
                    int seconds = (int) timeDiff / 1000;
                    int minutes = seconds / 60;
                    int hours = minutes / 60;
                    int days = hours / 24;
                    String time = days + " d. "
                            + validateNumber(hours % 24)
                            + " val. " + validateNumber(minutes % 60)
                            + " min. " + validateNumber(seconds % 60) + " s.";
                    timeUntilCharge.setText(time);
                }
            } catch (Exception e) {
                Log.d("SDHSUIDSDSDDDSD", e.getMessage());
            }

        }
    }
}