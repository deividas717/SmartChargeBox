package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-04-18.
 */

public class AuthorizationErrorEvent {
    private String error;

    public String getError() {
        return error;
    }

    public AuthorizationErrorEvent(String error) {

        this.error = error;
    }
}
