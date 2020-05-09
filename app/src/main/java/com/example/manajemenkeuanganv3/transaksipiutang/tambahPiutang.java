package com.example.manajemenkeuanganv3.transaksipiutang;

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
import com.example.manajemenkeuanganv3.transaksikeuangan.TambahKeuangan;

import org.json.JSONException;
import org.json.JSONObject;

public class tambahPiutang extends AppCompatActivity {
    Button add1;
    EditText edTextJumlah1, edtextnama1;
    RadioGroup radioPiutang1;
    RadioButton selected1;
    String status1,jumlah1,nama1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_piutang);

        add1=findViewById(R.id.btn_simpan11);
        edTextJumlah1=findViewById(R.id.edit_jumlah11);
        edtextnama1=findViewById(R.id.edit_nama11);
        radioPiutang1=findViewById(R.id.radio_status11);
        getSupportActionBar().setTitle("Tambah");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    int selectedd = radioPiutang1.getCheckedRadioButtonId();
                    selected1 = findViewById(selectedd);
                    status1 = selected1.getText().toString();
                    jumlah1=edTextJumlah1.getText().toString();
                    nama1=edtextnama1.getText().toString();
                    simpanDataPiutang();
            }
        });
    }

    private void simpanDataPiutang() {
        AndroidNetworking.post(Config.Host2+"create.php")
                .addBodyParameter("status1", status1)
                .addBodyParameter("jumlah1", jumlah1)
                .addBodyParameter("nama1", nama1)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            if(response.getString("response").equals("succes")){
                                Toast.makeText(tambahPiutang.this,"Berhasil diSimpan",Toast.LENGTH_LONG).show();
                                //finish();
                            }else {
                                Toast.makeText(tambahPiutang.this,response.getString("response"),Toast.LENGTH_LONG).show();
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
