package com.hctpbl.biovoiceapp.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class APIHandler {
	
	private static final String TAG = "APIHandler";

    private static HttpURLConnection openConnection(URL apiURL) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)apiURL.openConnection();
        connection.setConnectTimeout(2000);
        connection.setReadTimeout(8000);

        return connection;
    }

    private static String getResponse(HttpURLConnection connection) throws IOException {

        StringBuilder response = new StringBuilder();
        InputStream is;
        if (connection.getResponseCode() >= 400) {
            is = connection.getErrorStream();
        } else {
            is = connection.getInputStream();
        }
        Scanner inStream = new Scanner(is);

        while (inStream.hasNextLine()) {
            response.append(inStream.nextLine());
        }

        inStream.close();

        return response.toString();
    }

    public static JSONObject getData(Context ctx, URL apiURL) throws IOException, JSONException, APIConnException {

        Log.i(TAG, apiURL.toString());

        if (!canCommunicate(ctx)) throw new APIConnException();

        HttpURLConnection apiConn = openConnection(apiURL);

        String response = getResponse(apiConn);

        Log.i(TAG, response);

        apiConn.disconnect();

        return new JSONObject(response);


    }

	public static <K, V> JSONObject postData(Context ctx, URL apiURL, Map<K, V> data) throws IOException, JSONException, APIConnException {

        if (!canCommunicate(ctx)) throw new APIConnException();

        HttpURLConnection apiConn = openConnection(apiURL);
		apiConn.setDoOutput(true);
		apiConn.setRequestMethod("POST");
		
		String attURL = getAttributeString(data);
		Log.i(TAG, attURL);
		apiConn.setFixedLengthStreamingMode(attURL.getBytes().length);
		apiConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		PrintWriter out = new PrintWriter(apiConn.getOutputStream());
		out.print(attURL);
		out.close();

        String response = getResponse(apiConn);

		Log.i(TAG, response);

        apiConn.disconnect();
		
		return new JSONObject(response);
	}
	
	private static <K, V> String getAttributeString(Map<K, V> data) {
		StringBuilder attURL = new StringBuilder();
		for (Map.Entry<K, V> entry : data.entrySet()) {
			attURL.append(entry.getKey().toString() + "=" + entry.getValue().toString() + "&");
		}
		return attURL.substring(0, attURL.length()-1);
	}

    private static boolean canCommunicate(Context ctx) {
        ConnectivityManager connMgr = (ConnectivityManager)
                ctx.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
