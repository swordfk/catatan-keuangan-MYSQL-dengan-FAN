package com.example.manajemenkeuanganv3.transaksikeuangan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.manajemenkeuanganv3.Helper.Config;
import com.example.manajemenkeuanganv3.R;

import org.json.JSONException;
import org.json.JSONObject;

public class TambahKeuangan extends AppCompatActivity {
    Button add;
    EditText edTextJumlah, edtextKet;
    RadioGroup radioKeuangan;
    RadioButton selected;
    String status,jumlah,keterangan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambahkeuangan);

        add=findViewById(R.id.btn_simpan1);
        edTextJumlah=findViewById(R.id.edit_jumlah1);
        edtextKet=findViewById(R.id.edit_keterangan1);
        radioKeuangan=findViewById(R.id.radio_status1);
        getSupportActionBar().setTitle("Tambah");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selested = radioKeuangan.getCheckedRadioButtonId();
                selected = findViewById(selested);
                status = selected.getText().toString();
                jumlah=edTextJumlah.getText().toString();
                keterangan=edtextKet.getText().toString();
                simpanData();
            }
        });
    }

    private void simpanData(){
        AndroidNetworking.post(Config.Host+"create.php")
                .addBodyParameter("status", ""+status)
                .addBodyParameter("jumlah", ""+jumlah)
                .addBodyParameter("keterangan", ""+keterangan)
                .setPriority(Priority.MEDIUM)
                .setTag("Tambah Data")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            if(response.getString("response").equals("succes")){
                                Toast.makeText(TambahKeuangan.this,"Berhasil diSimpan",Toast.LENGTH_LONG).show();
                                finish();
                            }else {
                                Toast.makeText(TambahKeuangan.this,response.getString("response"),Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("ErrorTambahData",""+error.getErrorDetail());
                    }
                });
    }
}
