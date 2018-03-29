package realcolocviulast.eim.systems.cs.pub.ro.colocviu01last;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;

public class Colocviu01MainActivity extends Activity {

    private TextView out_text, bounded_service_text;
    private Button bottom_left, bottom_right, top_left, top_right, navigate, center;
    private int num_Clicks = 0;
    private ButtonClickListener buttonClickListener = new ButtonClickListener();

    private final int REQUEST_CODE = 1;
    private boolean isServiceStarted = false;
    private boolean isBoundedServiceConnected = false;
    private IntentFilter intentFilter = null;
    private MessageBroadcastReceiver messageBroadcastReceiver = new MessageBroadcastReceiver();
    private MyBoundedService myBoundedService = null;
    private Button message_from_bounded_service = null;

    private class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int message = intent.getIntExtra("msg", -1);
            Toast.makeText(getApplicationContext(), "[MessageBroadcastReceiver] -> Number of Clicks is: "
                            + " " + message,
                    Toast.LENGTH_LONG).show();
            Log.d("[BroadcastReceiver]", "Number of Clicks is: " + message);
        }
    }

    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.top_left_button:
                    num_Clicks++;
                    out_text.setText(",Top Left " + out_text.getText().toString());
                    break;
                case R.id.top_right_button:
                    num_Clicks++;
                    out_text.setText(",Top Right " + out_text.getText().toString());
                    break;
                case R.id.bottom_left_button:
                    num_Clicks++;
                    out_text.setText(",Bottom Left " + out_text.getText().toString());
                    break;
                case R.id.bottom_right_button:
                    out_text.setText(",Bottom Right " + out_text.getText().toString());
                    num_Clicks++;
                    break;
                case R.id.center_button:
                    num_Clicks++;
                    out_text.setText(",Center " + out_text.getText().toString());
                    break;
                case R.id.navigate_button:
                    Intent intent = new Intent(getApplicationContext(), SecondaryActivity.class);
                    intent.putExtra("num_clicks", num_Clicks);
                    startActivityForResult(intent, REQUEST_CODE);
                    break;
                case R.id.get_message_button:
                    if ( myBoundedService != null && isBoundedServiceConnected) {
                        bounded_service_text.setText("[" + new Timestamp(System.currentTimeMillis()) + "]" +
                                myBoundedService.getMessage() + "\n" + bounded_service_text.getText().toString());
                    }
                    break;
            }

            verifyStartService();
        }
    }

    private void verifyStartService() {
        if(!isServiceStarted && num_Clicks > Constants.THRESOLD) {
            isServiceStarted = true;
            Intent intent = new Intent(getApplicationContext(), MyService.class);
            intent.putExtra("num_clicks", num_Clicks);
            startService(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colocviu01_main);

        out_text = findViewById(R.id.output_text);
        top_left = findViewById(R.id.top_left_button);
        top_right = findViewById(R.id.top_right_button);
        bottom_left = findViewById(R.id.bottom_left_button);
        bottom_right = findViewById(R.id.bottom_right_button);
        navigate = findViewById(R.id.navigate_button);
        center = findViewById(R.id.center_button);
        message_from_bounded_service = findViewById(R.id.get_message_button);
        bounded_service_text = findViewById(R.id.bounded_service_text);

        center.setOnClickListener(buttonClickListener);
        navigate.setOnClickListener(buttonClickListener);
        top_right.setOnClickListener(buttonClickListener);
        top_left.setOnClickListener(buttonClickListener);
        bottom_right.setOnClickListener(buttonClickListener);
        bottom_left.setOnClickListener(buttonClickListener);
        message_from_bounded_service.setOnClickListener(buttonClickListener);

        if(savedInstanceState != null) {
            out_text.setText(savedInstanceState.getString("output_text"));
            num_Clicks = savedInstanceState.getInt("num_Clicks");
        }
        else {
            out_text.setText("");
            num_Clicks = 0;
        }

        intentFilter = new IntentFilter();

        for(int i = 0; i < Constants.action_types.length; i++) {
            intentFilter.addAction(Constants.action_types[i]);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(messageBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(messageBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if ( !isBoundedServiceConnected) {
           isBoundedServiceConnected = false;
           unbindService(serviceConnection);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, MyBoundedService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("[RestoreInstanceState]", "is Called !");

        if(savedInstanceState == null) {
            out_text.setText("");
            num_Clicks = 0;
        }
        else {
            out_text.setText(savedInstanceState.getString("output_text"));
            num_Clicks = savedInstanceState.getInt("num_Clicks");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("[onSaveInstanceState]", "is Called !");
        outState.putString("output_text", out_text.getText().toString());
        outState.putInt("num_Clicks", num_Clicks);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getApplicationContext(), "Activity returned with result: " + resultCode,
                Toast.LENGTH_LONG).show();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            isBoundedServiceConnected = true;
            Log.d("ServiceConnected", "Called");
            MyBoundedService.MyBoundedServiceBinder binder = (MyBoundedService.MyBoundedServiceBinder)service;
            myBoundedService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            myBoundedService = null;
            Log.d("ServiceDisconnected", "Called");
            isBoundedServiceConnected = false;
        }
    };
}
