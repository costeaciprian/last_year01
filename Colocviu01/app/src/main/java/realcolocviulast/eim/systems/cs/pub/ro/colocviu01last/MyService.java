package realcolocviulast.eim.systems.cs.pub.ro.colocviu01last;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by ciprian on 3/29/2018.
 */

public class MyService extends Service {

    private ProcessingThread prTh = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        prTh = new ProcessingThread(getApplicationContext(), intent.getIntExtra("num_clicks", -1));

        prTh.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void stopService() {
        prTh.stopThread();
    }
}
