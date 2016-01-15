package com.aubbsapps.weatherman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;


public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentTemp mCurrentTemp;
    private ColorWheel mColorWheel = new ColorWheel();


    @Bind(R.id.timeText) TextView mTimeText;
    @Bind(R.id.cityText) TextView mCityText;
    @Bind(R.id.weatherImage) ImageView mWeatherImage;
    @Bind(R.id.temperatureLabel) TextView mTemperatureLabel;
    @Bind(R.id.refreshImage) ImageView mRefreshImage;
    @Bind(R.id.humidityTextVeiw) TextView mHumidityTextVeiw;
    @Bind(R.id.percprainText) TextView mPercprainText;
    @Bind(R.id.Colorbutton) Button mColorButton;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.RelativeLayout) RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
     //natural state of progress bar
        mProgressBar.setVisibility(View.INVISIBLE);

        mRefreshImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast();
            }
        });
        getForecast();

        mColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = mColorWheel.getColor();
                mRelativeLayout.setBackgroundColor(color);

                mColorButton.setTextColor(color);
            }

        });


    }
//some changes a



    private void getForecast() {
        //more parameters for our URL variable
        String apiKey = "9fa5348fe6303cda3a39748bc4c1866b";
        String forecastURL = "https://api.forecast.io/forecast/" + apiKey + "/37.8267,-122.423";

        if (isNetworkAvailable()) {
            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            //request data to URL
            Request request = new Request.Builder()
                    .url(forecastURL)
                    .build();
          //calls or get data to use
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertuserwhenerror();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {
                        String JsonData = response.body().string();
                        Log.v(TAG, JsonData);
                        if (response.isSuccessful()) {
                            mCurrentTemp = getCurrentDetails(JsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UpdateInterface();
                                }
                            });
                        } else {
                            alertuserwhenerror();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        }
        else {
            Toast.makeText(this, getString(R.string.Network_Error),
                    Toast.LENGTH_LONG).show();
        }
    }
    /*--------------------------------------------Methods---------------------------------------------------------------------*/
   //if refresh button is click what happens to it
    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImage.setVisibility(View.INVISIBLE);

        } else{
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImage.setVisibility(View.VISIBLE);

        }

    }
    private void UpdateInterface() {
        mTemperatureLabel.setText(mCurrentTemp.getTemperature() + "");
        mTimeText.setText("It is " + mCurrentTemp.getFormatTime());
        mCityText.setText(mCurrentTemp.getTimeZone());
        mHumidityTextVeiw.setText(mCurrentTemp.getHumidity() + "");
        mPercprainText.setText(mCurrentTemp.getPrecipChance() + "%");


        Drawable drawable = getResources().getDrawable(mCurrentTemp.getIconId());
        mWeatherImage.setImageDrawable(drawable);

    }

    private CurrentTemp getCurrentDetails(String JsonData) throws JSONException {
            JSONObject forecast = new JSONObject(JsonData);
            String timezone = forecast.getString("timezone");
            Log.i(TAG, "Fromm Json: " + timezone);

            JSONObject currently = forecast.getJSONObject("currently");
            CurrentTemp currentTemp = new CurrentTemp();
            //setting and getting Json data from json doc
            currentTemp.setHumidity(currently.getDouble("humidity"));
            currentTemp.setIcon(currently.getString("icon"));
            currentTemp.setPrecipChance(currently.getDouble("precipProbability"));
            currentTemp.setTemperature(currently.getDouble("temperature"));
            currentTemp.setTime(currently.getInt("time"));
            currentTemp.setSummary(currently.getString("summary"));
            currentTemp.setTimeZone(timezone);

            Log.d(TAG, currentTemp.getFormatTime());


        return currentTemp ;
    }





    //checks if there is a network and if it is connected then it will run the isNetworkAvailable
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
         return isAvailable;
    }
    private void alertuserwhenerror() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }





}








