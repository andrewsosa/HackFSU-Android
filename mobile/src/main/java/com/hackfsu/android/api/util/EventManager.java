package com.hackfsu.android.api.util;

import android.app.Activity;
import android.util.Log;

import com.hackfsu.android.api.API;
import com.hackfsu.android.api.JudgeAPI;
import com.hackfsu.android.api.RetroAPI;
import com.hackfsu.android.api.templates.EventsResponse;
import com.hackfsu.android.api.templates.HacksResponse;
import com.hackfsu.android.app.BuildConfig;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Randy Bruno-Piverger on 2/28/2018.
 */

public class EventManager extends API {

    final static String API_HOST = BuildConfig.API_HOST;

    public EventManager(Activity mActivity) {
        super(mActivity);
    }


    public interface OnAssignmentRetrievedListener {
        void onAssignment(ArrayList<String> eventList);
        void onFailure();
    }



    public void getHackerEvents(final EventManager.OnAssignmentRetrievedListener listener) {

        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new AddCookiesInterceptor(mActivity)); // VERY VERY IMPORTANT
        builder.addInterceptor(new ReceivedCookiesInterceptor(mActivity)); // VERY VERY IMPORTANT
        client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_HOST)
                .client(client) // VERY VERY IMPORTANT
                .addConverterFactory(GsonConverterFactory.create())
                .build(); // REQUIRED

        RetroAPI mapi = retrofit.create(RetroAPI.class);
        Call<EventsResponse> call = mapi.GetEvents(new AddCookiesInterceptor(mActivity));


        call.enqueue(new Callback<EventsResponse>() {

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    Log.d("RequestCall", "Request Successful");
                    Log.d(this.getClass().getName(), "Response code: " + response.code());

                    try {

                        //".events" should be the ArrayList of events returned from the call
                        // I couldn't reference getEvents here for what ever reason.
                        listener.onAssignment(response.body().events);
                        return;
                    }
                    catch (NullPointerException e) {
                        Log.e(JudgeAPI.class.getName(), e.getLocalizedMessage());
                    }

                } else {
                    Log.e("RequestCall", "Request failed");

                }

                listener.onFailure();

            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                Log.e("RequestCall", "Request failed");
                listener.onFailure();
            }
        });

    }


}
