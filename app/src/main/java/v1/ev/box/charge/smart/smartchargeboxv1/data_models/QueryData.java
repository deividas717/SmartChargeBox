package v1.ev.box.charge.smart.smartchargeboxv1.data_models;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by Deividas on 2017-04-12.
 */

public class QueryData implements SearchSuggestion {
    private int id;
    private String address;
    private double lat;
    private double lng;

    public QueryData() {

    }

    public QueryData(Parcel source) {
        this.id = source.readInt();
        this.address = source.readString();
        this.lat = source.readDouble();
        this.lng = source.readDouble();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String getBody() {
        return address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(address);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
    }

    public static final Creator<QueryData> CREATOR = new Creator<QueryData>() {
        @Override
        public QueryData createFromParcel(Parcel in) {
            return new QueryData(in);
        }

        @Override
        public QueryData[] newArray(int size) {
            return new QueryData[size];
        }
    };
}
