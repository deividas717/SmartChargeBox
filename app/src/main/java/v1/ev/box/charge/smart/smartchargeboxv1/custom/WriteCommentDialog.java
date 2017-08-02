package v1.ev.box.charge.smart.smartchargeboxv1.custom;

import android.Manifest;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import v1.ev.box.charge.smart.smartchargeboxv1.R;
import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;
import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

/**
 * Created by Deividas on 2017-04-28.
 */

public class WriteCommentDialog extends DialogFragment {
    private EditText commentText;
    private String locationId;
    private String comment;
    private String commentId;

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_write_comment, container, false);

        commentText = (EditText) root.findViewById(R.id.comment_edit_text);
        if(comment != null) {
            commentText.setText(comment);
        }

        Button sendComment = (Button) root.findViewById(R.id.send_comment);
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(commentText.getText() != null && commentText.getText().length() > 0) {
                    if(comment == null) {
                        Intent intent = new Intent(getActivity(), NetworkingService.class);
                        intent.setAction(NetworkingService.SEND_COMMENT);
                        intent.putExtra("comment", commentText.getText().toString());
                        intent.putExtra("locationId", locationId);
                        getActivity().startService(intent);
                        getDialog().dismiss();
                        Log.d("SDSGUDIPSDD", "A");
                    } else {
                        Log.d("SDSGUDIPSDD", "B");
                        if(commentId != null) {
                            Intent intent = new Intent(getActivity(), NetworkingService.class);
                            intent.setAction(NetworkingService.EDIT_COMMENT);
                            intent.putExtra("editText", commentText.getText().toString());
                            intent.putExtra("commentId", commentId);
                            getActivity().startService(intent);
                            getDialog().dismiss();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Tuščias komentaras", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button cancelButton = (Button) root.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return root;
    }

    public static WriteCommentDialog newInstance() {
        WriteCommentDialog ret = new WriteCommentDialog();
        return ret;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null)
            return;
        getDialog().getWindow().setLayout(950, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}