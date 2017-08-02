package v1.ev.box.charge.smart.smartchargeboxv1.activites.detaill_location;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.activites.station_time.StationFullTimeActivity;
import v1.ev.box.charge.smart.smartchargeboxv1.adapter.CommentsAdapter;
import v1.ev.box.charge.smart.smartchargeboxv1.custom.WriteCommentDialog;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.CommentDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.CommentRemoveDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.EditedCommentDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.LocationDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.DeleteCommentEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.EditCommentEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.NewCommentCreatedEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.array_list_events.CommentsListEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

public class CommentsFragment extends Fragment {
    private RecyclerView recyclerView;
    private int locationId;
    private LinearLayout loading_bar;
    private TextView no_comments_label;
    private CommentsAdapter adapter;

    public CommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.comments_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        loading_bar = (LinearLayout) view.findViewById(R.id.loading_bar);
        no_comments_label = (TextView) view.findViewById(R.id.no_comments_label);

        adapter = new CommentsAdapter();

        Bundle bundle = getArguments();
        if(bundle != null) {
            locationId = bundle.getInt("locationId");

            Intent intent = new Intent(getContext(), NetworkingService.class);
            intent.setAction(NetworkingService.READ_COMMENTS);
            intent.putExtra("locationId", String.valueOf(locationId));
            intent.putExtra("skip", 0);
            getActivity().startService(intent);
        }

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe
    public void onCommentsDataArrived(CommentsListEvent list) {
        loading_bar.setVisibility(View.GONE);
        if(list.getList().size() == 0) {
            no_comments_label.setVisibility(View.VISIBLE);
        } else {
            adapter.setData(list.getList());
            recyclerView.setAdapter(adapter);
        }
    }

    @Subscribe
    public void onUserCreatedComment(NewCommentCreatedEvent obj) {
        if(no_comments_label.getVisibility() == View.VISIBLE) {
            no_comments_label.setVisibility(View.GONE);
        }
        CommentDataModel model = new CommentDataModel();
        model.setComment(obj.getComment());
        model.setImgUrl(obj.getUserImg());
        model.setName(obj.getUserName());
        model.setUserId(obj.getUserId());
        model.setId(obj.getCommentId());
        Log.d("SDGSODSD", obj.getTime() + "");
        model.setTime(obj.getTime());
        adapter.appendNewComment(model);
        recyclerView.setAdapter(adapter);
    }

    @Subscribe
    public void onCommentEdit(EditCommentEvent obj) {
        WriteCommentDialog dialog = WriteCommentDialog.newInstance();
        dialog.setLocationId(String.valueOf(locationId));
        dialog.setComment(obj.getComment());
        dialog.setCommentId(obj.getCommentId());
        Log.d("SDGSDSDD", "dfsdfsdfsdf");
        dialog.show(getActivity().getFragmentManager(), "Comment");
    }

    @Subscribe
    public void onCommentDelete(final DeleteCommentEvent obj) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setCancelable(false);
        dialog.setTitle("Ar tikrai norite ištrinti komentarą?");
        dialog.setMessage("Veiksmo anuliuoti nebus galima");
        dialog.setPositiveButton("Ištrinti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getContext(), NetworkingService.class);
                intent.setAction(NetworkingService.REMOVE_COMMENT);
                intent.putExtra("commentId", obj.getCommentId());
                getActivity().startService(intent);
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

    @Subscribe
    public void onCommentRemoved(CommentRemoveDataModel obj) {
        if(adapter != null) {
            adapter.removeComment(obj.getCommentId());
        }
    }

    @Subscribe
    public void onCommentEdited(EditedCommentDataModel obj) {
        if(adapter != null) {
            adapter.updateComment(obj.getCommentId(), obj.getText());
        }
    }
}