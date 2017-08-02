package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-04-26.
 */

public class ReservationActionEvent {
    private int action;
    private String stationId;
    private String reservationId;
    private int adapterPosition;
    private long timeDiff;

    public ReservationActionEvent(int action, String stationId, String reservationId, int adapterPosition) {
        this.action = action;
        this.reservationId = reservationId;
        this.stationId = stationId;
        this.adapterPosition = adapterPosition;
    }

    public ReservationActionEvent(int action, String stationId, String reservationId, int adapterPosition, long diff) {
        this.action = action;
        this.reservationId = reservationId;
        this.stationId = stationId;
        this.adapterPosition = adapterPosition;
        this.timeDiff = diff;
    }

    public ReservationActionEvent(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }

    public String getStationId() {
        return stationId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }

    public long getTimeDiff() {
        return timeDiff;
    }
}
