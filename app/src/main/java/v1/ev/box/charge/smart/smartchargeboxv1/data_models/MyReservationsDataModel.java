package v1.ev.box.charge.smart.smartchargeboxv1.data_models;

/**
 * Created by Deividas on 2017-04-25.
 */

public class MyReservationsDataModel {
    private String reservationId;
    private String stationId;
    private long startTime;
    private long endTime;

    public MyReservationsDataModel(String reservationId, long startTime, long endTime, String stationId) {
        this.reservationId = reservationId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.stationId = stationId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }
}
