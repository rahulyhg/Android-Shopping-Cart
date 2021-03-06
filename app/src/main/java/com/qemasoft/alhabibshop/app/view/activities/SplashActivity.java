package com.qemasoft.alhabibshop.app.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.qemasoft.alhabibshop.app.AppConstants;
import com.qemasoft.alhabibshop.app.Preferences;
import com.qemasoft.alhabibshop.app.R;
import com.qemasoft.alhabibshop.app.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.qemasoft.alhabibshop.app.AppConstants.CURRENCY_KEY;
import static com.qemasoft.alhabibshop.app.AppConstants.CURRENCY_SYMBOL_KEY;
import static com.qemasoft.alhabibshop.app.AppConstants.GET_KEY;
import static com.qemasoft.alhabibshop.app.AppConstants.KEY_FOR_KEY;
import static com.qemasoft.alhabibshop.app.AppConstants.LANGUAGE_KEY;
import static com.qemasoft.alhabibshop.app.AppConstants.LOGO_KEY;
import static com.qemasoft.alhabibshop.app.AppConstants.SECRET_KEY_FILE;
import static com.qemasoft.alhabibshop.app.AppConstants.SECRET_KEY_URL;
import static com.qemasoft.alhabibshop.app.AppConstants.SET_KEY;
import static com.qemasoft.alhabibshop.app.AppConstants.SPLASH_REQUEST_CODE;
import static com.qemasoft.alhabibshop.app.AppConstants.appContext;
import static com.qemasoft.alhabibshop.app.AppConstants.setHomeExtra;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private static Context context;
    private Utils utils;
    private int clicks = 0;

    public static Context getAppContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = getApplicationContext();
        this.utils = new Utils(this);

        LinearLayout splash = (LinearLayout) findViewById(R.id.splash_layout);
        splash.setOnClickListener(this);


        AndroidNetworking.initialize(context);

        if (utils.isNetworkConnected()) {
            AndroidNetworking.post(SECRET_KEY_URL)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            boolean success = response.optBoolean("success");
                            if (success) {
                                AppConstants.setMidFixApi("home");
                                String secretKey = response.optString(SECRET_KEY_FILE);
                                String keyVal = GET_KEY(context, KEY_FOR_KEY);
                                utils.printLog("StoredKey", "Key = " + keyVal);
                                if (keyVal.isEmpty() || keyVal.length() < 1) {
                                    utils.printLog("StoringKey", "Success");
                                    SET_KEY(KEY_FOR_KEY, secretKey);
                                    utils.printLog("KeyStored", "Success");
                                }
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("hasParameters", false);
                                Intent intent = new Intent(SplashActivity.this, FetchData.class);
                                intent.putExtras(bundle);
                                startActivityForResult(intent, SPLASH_REQUEST_CODE);
                            } else {
                                utils.printLog("Splash", "Success False");
                                utils.showAlertDialog("Invalid Request!", "No Relevant Record Found");
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            utils.showErrorDialog("Error Getting Data From Server");
                            utils.showToast("ErrorGettingDataFromServer");
                        }
                    });
        } else {
            utils.showAlertDialogTurnWifiOn();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SPLASH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    final JSONObject response = new JSONObject(data.getStringExtra("result"));
                    boolean success = response.optBoolean("success");
                    if (success) {
                        JSONObject homeObject = response.getJSONObject("home");
                        JSONObject settingObject = homeObject.optJSONObject("setting");
                        int width = Utils.getScreenWidth(appContext);
                        String logoPath;
                        if (width <= 480) {
                            logoPath = settingObject.optString("logo_small");
                        } else {
                            logoPath = settingObject.optString("logo");
                        }
                        String language = settingObject.optString("language");
                        String currency = settingObject.optString("currency");

                        Preferences.setSharedPreferenceString(appContext,
                                LOGO_KEY, logoPath);
                        Preferences.setSharedPreferenceString(appContext,
                                LANGUAGE_KEY, language);
                        Preferences.setSharedPreferenceString(appContext,
                                CURRENCY_KEY, currency);
                        String symbol = settingObject.optString("symbol");

                        Preferences.setSharedPreferenceString(appContext,
                                CURRENCY_SYMBOL_KEY, symbol);
                        utils.printLog("Symbol", "Symbol = "
                                +Preferences.getSharedPreferenceString(appContext
                                ,CURRENCY_SYMBOL_KEY,""));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra(MainActivity.KEY_EXTRA, response.toString());
                                startActivity(intent);
                                setHomeExtra(response.toString());
                            }
                        }, 700);
                    } else {
                        utils.showErrorDialog("Server Response is False!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                utils.printLog("RequestCanceled", "Canceled");
            }
        }
    }

    @Override
    public void onClick(View v) {
        clicks++;
        if (clicks % 2 == 0) {
            recreate();
        }
    }
}
