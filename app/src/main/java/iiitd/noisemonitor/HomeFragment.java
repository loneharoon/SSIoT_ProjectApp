package iiitd.noisemonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ubidots.ApiClient;
import com.ubidots.Value;
import com.ubidots.Variable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by garvitab on 24-11-2015.
 */
public class HomeFragment extends android.app.Fragment {

	private TextView mnoise1,mnoise2;
	private Button start,stop;
	Context mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		setRetainInstance(true);

		mContext=getActivity();
		View view= inflater.inflate(R.layout.content_main, container, false);

		mnoise1 = (TextView) view.findViewById(R.id.noiseLevel);
		mnoise2 = (TextView) view.findViewById(R.id.noiseLevel2);
		start=(Button)view.findViewById(R.id.button);
		stop=(Button)view.findViewById(R.id.button2);

		start.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				mHandlerTask.run();
				Toast.makeText(mContext,"Fetching sensor readings now",Toast.LENGTH_SHORT).show();

//				Buffer buf = new CircularFifoBuffer(4);

				}
		});

		stop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mHandler.removeCallbacks(mHandlerTask);
				Toast.makeText(mContext,"Sensor readings stopped",Toast.LENGTH_SHORT).show();
			}
		});
		new ApiUbidots().execute();
		return view;
	}

	private final static int INTERVAL = 1000 * 2; //5 seconds
	Handler mHandler=new Handler();

	Runnable mHandlerTask = new Runnable()
	{
		@Override
		public void run() {
			new ApiUbidots().execute();
			mHandler.postDelayed(mHandlerTask, INTERVAL);
		}
	};

	public class ApiUbidots extends AsyncTask<Void, Void, Void> {
		private final String API_KEY = "bd80d5dd9a1f015793fb0ed7686b428b3558e8fb";
		private final String VARIABLE_ID_S1 = "565d5ab87625421e43701aa6"; // Sensor-1
		private final String VARIABLE_ID_S2 = "565d5aca7625421ecae0d25f"; // Sensor-2
		NotificationManager notificationManager =(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		public ApiUbidots(){
		}
		@Override
		protected Void doInBackground(Void... params) {

			ApiClient apiClient = new ApiClient(API_KEY);
			Variable noiseLevel = apiClient.getVariable(VARIABLE_ID_S1);
			Variable noiseLevel2 = apiClient.getVariable(VARIABLE_ID_S2);

			final Value[] val = noiseLevel.getValues();
			final Value[] val2 = noiseLevel2.getValues();
			String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").format(new Date(val[ 1 ].getTimestamp()));
			Log.i("info", dateString);
			getActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												mnoise1.setText(String.format("%.5f", val[ 1 ].getValue()) + "dB");
												mnoise2.setText(String.format("%.5f", val2[ 1 ].getValue()) + "dB");

												if(val[1].getValue()<13.5 || val[1].getValue()>14.2 || val2[1].getValue()<13.5 || val2[1].getValue()>14.2){
//													Toast.makeText(mContext,"WARNING!",Toast.LENGTH_SHORT).show();
													NotificationCompat.Builder builder =
															new NotificationCompat.Builder(mContext)
																	.setSmallIcon(R.drawable.user82)
																	.setContentTitle("Loud noise")
																	.setContentText("Warning! You are being surrounded by high noise levels")
																	.setAutoCancel(true)
																	.setStyle(new NotificationCompat.BigTextStyle())
																	.setDefaults(Notification.DEFAULT_SOUND)
																	.setWhen(System.currentTimeMillis());

													notificationManager.notify(1111, builder.build());
												}
											}
										}
			);
			Log.i("info", "value from sensor 1:" + val[ 1 ].getValue());
			Log.i("info","value from sensor 2:"+val2[2].getValue());
			Log.i("refreshed","values fetched again");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.d("Ubidots", "inside onPostExecute now");
		}
	}



	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onStop() {

		super.onStop();
	}
}
