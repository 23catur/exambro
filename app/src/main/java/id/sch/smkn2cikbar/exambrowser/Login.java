package id.sch.smkn2cikbar.exambrowser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {


    EditText ETEmail,ETPass;
    String nama,password, id_user;
    ProgressDialog progressDialog;
    public final static String TAG_NAMA = "nama";
    public final static String TAG_ID = "id_user";
    String tag_json_obj = "json_obj_req";
    public final static String TAG_success = "success";
    TextView regis;
    Button Button3;
    private static final int REQUEST_SIGNUP = 0;
    public static final String session_status = "session_status";

    public static final String my_shared_preferences = "my_shared_preferences";
    SharedPreferences sharedpreferences;
    Boolean session = false;
    private static String SHARED_NAME = "SPLASH";
    private static String FIRST_TIME = "FIRST_TIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ETEmail     = findViewById(R.id.input_nim);
        ETPass      = findViewById(R.id.input_password);
        Button3 = findViewById(R.id.btn_login);
        progressDialog = new ProgressDialog(this);
//        regis =findViewById(R.id.link_signup);
        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        id_user = sharedpreferences.getString(TAG_ID, null);
        nama = sharedpreferences.getString(TAG_NAMA, null);

        if (session) {
            Intent intent = new Intent(Login.this, SplashScreen.class);
            intent.putExtra(TAG_ID, id_user);
            intent.putExtra(TAG_NAMA, nama);
            finish();
            startActivity(intent);
        }
        Button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                progressDialog.setMessage("Loading ...");
                progressDialog.setCancelable(false);
                progressDialog.show( );

                nama = ETEmail.getText().toString();
                password = ETPass.getText().toString();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() { validasiData(); }

                },1000);
            }
        });


//        regis.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // Start the Signup activity
//              //  Intent intent = new Intent(getApplicationContext(), Regis.class);
//              //  startActivityForResult(intent, REQUEST_SIGNUP);
//                finish();
//               // overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//            }
//        });
    }
    void validasiData(){
        if(nama.equals("") || password.equals("")){
            progressDialog.dismiss();
            Toast.makeText(Login.this,"Periksa kembali data Anda !", Toast.LENGTH_SHORT).show();
        }else  {
            kirimData();
        }
    }
    void kirimData(){
        AndroidNetworking.post("https://nasionalmakassar.sch.id/exam/login.php")
                .addBodyParameter("nama",""+nama)
                .addBodyParameter("password",""+password)
                .setPriority(Priority.MEDIUM)
                .setTag("Tambah Data")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Log.d("cekTambah",""+response);
                        try {
                            //        Boolean status = response.getBoolean("status");
                            //        String pesan   = response.getString("result");
                            //        Toast.makeText(Login.this, ""+pesan, Toast.LENGTH_SHORT).show();

                            //        Log.d("status",""+status);
                            Integer success;
                         //   success = jObj.getInt(TAG_success);

                            success = response.getInt(TAG_success);
                            if (success == 1) {

//                                String nama = response.getString(TAG_NAMA);

                                new AlertDialog.Builder(Login.this)
                                        .setMessage("Selamat Datang di LMS")
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent(Login.this, MainActivity.class);
                                                startActivity(i);
                                            }
                                        })
                                        .show();

                                if (response.getString("nama").equals("")) {
                                    // menyimpan login ke session
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putBoolean(session_status, true);
                                    editor.putString(TAG_ID, id_user);
                                    editor.putString(TAG_NAMA, nama);

                                    editor.commit();
                                    Intent intent = new Intent(Login.this, Login.class);

                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                new AlertDialog.Builder(Login.this)
                                        .setMessage("Periksa Kembali Nama dan Password !")
                                        .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent(Login.this, Login.class);
                                                startActivity(i);
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(com.androidnetworking.error.ANError anError) {
                        Log.d("ErrorTambahData",""+anError.getErrorBody());
                    }

                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }
    public static boolean checkFirstTime(Context ctx) {
        SharedPreferences settings = ctx.getSharedPreferences(SHARED_NAME, 0);
        Boolean first = settings.getBoolean(FIRST_TIME, false);
        if (!first) {
            settings = ctx.getSharedPreferences(SHARED_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(FIRST_TIME, true);
            editor.commit();
        }
        return first;

    }
}