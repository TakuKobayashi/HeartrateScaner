package kobayashi.taku.com.heartratescan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private HeartRateScanCamera mHeartRateScanCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button heartScanButton = (Button) findViewById(R.id.HeartRateButton);
        heartScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeartRateScanCamera.scanStart();
            }
        });

        TextView bpmLable = (TextView) findViewById(R.id.bpmLabelText);
        bpmLable.setText(getString(R.string.bpmLabel, 0));

        mHeartRateScanCamera = new HeartRateScanCamera(this);
        mHeartRateScanCamera.setScanCallback(new HeartRateScanCamera.HeartScanCallback() {
            @Override
            public void onBeat(int bpm, long beatSpan) {
                TextView bpmLable = (TextView) findViewById(R.id.bpmLabelText);
                bpmLable.setText(getString(R.string.bpmLabel, bpm));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHeartRateScanCamera.releaseCamera();
    }
}
