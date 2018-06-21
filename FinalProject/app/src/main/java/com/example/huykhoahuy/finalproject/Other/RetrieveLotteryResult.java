package com.example.huykhoahuy.finalproject.Other;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.huykhoahuy.finalproject.Class.Lottery;
import com.example.huykhoahuy.finalproject.Class.LotteryResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RetrieveLotteryResult extends AsyncTask<Void, Void, String> {

    private Exception exception;
    private ProgressBar progressBar;
    private String lottery_province_id;
    private String lottery_date;
    private Lottery lottery;
    private View view;

    private String minorStringProcessing(String date) {
        return date.replace("/", "-");
    }

    private static final String API_KEY = "5b0cf5d828e03";
    private static final String API_URL = "https://laythongtin.net/mini-content/lottery-all-api.php?type=json";

    public RetrieveLotteryResult(Lottery lottery, View view,ProgressBar progressBar) {
        this.lottery_province_id = lottery.getLoterry_Province_ID();
        this.lottery_date = this.minorStringProcessing(lottery.getLottery_Date());
        this.lottery = lottery;
        this.progressBar=progressBar;
        this.view = view;
    }

    public void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public String doInBackground(Void... urls) {

        // Do some validation here
        try {
            URL url = new URL(String.format(API_URL + "&key=" + API_KEY + "&location="
                    + lottery_province_id + "&date=" + lottery_date));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    private JSONObject getJSONTokener(String JSONResponse) {
        JSONObject reader = null;
        try {
            reader = (JSONObject) new JSONTokener(JSONResponse).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reader;
    }

    private JSONObject getTheIthJSON(JSONObject reader, int i) {
        JSONObject temp = null;
        try {
            temp = reader.getJSONObject(String.valueOf(i));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    private String getTheIthResult(JSONObject reader, int i) {
        JSONObject jsonObject = getTheIthJSON(reader, i);
        String result = null;
        try {
            result = jsonObject.getString("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private ArrayList<String> extractResultListFromJSON(String JSONResponse) {
        // Precondition: Suppose that JSONResponse != null

        ArrayList<String> listResults = new ArrayList<String>();
        JSONObject reader = this.getJSONTokener(JSONResponse);

        int nPrizes = 18;
        for (int i = 0; i < nPrizes; i++) {
            String result = this.getTheIthResult(reader, i);
            listResults.add(result);
        }
        return listResults;
    }

    public void onPostExecute(String response) {
        progressBar.setVisibility(View.GONE);
        ArrayList<String> listResults = new ArrayList<String>();
        if (response == null) {
            response = "THERE WAS AN ERROR";
            Log.i("NOINFO", response);
            Toast.makeText(this.view.getContext(), "Cannot retrieve the information!", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.i("INFO", response);
            listResults = this.extractResultListFromJSON(response);
            if (listResults != null && listResults.size() == 18) {
                this.showToast(listResults);
            }
            else {
                Toast.makeText(this.view.getContext(), "Cannot retrieve the information!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showToast(ArrayList<String> listResults) {
        this.lottery.checkResult(listResults);

        Context context = this.view.getContext();
        String prize = lottery.getLottery_Prize();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, prize, duration);
        toast.show();
    }
}