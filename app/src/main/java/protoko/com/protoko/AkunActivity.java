package protoko.com.protoko;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AkunActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etSandi;
    private EditText etSandiUlang;
    private EditText etSandiBaru;
    private CheckBox mCbShowPwd;

    private Button btUbah;
    private Button btSimpan;
    private Button btUbahSandi;
    private Button btSimpanSandi;

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private ProgressDialog progressDialog;

    private LinearLayout llSandi;
    private LinearLayout llSandiBaru;

    private String email;
    private String password;

    private ScrollView svAkun;

    private final Pattern letter = Pattern.compile("[a-zA-z]");
    private final Pattern digit = Pattern.compile("[0-9]");
    private boolean ok(String password) {
        Matcher hasLetter = letter.matcher(password);
        Matcher hasDigit = digit.matcher(password);
        return hasLetter.find() && hasDigit.find();
    }

    private static final String Database_Path = "protoko_db/toko";
    private DatabaseReference mDatabaseReference;
    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun);

        svAkun = (ScrollView)findViewById(R.id.svAkun);
        progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
        llSandi = (LinearLayout)findViewById(R.id.llSandi);
        llSandiBaru = (LinearLayout)findViewById(R.id.llSandiBaru);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etSandi = (EditText)findViewById(R.id.etSandi);
        etSandiUlang = (EditText)findViewById(R.id.etSandiUlang);
        etSandiBaru = (EditText)findViewById(R.id.etSandiBaru);
        btUbah = (Button)findViewById(R.id.Ubah);
        btUbahSandi = (Button)findViewById(R.id.btUbahSandi);
        btSimpan = (Button)findViewById(R.id.Simpan);
        btSimpanSandi = (Button)findViewById(R.id.SimpanSandi);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            try {
                email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }else{
            email = "";
        }

        etEmail.setText(email);
        etEmail.setInputType(0);
        etSandi.setInputType(0);
        etEmail.setTextColor(ContextCompat.getColor(this, R.color.colorTextDis));

        mCbShowPwd = (CheckBox) findViewById(R.id.cbShowPwd);

        mCbShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    etSandi.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    etSandi.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            try {
                userName = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }else{
            userName = "";
        }


    }


    public void ubah(View view) {
        etEmail = (EditText)findViewById(R.id.etEmail);
        etSandi = (EditText)findViewById(R.id.etSandi);
        etSandiUlang = (EditText)findViewById(R.id.etSandiUlang);
        btUbah = (Button)findViewById(R.id.Ubah);
        btSimpan = (Button)findViewById(R.id.Simpan);
        btUbahSandi = (Button)findViewById(R.id.btUbahSandi);
        mCbShowPwd = (CheckBox) findViewById(R.id.cbShowPwd);

        etSandi.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etSandi.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etEmail.setTextColor(ContextCompat.getColor(this, R.color.colorHitam));
        btSimpan.setVisibility(View.VISIBLE);
        btUbah.setVisibility(View.GONE);
        btUbahSandi.setVisibility(View.GONE);
        etEmail.requestFocus();
        mCbShowPwd.setVisibility(View.VISIBLE);
    }

    public void simpan(View view) {
        if(!etSandi.getText().toString().isEmpty()){
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Proses ...");
            progressDialog.show();
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                try {
                    email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                }catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            password = etSandi.getText().toString();
            firebaseAuth.signOut();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                gantiEmail(etEmail.getText().toString());
                            } else {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AkunActivity.this, R.style.AppCompatAlertDialogStyle);
                                alertDialogBuilder
                                        .setTitle("Perubahan Email")
                                        .setMessage("Perubahan email tidak berhasil, kata sandi yang Anda masukan salah. Untuk menjaga keamanan akun ini, maka silahkan masuk kembali")
                                        .setCancelable(false)
                                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                FirebaseAuth.getInstance().signOut();
                                                Intent intent = new Intent(AkunActivity.this, LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        });
                                final AlertDialog alertDialog = alertDialogBuilder.create();
                                progressDialog.dismiss();
                                alertDialog.show();
                                TextView pesan = (TextView) alertDialog.findViewById(android.R.id.message);
                                pesan.setTextSize(15);
                            }
                        }
                    });
        }else{
            Snackbar.make(svAkun,"Kata sandi belum diisi.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void gantiEmail(final String emailBaru){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        try {
            user.updateEmail(emailBaru)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                kirimVerikasiEmail();

                                mDatabaseReference.child(userName + "/info").child("email").setValue(emailBaru);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AkunActivity.this, R.style.AppCompatAlertDialogStyle);
                                alertDialogBuilder
                                        .setTitle("Perubahan Email")
                                        .setMessage("Perubahan email telah berhasil, kami telah mengirimkan email ke " + user.getEmail() + " untuk  memverifikasi alamat email Anda.")
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                FirebaseAuth.getInstance().signOut();
                                                Intent intent = new Intent(AkunActivity.this, LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        });
                                final AlertDialog alertDialog = alertDialogBuilder.create();
                                progressDialog.dismiss();
                                alertDialog.show();
                                TextView pesan = (TextView) alertDialog.findViewById(android.R.id.message);
                                pesan.setTextSize(15);
                            }
                        }
                    });
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void kirimVerikasiEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            try {
                user.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                }
                            }
                        });
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void klikUbahSandi(View view) {
        etEmail = (EditText)findViewById(R.id.etEmail);
        etSandi = (EditText)findViewById(R.id.etSandi);
        etSandiUlang = (EditText)findViewById(R.id.etSandiUlang);
        btUbah = (Button)findViewById(R.id.Ubah);
        btSimpan = (Button)findViewById(R.id.Simpan);
        btSimpanSandi = (Button)findViewById(R.id.SimpanSandi);
        btUbahSandi = (Button)findViewById(R.id.btUbahSandi);
        mCbShowPwd = (CheckBox) findViewById(R.id.cbShowPwd);
        llSandi = (LinearLayout)findViewById(R.id.llSandi);
        llSandiBaru = (LinearLayout)findViewById(R.id.llSandiBaru);

        etSandi.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etSandi.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etEmail.setInputType(0);
        btSimpan.setVisibility(View.GONE);
        btUbah.setVisibility(View.GONE);
        btUbahSandi.setVisibility(View.GONE);
        btSimpanSandi.setVisibility(View.VISIBLE);
        llSandi.setVisibility(View.VISIBLE);
        llSandiBaru.setVisibility(View.VISIBLE);
        etSandi.requestFocus();
        mCbShowPwd.setVisibility(View.GONE);
    }

    public void simpanSandi(View view) {
        String passBaru = etSandiBaru.getText().toString();
        String passBaruUlang = etSandiUlang.getText().toString();
        if(!etSandi.getText().toString().isEmpty()){
            if((ok(passBaru))&&(passBaru.length()>8)) {
                if (passBaru.equals(passBaruUlang)) {
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Proses ...");
                    progressDialog.show();

                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        try {
                            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        }catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    password = etSandi.getText().toString();
                    try {
                        firebaseAuth.signOut();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        gantiSandi(etSandiBaru.getText().toString());
                                    } else {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AkunActivity.this, R.style.AppCompatAlertDialogStyle);
                                        alertDialogBuilder
                                                .setTitle("Perubahan Kata Sandi")
                                                .setMessage("Perubahan kata sandi tidak berhasil, kata sandi lama yang Anda masukan salah. Untuk menjaga keamanan akun ini, maka silahkan masuk kembali")
                                                .setCancelable(false)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        FirebaseAuth.getInstance().signOut();
                                                        Intent intent = new Intent(AkunActivity.this, LoginActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    }
                                                });
                                        final AlertDialog alertDialog = alertDialogBuilder.create();
                                        progressDialog.dismiss();
                                        alertDialog.show();
                                        TextView pesan = (TextView) alertDialog.findViewById(android.R.id.message);
                                        pesan.setTextSize(15);
                                    }
                                }
                            });
                } else {
                    Snackbar.make(svAkun,"Kata sandi tidak sama.", Snackbar.LENGTH_SHORT).show();
                }
            }else{
                Snackbar.make(svAkun,"Kata sandi harus lebih dari 8 karakter dan alfanumerik.", Snackbar.LENGTH_SHORT).show();
            }
        }else {
            Snackbar.make(svAkun,"Kata sandi belum diisi.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void gantiSandi(String newPassword){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        try {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AkunActivity.this, R.style.AppCompatAlertDialogStyle);
                                alertDialogBuilder
                                        .setTitle("Perubahan Kata Sandi")
                                        .setMessage("Perubahan kata sandi telah berhasil. Silahkan masuk kembali dengan kata sandi yang baru.")
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                FirebaseAuth.getInstance().signOut();
                                                Intent intent = new Intent(AkunActivity.this, LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        });
                                final AlertDialog alertDialog = alertDialogBuilder.create();
                                progressDialog.dismiss();
                                alertDialog.show();
                                TextView pesan = (TextView) alertDialog.findViewById(android.R.id.message);
                                pesan.setTextSize(15);
                            }
                        }
                    });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}