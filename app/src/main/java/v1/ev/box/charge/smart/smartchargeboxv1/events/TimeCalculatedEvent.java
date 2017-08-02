package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-03-26.
 */

public class TimeCalculatedEvent {
    public String timeFormatted;
    public int secons;

    public TimeCalculatedEvent(String timeFormatted, int secons) {
        this.timeFormatted = timeFormatted;
        this.secons = secons;
    }

    public TimeCalculatedEvent(String timeFormatted) {
        this.timeFormatted = timeFormatted;
    }
}