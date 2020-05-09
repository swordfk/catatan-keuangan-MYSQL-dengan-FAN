package com.example.manajemenkeuanganv3.transaksikeuangan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.manajemenkeuanganv3.Helper.Config;
import com.example.manajemenkeuanganv3.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;
import com.zhuandian.rippleview.RippleView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class mainKeuangan extends AppCompatActivity {
    TextView textCashIn, textCashOut, textBalance,text_filter;
    ListView list_keuangan;
    ArrayList<HashMap<String, String>> arusuang;
    SwipeRefreshLayout swipe_refresh;

    public static String id, status, jumlah, keterangan, tanggal, tanggal2, tgl_dari, tgl_ke,link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_keuangan);

        textCashIn=findViewById(R.id.textCashIn);
        textCashOut=findViewById(R.id.textCashOut);
        textBalance=findViewById(R.id.textBalance);
        list_keuangan=findViewById(R.id.recyclerview1);
        arusuang= new ArrayList<>();
        text_filter=findViewById(R.id.text_filter);
        swipe_refresh=findViewById(R.id.swipe_refresh);
        getSupportActionBar().setTitle("Keuangan");
        link=Config.Host+"read.php";

        FloatingActionButton fab = findViewById(R.id.buttonAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mainKeuangan.this,TambahKeuangan.class);
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

    private void readData(){
        arusuang.clear();
        list_keuangan.setAdapter(null);
        AndroidNetworking.post(link)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            NumberFormat rupiah = NumberFormat.getInstance(Locale.GERMANY);

                            textCashIn.setText(rupiah.format(response.getDouble("masuk")));
                            textCashOut.setText(rupiah.format(response.getDouble("keluar")));
                            textBalance.setText(rupiah.format(response.getDouble("masuk")-response.getDouble("keluar")));

                            JSONArray jsonArray = response.getJSONArray("hasil");
                            for (int i=0; i <jsonArray.length();i++){
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

    private void adapterRead(){
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, arusuang, R.layout.listkeuangan,
                new String[]{"id", "status", "jumlah","keterangan", "tanggal","tanggal2"},
                new int[]{R.id.text_transaksi_id, R.id.text_status, R.id.text_jumlah, R.id.text_keterangan, R.id.text_tanggal,R.id.text_tanggal2}
        );
        list_keuangan.setAdapter(simpleAdapter);
        list_keuangan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long iD) {
                id = ((TextView)view.findViewById(R.id.text_transaksi_id)).getText().toString();
                status = ((TextView)view.findViewById(R.id.text_status)).getText().toString();
                jumlah = ((TextView)view.findViewById(R.id.text_jumlah)).getText().toString();
                keterangan = ((TextView)view.findViewById(R.id.text_keterangan)).getText().toString();
                tanggal = ((TextView)view.findViewById(R.id.text_tanggal)).getText().toString();
                tanggal2 = ((TextView)view.findViewById(R.id.text_tanggal2)).getText().toString();

                ListMenu();
            }
        });
    }

    private void ListMenu(){
        final Dialog dialog = new Dialog(mainKeuangan.this);
        dialog.setContentView(R.layout.listmenu);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RippleView rip_hapus = dialog.findViewById(R.id.rip_hapus);
        RippleView rip_edit = dialog.findViewById(R.id.rip_edit);
        rip_hapus.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
                hapusdata();
            }
        });
        rip_edit.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
                startActivity(new Intent(mainKeuangan.this, editActivity.class));
            }
        });

        dialog.show();
    }
    private void hapusdata(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah Anda Yakin Menghapus Data Ini?");
        builder.setPositiveButton(
                "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        AndroidNetworking.post(Config.Host+"delete.php")
                                .addBodyParameter("id", id)
                                .setPriority(Priority.MEDIUM)
                                .setTag("Delete Data")
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // do anything with response
                                        try {
                                            if(response.getString("response").equals("succes")){

                                                Toast.makeText(getApplicationContext(), "Data Berhasil Dihapus", Toast.LENGTH_LONG).show();
                                                readData();
                                            }else {
                                                Toast.makeText(mainKeuangan.this,response.getString("response"),Toast.LENGTH_LONG).show();
                                            }
                                        }catch (JSONException e){
                                            e.printStackTrace();
                                        }
                                    }
                                    @Override
                                    public void onError(ANError error) {
                                        // handle error
                                        Log.d("ErrorHapusData",""+error.getErrorDetail());
                                    }
                                });
                    }
                });
        builder.setNegativeButton(
                "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            filter();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void filter(){
        SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                    @Override
                    public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                               int yearStart, int monthStart,
                                               int dayStart, int yearEnd,
                                               int monthEnd, int dayEnd) {
                        // grab the date range, do what you want
                        tgl_dari = String.valueOf(yearStart) + "-" + String.valueOf(monthStart+1) + "-" + String.valueOf(dayStart);
                        tgl_ke = String.valueOf(yearEnd) + "-" + String.valueOf(monthEnd+1) + "-" + String.valueOf(dayEnd);

                        text_filter.setText(String.valueOf(dayStart) + "/" + String.valueOf(monthStart+1) + "/" + String.valueOf(yearStart)
                                +" - "+ String.valueOf(dayEnd) + "/" + String.valueOf(monthEnd+1) + "/" + String.valueOf(yearEnd)
                        );
                        link=Config.Host+"filter.php?dari="+tgl_dari+"&ke="+tgl_ke;
                        readData();
                    }
                });

        smoothDateRangePickerFragment.show(getFragmentManager(), "smoothDateRangePicker");
    }
}
