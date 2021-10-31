package com.minorproject.polycurrenc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView output_amt_text;
    EditText input_amt_value;
    ImageView convert_button;
    Spinner input_curr, output_curr;
    String from, to, amt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output_amt_text = findViewById(R.id.output_amt);
        input_amt_value = findViewById(R.id.input_amt);
        convert_button = findViewById(R.id.convert_button);
        input_curr = findViewById(R.id.from_spinner);
        output_curr = findViewById(R.id.to_spinner);

        input_curr.setOnItemSelectedListener(this);
        output_curr.setOnItemSelectedListener(this);

        String[] currencies = getResources().getStringArray(R.array.currency_array);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies);
        input_curr.setAdapter(adapter);
        output_curr.setAdapter(adapter);

        OkHttpClient client = new OkHttpClient();


        convert_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amt = input_amt_value.getText().toString();
                 Request request = new Request.Builder()
                        .url("https://currency-converter-by-api-ninjas.p.rapidapi.com/v1/convertcurrency?have="+from+"&want="+to+"&amount="+amt)
                        .get()
                        .addHeader("x-rapidapi-host", "currency-converter-by-api-ninjas.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "c0d0ac06d1msh357a33595b3e794p1ecb7cjsn84d779d2e89d")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.d("TAG", "onFailure: "+e.getMessage());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if(response.isSuccessful()){
//                        Log.d("TAG", "onResponse: "+response.body().toString());
//                        JSONObject jsonObject = new JSONObject(response.body().toString());
//                        Log.d("TAG", "onResponse: "+jsonObject);
//                        String value = jsonObject.getString("new_amount");
                            final String str = response.body().string();
                            try {
                                JSONObject obj = new JSONObject(str);
                                amt = obj.getString("new_amount");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    output_amt_text.setText(amt);
                                }
                            });
//                        output_amt_text.setText(value);
//                        Toast.makeText(MainActivity.this, "Out"+value, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.from_spinner){
            from = parent.getItemAtPosition(position).toString();
        }
        if(parent.getId() == R.id.to_spinner){
            to = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}