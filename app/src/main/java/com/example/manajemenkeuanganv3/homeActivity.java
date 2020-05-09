package com.example.manajemenkeuanganv3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.manajemenkeuanganv3.Helper.Config;
import com.example.manajemenkeuanganv3.transaksikeuangan.mainKeuangan;
import com.example.manajemenkeuanganv3.transaksipiutang.mainPiutang;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class homeActivity extends AppCompatActivity {
    TextView textBalance,inout,textBalancep,piutang;
    ArrayList<HashMap<String, String>> arusuang, arusPiutang;
    ListView list_keuangan, list_piutang;

    public static String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textBalance=findViewById(R.id.textBalance);
        textBalancep=findViewById(R.id.textBalanceP);
        list_keuangan=findViewById(R.id.listHome);
        list_piutang=findViewById(R.id.listHome1);
        arusuang= new ArrayList<>();
        arusPiutang= new ArrayList<>();
        inout=findViewById(R.id.inout);
        piutang=findViewById(R.id.piutang);
        piutang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), mainPiutang.class));
            }
        });
        inout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), mainKeuangan.class));
            }
        });
        readDataBalance();
        readBalanceP();
        list_keuangan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        list_piutang.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

    }

    private void readDataBalance(){
        AndroidNetworking.post(Config.Host+"read.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            NumberFormat rupiah = NumberFormat.getInstance(Locale.GERMANY);
                            textBalance.setText(rupiah.format(response.getDouble("masuk")-response.getDouble("keluar")));

                            JSONArray jsonArray = response.getJSONArray("hasil");
                            for (int i=0; i <2;i++) {
                                JSONObject jsonObject;
                                jsonObject = jsonArray.getJSONObject(i);
                                HashMap<String, String> map = new HashMap<>();
                                map.put("id", jsonObject.getString("id"));
                                map.put("status", jsonObject.getString("status"));
                                map.put("jumlah", jsonObject.getString("jumlah"));
                                map.put("keterangan", jsonObject.getString("keterangan"));
                                map.put("tanggal", jsonObject.getString("tanggal"));
                                map.put("tanggal2", jsonObject.getString("tanggal2"));

                                arusuang.add(map);
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

    private  void readBalanceP(){
        AndroidNetworking.post(Config.Host2+"read.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            NumberFormat rupiah = NumberFormat.getInstance(Locale.GERMANY);
                            textBalancep.setText(rupiah.format(response.getDouble("aset")));

                            JSONArray jsonArray = response.getJSONArray("hasil1");
                            for (int i=0; i <2;i++){
                                JSONObject jsonObject;
                                jsonObject = jsonArray.getJSONObject(i);
                                HashMap<String, String> map = new HashMap<>();
                                map.put("id1", jsonObject.getString("id1"));
                                map.put("status1", jsonObject.getString("status1"));
                                map.put("jumlah1", jsonObject.getString("jumlah1"));
                                map.put("nama1", jsonObject.getString("nama1"));
                                map.put("tanggal1", jsonObject.getString("tanggal1"));
                                map.put("tanggal21", jsonObject.getString("tanggal21"));

                                arusPiutang.add(map);
                            }
                            adapterReadP();
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
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, arusuang, R.layout.listkeuangan,
                new String[]{"id", "status", "jumlah","keterangan", "tanggal","tanggal2"},
                new int[]{R.id.text_transaksi_id, R.id.text_status, R.id.text_jumlah, R.id.text_keterangan, R.id.text_tanggal,R.id.text_tanggal2});
        list_keuangan.setAdapter(simpleAdapter);
    }

    private void adapterReadP() {
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, arusPiutang, R.layout.listpiutang,
                new String[]{"id1", "status1", "jumlah1","nama1", "tanggal1","tanggal21"},
                new int[]{R.id.text_transaksi_id1, R.id.text_status1, R.id.text_jumlah1, R.id.text_nama1, R.id.text_tanggal1,R.id.text_tanggal21}
        );
        list_piutang.setAdapter(simpleAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            startActivity(new Intent(getApplicationContext(), aboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
