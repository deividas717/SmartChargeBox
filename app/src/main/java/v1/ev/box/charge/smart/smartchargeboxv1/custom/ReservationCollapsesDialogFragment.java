package v1.ev.box.charge.smart.smartchargeboxv1.custom;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.adapter.ReservationsCollideAdapter;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.ReservationCollapsesDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReservationRemovedEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.ReserveOneMoreTime;

public class ReservationCollapsesDialogFragment extends DialogFragment {
    private RecyclerView recyclerView;
    private ReservationsCollideAdapter adapter;

    public void setData(ArrayList<ReservationCollapsesDataModel> list, String activeId) {
        adapter = new ReservationsCollideAdapter();
        adapter.setData(list, activeId);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation_collapses_dialog, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.collapse_reservations_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        Button closeBtn = (Button) view.findViewById(R.id.cancel);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return view;
    }

    public static ReservationCollapsesDialogFragment newInstance() {
        ReservationCollapsesDialogFragment ret = new ReservationCollapsesDialogFragment();
        return ret;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null) return;
        getDialog().getWindow().setLayout(950, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Subscribe
    public void onReservationRemoved(ReservationRemovedEvent obj) {
        if ("1".equals(obj.getResultTag())) {
            if(adapter != null) {
                adapter.removeItem(obj.getReservationId());
            }
        } else {
            Toast.makeText(getActivity(), "Klaida " + obj.getResultTag(), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void closeDialog(ReserveOneMoreTime obj) {
        getDialog().dismiss();
    }
}