package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-04-30.
 */

public class TimerEvent {
    private String time;
    private int progress;

    public TimerEvent(String time, int progress) {
        this.time = time;
        this.progress = progress;
    }

    public String getTime() {
        return time;
    }

    public int getProgress() {
        return progress;
    }
}
