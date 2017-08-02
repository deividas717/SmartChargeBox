package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-04-26.
 */

public class ReservationRemovedEvent {
    private String resultTag;
    private String reservationId;

    public ReservationRemovedEvent(String resultTag, String reservationId) {
        this.resultTag = resultTag;
        this.reservationId = reservationId;
    }

    public String getResultTag() {
        return resultTag;
    }

    public String getReservationId() {
        return reservationId;
    }
}
