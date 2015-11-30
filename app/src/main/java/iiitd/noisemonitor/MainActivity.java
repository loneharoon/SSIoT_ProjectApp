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
    private TextView mnoise1,mnoise2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mnoise1 = (TextView) findViewById(R.id.noiseLevel);
        mnoise2 = (TextView) findViewById(R.id.noiseLevel2);
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
        private final String VARIABLE_ID_S1 = "562db59376254264a1508b35"; // Sensor-1
        private final String VARIABLE_ID_S2 = "563c8b9e762542212f42d19a"; // Sensor-2

        public ApiUbidots(){

        }
        @Override
        protected Void doInBackground(Integer... params) {

            ApiClient apiClient = new ApiClient(API_KEY);
            Variable noiseLevel = apiClient.getVariable(VARIABLE_ID_S1);
            Variable noiseLevel2 = apiClient.getVariable(VARIABLE_ID_S2);


            final Value[] val = noiseLevel.getValues();
            final Value[] val2=noiseLevel2.getValues();
            String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").format(new Date(val[1].getTimestamp()));
            Log.i("info", dateString);
            runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  mnoise1.setText(String.format("%.5f", val[ 1 ].getValue()) + "dB");
                                  mnoise2.setText(String.format("%.5f", val2[ 1 ].getValue()) + "dB");
                              }
                          }
            );
            Log.i("info", "value from sensor 1:" + val[ 1 ].getValue());
            Log.i("info","value from sensor 2:"+val2[2].getValue());
            return null;
        }
    }
}
