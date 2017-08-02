package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-05-20.
 */

public class ReservationListCancelCharAndRes {
    private int position;
    public ReservationListCancelCharAndRes(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
