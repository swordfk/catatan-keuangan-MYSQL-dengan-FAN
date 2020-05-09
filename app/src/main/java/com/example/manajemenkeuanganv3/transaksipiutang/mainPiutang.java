package com.example.manajemenkeuanganv3.transaksipiutang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.manajemenkeuanganv3.Helper.Config;
import com.example.manajemenkeuanganv3.R;
import com.example.manajemenkeuanganv3.transaksikeuangan.TambahKeuangan;
import com.example.manajemenkeuanganv3.transaksikeuangan.mainKeuangan;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class mainPiutang extends AppCompatActivity {
    TextView textPiutang, textHutang, textAset;
    ListView list_piutang;
    ArrayList<HashMap<String, String>> aruspiutang;
    SwipeRefreshLayout swipe_refresh;

    public static String id, status, jumlah, keterangan, tanggal, tanggal2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_piutang);

        textPiutang=findViewById(R.id.textPiutang);
        textHutang=findViewById(R.id.textHutang);
        textAset=findViewById(R.id.textAset);
        list_piutang=findViewById(R.id.recyclerview11);
        aruspiutang=new ArrayList<>();
        swipe_refresh=findViewById(R.id.swipe_refresh1);

        getSupportActionBar().setTitle("Piutang");

        FloatingActionButton fab = findViewById(R.id.buttonAdd1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mainPiutang.this, tambahPiutang.class);
                startActivity(i);
            }
        });
        readData();
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Handler untuk menjalankan jeda selama 5 detik
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {

                        // Berhenti berputar/refreshing
                        swipe_refresh.setRefreshing(false);

                        // fungsi-fungsi lain yang dijalankan saat refresh berhenti
                        recreate();
                    }
                }, 5000);
            }
        });
    }

    private void readData() {
        AndroidNetworking.post(Config.Host2+"read.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            NumberFormat rupiah = NumberFormat.getInstance(Locale.GERMANY);

                            textPiutang.setText(rupiah.format(response.getDouble("piutang")));
                            textHutang.setText(rupiah.format(response.getDouble("hutang")));
                            textAset.setText(rupiah.format(response.getDouble("aset")));

                            JSONArray jsonArray = response.getJSONArray("hasil1");
                            for (int i=0; i <jsonArray.length();i++){
                                JSONObject jsonObject;
                                jsonObject = jsonArray.getJSONObject(i);
                                HashMap<String, String> map = new HashMap<>();
                                map.put("id1", jsonObject.getString("id1"));
                                map.put("status1", jsonObject.getString("status1"));
                                map.put("jumlah1", jsonObject.getString("jumlah1"));
                                map.put("nama1", jsonObject.getString("nama1"));
                                map.put("tanggal1", jsonObject.getString("tanggal1"));
                                map.put("tanggal21", jsonObject.getString("tanggal21"));

                                aruspiutang.add(map);
                            }
                            adapterRead();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("ReadData", "onError: " + error);
                    }
                });
    }

    private void adapterRead() {
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, aruspiutang, R.layout.listpiutang,
                new String[]{"id1", "status1", "jumlah1","nama1", "tanggal1","tanggal21"},
                new int[]{R.id.text_transaksi_id1, R.id.text_status1, R.id.text_jumlah1, R.id.text_nama1, R.id.text_tanggal1,R.id.text_tanggal21}
        );
        list_piutang.setAdapter(simpleAdapter);
    }
}
