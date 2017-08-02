package v1.ev.box.charge.smart.smartchargeboxv1.data_models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Deividas on 2017-04-08.
 */

public class MarkerItemData implements ClusterItem, Parcelable {
    private LatLng mPosition;
    private int id;
    private String imgUrl;
    private int type;
    private int state;

    public MarkerItemData(int id, double lat, double lng, String imgUrl) {
        mPosition = new LatLng(lat, lng);
        this.id = id;
        this.imgUrl = imgUrl;
    }

    public MarkerItemData(int id, double lat, double lng, int state, String imgUrl) {
        mPosition = new LatLng(lat, lng);
        this.id = id;
        setState(state);
        this.imgUrl = imgUrl;
    }

    public void setState(int state) {
        if(state == 0) {
            this.state = state;
        } else {
            this.state = state;
        }
    }

    public int getState() {
        return state;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public int getId(){
        return id;
    }

    public int getType() {
        return type;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public MarkerItemData(Parcel in) {
        this.id = in.readInt();
        this.mPosition = new LatLng(in.readDouble(), in.readDouble());
        this.imgUrl = in.readString();
        this.type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeDouble(mPosition.latitude);
        parcel.writeDouble(mPosition.longitude);
        parcel.writeString(imgUrl);
        parcel.writeInt(type);
    }

    public static Parcelable.Creator<MarkerItemData> CREATOR
            = new Parcelable.Creator<MarkerItemData>() {

        @Override
        public MarkerItemData createFromParcel(Parcel parcel) {
            return new MarkerItemData(parcel);
        }

        @Override
        public MarkerItemData[] newArray(int size) {
            return new MarkerItemData[size];
        }
    };
}
