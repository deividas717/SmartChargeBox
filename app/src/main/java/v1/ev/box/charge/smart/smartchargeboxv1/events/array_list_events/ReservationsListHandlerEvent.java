package v1.ev.box.charge.smart.smartchargeboxv1.events.array_list_events;

import java.util.ArrayList;

import v1.ev.box.charge.smart.smartchargeboxv1.data_models.MyReservationsDataModel;

/**
 * Created by Deividas on 2017-04-30.
 */

public class ReservationsListHandlerEvent {
    public ArrayList<MyReservationsDataModel> list;
    public String activeId;
}
