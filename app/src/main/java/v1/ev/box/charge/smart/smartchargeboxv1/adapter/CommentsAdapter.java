package v1.ev.box.charge.smart.smartchargeboxv1.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.data_models.CommentDataModel;
import v1.ev.box.charge.smart.smartchargeboxv1.events.DeleteCommentEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.events.EditCommentEvent;
import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;

/**
 * Created by Deividas on 2017-04-28.
 */

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder > {
    private ArrayList<CommentDataModel> list;
    private Calendar calendar = Calendar.getInstance();

    public CommentsAdapter() {

    }

    public void setData(ArrayList<CommentDataModel> list) {
        this.list = list;
    }

    public void appendNewComment(CommentDataModel obj) {
        if(this.list == null) {
            this.list = new ArrayList<>();
        }
        this.list.add(0, obj);
        notifyDataSetChanged();
    }

    public void removeComment(String id) {
        if(list !=null) {
            for(int i=0; i<list.size(); i++) {
                if(list.get(i).getId() != null && list.get(i).getId().equals(id)) {
                    list.remove(i);
                    break;
                }
            }
            notifyDataSetChanged();
        }
    }

    public void updateComment(String id, String comment) {
        if(list !=null) {
            for(int i=0; i<list.size(); i++) {
                if(list.get(i).getId() != null && list.get(i).getId().equals(id)) {
                    list.get(i).setComment(comment);
                    break;
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CommentDataModel data = list.get(position);
        final CommentViewHolder viewHolder = (CommentViewHolder) holder;

        viewHolder.name.setText(data.getName());
        viewHolder.comment.setText(data.getComment());

        Calendar calendar = Calendar.getInstance();
        long diff = calendar.getTimeInMillis() - data.getTime();
        if(diff < 86400000) {
            int hours = (int)(diff / (3600000));
            viewHolder.timeStamp.setText("Prieš " + hours + " val.");
            if(hours < 1) {
                int min = (int) (diff / (60000)) % 60;
                viewHolder.timeStamp.setText("Prieš " + min + " min.");
                if(min < 1) {
                    viewHolder.timeStamp.setText("Ką tik");
                }
            }
        } else if (diff < 172800000) {
            viewHolder.timeStamp.setText("Prieš 2d.");
        } else {
            calendar.setTimeInMillis(data.getTime());
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String h = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            String m = String.valueOf(calendar.get(Calendar.MINUTE));
            String time = calendar.get(Calendar.YEAR) + "-" + format(month) + "-" + format(day) + " " + format(h) + ":" + format(m);

            viewHolder.timeStamp.setText(time);
        }

        Glide.with(viewHolder.profileImg.getContext()).load(data.getImgUrl()).into(viewHolder.profileImg);
        if(data.getUserId() != null && data.getUserId().equals(PreferencesManager.getInstance(viewHolder.profileImg.getContext()).getPrefValue(PreferencesManager.USER_ID))) {
            viewHolder.editComment.setVisibility(View.VISIBLE);
            viewHolder.deleteComment.setVisibility(View.VISIBLE);
        } else {
            viewHolder.editComment.setVisibility(View.INVISIBLE);
            viewHolder.deleteComment.setVisibility(View.INVISIBLE);
        }
    }

    private String format(String s) {
        if(s.length() < 2) {
            return "0" + s;
        }
        return s;
    }

    @Override
    public int getItemCount() {
        if(list != null) {
            return list.size();
        }
        return 0;
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView profileImg;
        protected TextView name;
        protected TextView comment;
        protected TextView timeStamp;
        protected ImageView editComment;
        protected ImageView deleteComment;

        private CommentViewHolder(View itemView) {
            super(itemView);

            profileImg = (CircleImageView) itemView.findViewById(R.id.comment_image);
            name = (TextView) itemView.findViewById(R.id.comment_name);
            comment = (TextView) itemView.findViewById(R.id.comment_text);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
            editComment = (ImageView) itemView.findViewById(R.id.edit_comment);
            deleteComment = (ImageView) itemView.findViewById(R.id.delete_comment);

            editComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new EditCommentEvent(list.get(getAdapterPosition()).getId(), list.get(getAdapterPosition()).getComment()));
                }
            });

            deleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new DeleteCommentEvent(list.get(getAdapterPosition()).getId()));
                }
            });
        }
    }
}