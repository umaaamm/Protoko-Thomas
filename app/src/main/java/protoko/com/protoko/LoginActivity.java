package protoko.com.protoko;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edit_text_username_activity_main) EditText editTextUsername;
    @BindView(R.id.edit_text_password_activity_main) EditText editTextPassword;
    private CheckBox mCbShowPwd;

    private FirebaseAuth firebaseAuth;
    private boolean loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initFirebase();
        ButterKnife.bind(this);
        loggedIn = isLoggedIn();
        if (loggedIn) {
            goToMain();
        }

        editTextPassword = (EditText) findViewById(R.id.edit_text_password_activity_main);
        mCbShowPwd = (CheckBox) findViewById(R.id.cbShowPwd);
        mCbShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick({R.id.button_login_activity_main, R.id.button_sign_up_activity_main, R.id.button_sign_up_sales})
    public void onClick(Button button) {
        switch (button.getId()) {
            case R.id.button_login_activity_main:
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                login(username, password);
                break;
            case R.id.button_sign_up_activity_main:
                Intent intent = new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(intent);
                break;
            case R.id.button_sign_up_sales:
                Intent signupSales = new Intent(getApplicationContext(),SignupSalesActivity.class);
                startActivity(signupSales);
                break;
        }
    }

    private void login(final String username, final String password) {
        if (TextUtils.isEmpty(username)) {
            Snackbar.make(findViewById(android.R.id.content), "Alamat email tidak boleh kosong", Snackbar.LENGTH_LONG)
                    .show();
        } else if (TextUtils.isEmpty(password)) {
            Snackbar.make(findViewById(android.R.id.content), "Kata sandi tidak boleh kosong", Snackbar.LENGTH_LONG)
                    .show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Proses ...");
            progressDialog.show();
            try {
                firebaseAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {
                                        if (user.isEmailVerified()) {
                                            goToMain();
                                        } else {
                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this, R.style.AppCompatAlertDialogStyle);
                                            alertDialogBuilder
                                                    .setTitle("Verifikasi Email Anda")
                                                    .setMessage("Kami telah mengirimkan email ke " + user.getEmail() + " untuk  memverifikasi alamat email Anda. Silahkan klik link di email tersebut untuk dapat masuk ke akun Anda.")
                                                    .setCancelable(false)
                                                    .setPositiveButton("Keluar", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            progressDialog.dismiss();
                                                            FirebaseAuth.getInstance().signOut();
                                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .setNegativeButton("Kirim Ulang", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            kirimVerikasiEmail();
                                                            FirebaseAuth.getInstance().signOut();
                                                            dialog.dismiss();
                                                            progressDialog.dismiss();
                                                        }
                                                    });
                                            final AlertDialog alertDialog = alertDialogBuilder.create();
                                            alertDialog.show();

                                            TextView pesan = (TextView) alertDialog.findViewById(android.R.id.message);
                                            pesan.setTextSize(15);

                                            Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                            b.setTextColor(getResources().getColor(R.color.colorPrimary));

                                            Button c = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                                            c.setTextColor(getResources().getColor(R.color.colorHitam));
                                        }
                                    }

                                } else {

                                    showMessageBox("Proses gagal. Alamat email atau kata sandi salah");
                                    progressDialog.hide();
                                }
                            }
                        });
            }catch (NullPointerException e){
                e.printStackTrace();
            }

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
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    private void showMessageBox(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Masuk");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        TextView pesan = (TextView) alertDialog.findViewById(android.R.id.message);
        pesan.setTextSize(15);
    }

    private boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }
}