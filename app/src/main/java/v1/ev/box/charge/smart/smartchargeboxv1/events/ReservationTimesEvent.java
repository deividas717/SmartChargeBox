package v1.ev.box.charge.smart.smartchargeboxv1.events;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Deividas on 2017-04-02.
 */

public class ReservationTimesEvent implements Parcelable {
    public String reservationId;
    public String userId;
    public int id;
    public long start;
    public long end;

    public ReservationTimesEvent(int id, String reservationId, long start, long end, String userId) {
        this.id = id;
        this.reservationId = reservationId;
        this.start = start;
        this.end = end;
        this.userId = userId;
    }

    private ReservationTimesEvent(Parcel in) {
        this.id = in.readInt();
        reservationId = in.readString();
        start = in.readLong();
        end = in.readLong();
        userId = in.readString();
        reservationId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(reservationId);
        dest.writeLong(start);
        dest.writeLong(end);
        dest.writeString(userId);
        dest.writeString(reservationId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReservationTimesEvent> CREATOR = new Creator<ReservationTimesEvent>() {
        @Override
        public ReservationTimesEvent createFromParcel(Parcel in) {
            return new ReservationTimesEvent(in);
        }

        @Override
        public ReservationTimesEvent[] newArray(int size) {
            return new ReservationTimesEvent[size];
        }
    };
}