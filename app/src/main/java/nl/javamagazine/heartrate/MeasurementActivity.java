/**
 * Demo app for Android Wear, for JavaMagazine
 * measuring heartrate and then show a card fragment with average heartrate
 * Rik Vegt, Ordina, februari 2015
 */

package nl.javamagazine.heartrate;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.DelayedConfirmationView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;


public class MeasurementActivity extends Activity implements DelayedConfirmationView.DelayedConfirmationListener {

    private static final String DEBUG_TAG = "HRMeasurementActivity";
    public static final String EXTRA_MSG = "Activity";
    private int durationMs = 20000;
    private TextView text;
    private ArrayList<Integer> values = new ArrayList<>();
    private SensorManager sensorManager;
    private Sensor hrSensor;
    private boolean hrKnown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        hrSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        text = (TextView) this.findViewById(R.id.textView);
        sensorManager.registerListener(listener, hrSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (hrSensor == null) {
            text.setText("Sensor niet gevonden!");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener, hrSensor);
    }

    public void measurement(View view) {
        text.setText("Wacht op sensor.");
        int count = 0;
        Log.d(DEBUG_TAG, "" + hrKnown);
        while (!hrKnown) {
            Log.d(DEBUG_TAG, "ff wachten nog..." + count);
            try {
                count++;
                Thread.sleep(1000);
                if (count > 30) {
                    text.setText("Wacht te lang!");
                    return;
                }
            } catch (InterruptedException ie) {
                Log.w("MeasurementActivity", "exception: " + ie);
            }
        }
        startMeasurement();
    }

    public void startMeasurement() {
        text.setText("Meten...");
        DelayedConfirmationView delayedConfirmationView = (DelayedConfirmationView)
                this.findViewById(R.id.timer);
        delayedConfirmationView.setTotalTimeMs(durationMs);
        delayedConfirmationView.setListener(this);
        delayedConfirmationView.start();
    }

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                if (event.values[0] > 0) {
                    values.add((int) event.values[0]);
                    hrKnown = true;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onTimerFinished(View v) {
        Log.d(DEBUG_TAG, "onTimerFinished is called.");
        int avg = average();
        text.setText(R.string.start_msg);
        Intent intent = new Intent(getApplicationContext(), MyDisplayActivity.class);
        intent.putExtra(EXTRA_MSG, avg);
        startActivity(intent);
    }

    @Override
    public void onTimerSelected(View v) {
        Log.d(DEBUG_TAG, "onTimerSelected is called.");
    }

    private int average() {
        Log.d(DEBUG_TAG, "Gemiddelde wordt berekend...");
        text.setText(R.string.calc_msg);
        int sum = 0;
        if (values.size() != 0) {
            for (int x : values) {
                sum += x;
            }
            return (sum / values.size());
        }
        return 0;
    }

}