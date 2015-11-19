package iiitd.noisemonitor;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ubidots.ApiClient;
import com.ubidots.Value;
import com.ubidots.Variable;

import java.text.SimpleDateFormat;
import java.util.Date;

/// Related links:
// http://ubidots.com/docs/libraries/java.html
// http://ubidots.com/docs/devices/android.html
// https://github.com/ubidots/ubidots-java
public class MainActivity extends Activity {
   // private static final String BATTERY_LEVEL = "level";
    private TextView mnoise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mnoise = (TextView) findViewById(R.id.noiseLevel);
        new ApiUbidots().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    public class ApiUbidots extends AsyncTask<Integer, Void, Void> {
        private final String API_KEY = "b107bfa74890be5625245c25ad261cbeafa1f135";
        private final String VARIABLE_ID = "562db59376254264a1508b35"; // Sensor-1


        @Override
        protected Void doInBackground(Integer... params) {
            ApiClient apiClient = new ApiClient(API_KEY);
            Variable batteryLevel = apiClient.getVariable(VARIABLE_ID);


            final Value[] val = batteryLevel.getValues();
            String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").format(new Date(val[1].getTimestamp()));
            Log.i("info", dateString);
            runOnUiThread(new Runnable() {
                              @Override
                              public void run() {

                                  mnoise.setText(String.format("%.2f",val[1].getValue()) + "dB");
                              }
                          }

            );
            Log.i("info","val:"+val[1].getValue());
            return null;
        }
    }
}
