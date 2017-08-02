package v1.ev.box.charge.smart.smartchargeboxv1.menu.menu_detail_activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
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
import java.util.Calendar;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.activites.station_time.StationFullTimeActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.adapter.MyReservationsListAdapter;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.MyReservationsDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationActionEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationListCancelCharAndRes;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationRemovedEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.array_list_events.ReservationsListHandlerEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

public class MyReservationsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    public static final int CANCEL_RESERVATION = 1;
    public static final int DELETE_CHARGING = 2;
    public static final int MAKE_TOAST = 3;
    public int adapterPosition = -1;
    public MyReservationsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);

        ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(R.drawable.reservation).into(imageView);

        recyclerView = (RecyclerView) findViewById(R.id.reservations_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setTitle("Rezervacijos");
        }

        Calendar calendar = Calendar.getInstance();
        long offset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);
        long time = calendar.getTimeInMillis() + offset;

        Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
        intent.setAction(NetworkingService.GET_RESERVATIONS);
       // intent.putExtra("time", time);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Subscribe
    public void onUserReservationsArrived(ReservationsListHandlerEvent obj) {
        if(obj.list == null || obj.list.size() == 0) {
            createNoReservationsDialog();
            return;
        }
        adapter = new MyReservationsListAdapter(obj.list);
        adapter.setActiveId(obj.activeId);
        recyclerView.setAdapter(adapter);
    }

    @Subscribe
    public void onReservationActionClickEvent(ReservationActionEvent obj) {
        if (obj.getAction() == CANCEL_RESERVATION) {
            adapterPosition = obj.getAdapterPosition();
            createAlertDialog(obj.getStationId(), obj.getReservationId());
        } else if (obj.getAction() == DELETE_CHARGING) {
            createCancelChargingDialog(obj.getTimeDiff(), obj.getReservationId(), obj.getStationId(), obj.getAdapterPosition());
        } else if(obj.getAction() == MAKE_TOAST) {
            Toast.makeText(getApplicationContext(), "Krovimas aktyvus", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onReservationRemovedEvent(ReservationRemovedEvent obj) {
        if("1".equals(obj.getResultTag())) {
            if(adapter != null && adapterPosition != -1) {
                adapter.removeItem(adapterPosition);

                if(adapter.getItemCount() == 0) {
                    createNoReservationsDialog();
                    return;
                }
            }
            Toast.makeText(getApplicationContext(), "Rezervacija ir krovimas pašalinti", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Atšaukimo klaida", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void removeChargingAndReservation(ReservationListCancelCharAndRes obj) {
        Log.d("SDVSDSDSDSDSDSD", obj.getPosition() + "");
        if(adapter != null && obj.getPosition() != -1) {
            adapter.removeItem(obj.getPosition());

            if(adapter.getItemCount() == 0) {
                createNoReservationsDialog();
                return;
            }
        }
    }

    private void createAlertDialog(final String stationId, final String reservationId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MyReservationsActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Rezervacijos atšaukimas");
        dialog.setMessage("Ar tikrai norite ištrinti rezervaciją?");
        dialog.setPositiveButton("Ištrinti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
                intent.setAction(NetworkingService.REMOVE_STATION_RESERVATION);
                intent.putExtra("reservationId", reservationId);
                intent.putExtra("stationId", stationId);
                startService(intent);
            }
        })
        .setNegativeButton("Atšaukti ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    private void createCancelChargingDialog(final long time, final String reservationId, final String stationId, final int adapterPosition) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MyReservationsActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Krovimo atšaukimas");
        dialog.setMessage("Ar tikrai norite atšaukti krovimą?");
        dialog.setPositiveButton("Gerai", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
                intent.putExtra("time_charged", time);
                intent.putExtra("reservationId", reservationId);
                intent.putExtra("adapterPosition", adapterPosition);
                intent.setAction(NetworkingService.CANCEL_CHARGING);
                startService(intent);
            }
        })
                .setNegativeButton("Atšaukti ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    private void createNoReservationsDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MyReservationsActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Jūs neturite rezervacijų");
        dialog.setMessage("Rezervuotis galite pasirinkę vieną iš laisvų stotelės rezervacijos laikų");
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