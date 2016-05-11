package com.rambo.marketposter.network;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.rambo.marketposter.application.MyApplication;
import com.rambo.marketposter.utils.Mylog;

import java.util.Map;
import java.util.Objects;

/**
 * Created by windy on 15/8/19.
 */
public class HttpService {
    public static final String TAG = HttpService.class.getSimpleName();

    public HttpService() {
        super();
    }


    //GET
    public void getStringRequest(final String url, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Mylog.d(TAG, "onResponse: " + s);
                listener.onResponse(s);
            }
        },
                errorListener
        );

        stringRequest.setShouldCache(false);


        stringRequest.setRetryPolicy(getRetryPolicy());
        MyApplication.instance.getRequestQueue().add(stringRequest);

    }


    //POST
    public void postStringRequest(final String url, final Map<String, String> map, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Mylog.d(TAG, "onResponse: " + s);
                listener.onResponse(s);
            }
        },
                errorListener
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return map;
            }
        };
        stringRequest.setShouldCache(false);

        stringRequest.setRetryPolicy(getRetryPolicy());
        MyApplication.instance.getRequestQueue().add(stringRequest);
    }


    public RetryPolicy getRetryPolicy() {
        RetryPolicy retryPolicy = new DefaultRetryPolicy(2500, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return retryPolicy;
    }
}
