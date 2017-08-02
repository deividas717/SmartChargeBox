package v1.ev.box.charge.smart.smartchargeboxv1.broadcast_receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import v1.ev.box.charge.smart.smartchargeboxv1.services.NetworkingService;

public class CancelChargeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String stationId = intent.getStringExtra("stationId");
        String reservationId = intent.getStringExtra("reservationId");

        Intent stationService = new Intent(context, NetworkingService.class);
        stationService.setAction(NetworkingService.REMOVE_STATION_RESERVATION);
        stationService.putExtra("stationId", stationId);
        stationService.putExtra("reservationId", reservationId);
        context.startService(stationService);

        int notificationId = intent.getIntExtra("notificationId", 0);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}