package realcolocviulast.eim.systems.cs.pub.ro.colocviu01last;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecondaryActivity extends Activity {

    private TextView result;
    private Button verify_button, cancel_button;
    private ButtonClickListener buttonClickListener = new ButtonClickListener();

    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cancel_button:
                    setResult(RESULT_CANCELED, null);
                    finish();
                    break;
                case R.id.verify_button:
                    setResult(RESULT_OK, null);
                    finish();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);

        result = findViewById(R.id.result_text);
        verify_button = findViewById(R.id.verify_button);
        cancel_button = findViewById(R.id.cancel_button);

        verify_button.setOnClickListener(buttonClickListener);
        cancel_button.setOnClickListener(buttonClickListener);

        Intent intent = getIntent();
        int num_clicks = intent.getIntExtra("num_clicks", -1);
        result.setText(String.valueOf(num_clicks));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
