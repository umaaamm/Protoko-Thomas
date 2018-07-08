package protoko.com.protoko;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileSalesActivity extends AppCompatActivity {

    private static final String Database_Path = "protoko_db/sales";
    private DatabaseReference mDatabaseReference;
    private Query query;
    private String userName;

    private ScrollView svToko;
    private ProgressDialog progressDialog;
    private EditText etNamaSales;
    private EditText etNamaPerusahaan;
    private EditText etIdSales;
    private EditText etTelepon;
    private Button btUbah;
    private Button btSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_sales);

        svToko = (ScrollView)findViewById(R.id.svToko);

        progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
        etNamaSales = (EditText)findViewById(R.id.etNamaSales);
        etNamaPerusahaan = (EditText)findViewById(R.id.etNamaPerusahaan);
        etIdSales = (EditText)findViewById(R.id.etIdSales);
        etTelepon = (EditText)findViewById(R.id.etTelp);
        btUbah = (Button)findViewById(R.id.Ubah);
        btSimpan = (Button)findViewById(R.id.Simpan);
        
        etNamaSales.setInputType(0);
        etNamaPerusahaan.setInputType(0);
        etTelepon.setInputType(0);
        etIdSales.setTextColor(ContextCompat.getColor(this, R.color.colorTextDis));
        etNamaSales.setTextColor(ContextCompat.getColor(this, R.color.colorTextDis));
        etNamaPerusahaan.setTextColor(ContextCompat.getColor(this, R.color.colorTextDis));
        etTelepon.setTextColor(ContextCompat.getColor(this, R.color.colorTextDis));

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            try {
                userName = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }else{
            userName = "";
        }

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        query = mDatabaseReference.child(userName+"/info");
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                etIdSales.setText(snapshot.child("idSales").getValue(String.class));

                etNamaSales.setText(snapshot.child("namaSales").getValue(String.class));

                etNamaPerusahaan.setText(snapshot.child("namaPerusahaan").getValue(String.class));

                etTelepon.setText(snapshot.child("telpSales").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    public void ubah(View view) {
        etTelepon = (EditText)findViewById(R.id.etTelp);
        btUbah = (Button)findViewById(R.id.Ubah);
        btSimpan = (Button)findViewById(R.id.Simpan);

        etTelepon.setInputType(InputType.TYPE_CLASS_NUMBER);
        etTelepon.setTextColor(ContextCompat.getColor(this, R.color.colorHitam));
        btSimpan.setVisibility(View.VISIBLE);
        btUbah.setVisibility(View.GONE);
    }

    public void simpan(View view) {
        String tlp = etTelepon.getText().toString();
        ubahTelepon(tlp);
    }

    private void ubahTelepon(String telp){
        if(!telp.isEmpty()) {
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Proses ...");
            progressDialog.show();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
            mDatabaseReference.child(userName + "/info").child("telpSales").setValue(telp);
            progressDialog.dismiss();
            Snackbar.make(svToko,"Perubahan profil sales berhasil.", Snackbar.LENGTH_SHORT).show();
            btSimpan.setVisibility(View.GONE);
            btUbah.setVisibility(View.VISIBLE);
            etTelepon.setInputType(0);
            etTelepon.setTextColor(ContextCompat.getColor(this, R.color.colorTextDis));
        }else{
            Snackbar.make(svToko,"Periksa kembali data Anda.", Snackbar.LENGTH_SHORT).show();
        }
    }
}