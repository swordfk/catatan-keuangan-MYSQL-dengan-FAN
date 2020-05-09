package com.example.manajemenkeuanganv3.transaksikeuangan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.manajemenkeuanganv3.Helper.Config;
import com.example.manajemenkeuanganv3.Helper.CurrentDate;
import com.example.manajemenkeuanganv3.R;
import com.zhuandian.rippleview.RippleView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class editActivity extends AppCompatActivity {
    RadioGroup radio_status;
    RadioButton radio_masuk, radio_keluar;
    EditText edit_jumlah, edit_keterangan, edit_tanggal;
    Button btn_simpan;
    RippleView rip_simpan;
    String status, tanggal;

    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        radio_status        = findViewById(R.id.radio_status);
        radio_masuk         = findViewById(R.id.radio_masuk);
        radio_keluar        = findViewById(R.id.radio_keluar);
        edit_jumlah         = findViewById(R.id.edit_jumlah);
        edit_keterangan     = findViewById(R.id.edit_keterangan);
        edit_tanggal        = findViewById(R.id.edit_tanggal);
        btn_simpan          = findViewById(R.id.btn_simpan);
        rip_simpan          = findViewById(R.id.rip_simpan);
        getSupportActionBar().setTitle("Edit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        status = mainKeuangan.status;
        switch (status){
            case "MASUK": radio_masuk.setChecked(true);
                break;
            case "KELUAR": radio_keluar.setChecked(true);
                break;
        }

        radio_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_masuk: status = "MASUK";
                        break;
                    case  R.id.radio_keluar: status = "KELUAR";
                        break;
                }
            }
        });

        edit_jumlah.setText(mainKeuangan.jumlah);
        edit_keterangan.setText(mainKeuangan.keterangan);
        tanggal = mainKeuangan.tanggal2;
        edit_tanggal.setText(mainKeuangan.tanggal);
        edit_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(editActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");

                        tanggal = year + "-" + numberFormat.format(month +1) + "-" + numberFormat.format(dayOfMonth);
                        Log.e(" tanggal", tanggal);

                        edit_tanggal.setText(numberFormat.format(dayOfMonth) + "/" + numberFormat.format(month+1) +
                                "/"  + year);

                    }
                }, CurrentDate.year, CurrentDate.month, CurrentDate.day);
                datePickerDialog.show();
            }
        });

        rip_simpan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (status.equals("") || edit_jumlah.getText().toString().equals("") || edit_keterangan.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Isi Data Dengan Benar",
                            Toast.LENGTH_LONG).show();
                }else {
                    AndroidNetworking.post(Config.Host+"update.php")
                            .addBodyParameter("id", mainKeuangan.id)
                            .addBodyParameter("status", status)
                            .addBodyParameter("jumlah", edit_jumlah.getText().toString())
                            .addBodyParameter("keterangan", edit_keterangan.getText().toString())
                            .addBodyParameter("tanggal", tanggal)
                            .setPriority(Priority.MEDIUM)
                            .setTag("Update Data")
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // do anything with response
                                    try {
                                        if(response.getString("response").equals("succes")){

                                            Toast.makeText(editActivity.this, "Perubahan Berehasil Disimpan",
                                                    Toast.LENGTH_LONG).show();
                                            finish();
                                        }else {
                                            Toast.makeText(editActivity.this,response.getString("response"),Toast.LENGTH_LONG).show();
                                        }
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onError(ANError error) {
                                    // handle error
                                    Log.d("ErrorEditData",""+error.getErrorDetail());
                                }
                            });
                }
            }
        });
    }
}
