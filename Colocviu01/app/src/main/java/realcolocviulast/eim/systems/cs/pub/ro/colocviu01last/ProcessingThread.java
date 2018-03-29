package realcolocviulast.eim.systems.cs.pub.ro.colocviu01last;

import android.content.Context;
import android.content.Intent;

import java.util.Random;

/**
 * Created by ciprian on 3/29/2018.
 */

public class ProcessingThread extends Thread {

    private boolean isRunning = true;
    private Context context;
    private int num_clicks = 0;
    private Random random = new Random();

    public ProcessingThread(Context context, int num_clicks) {
        this.context = context;
        this.num_clicks = num_clicks;
    }

    @Override
    public void run() {
        super.run();

        while(isRunning) {
            sendBroadcastMessage();
            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException interr) {
            interr.printStackTrace();
        }
    }

    private void sendBroadcastMessage() {
        Intent intent = new Intent();
        intent.setAction(Constants.action_types[random.nextInt(Constants.action_types.length)]);
        intent.putExtra("msg", num_clicks);
        context.sendBroadcast(intent);
    }

    public void stopThread() {
        isRunning = false;
    }
}
