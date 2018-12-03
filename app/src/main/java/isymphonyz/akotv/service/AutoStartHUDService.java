package isymphonyz.akotv.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStartHUDService extends BroadcastReceiver {
    public void onReceive(Context context, Intent arg1) {
        Intent intent = new Intent(context, HUD.class);
        context.startService(intent);
        Log.i("Autostart", "started");
    }
}
