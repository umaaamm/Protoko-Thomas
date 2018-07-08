package protoko.com.protoko;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.os.Bundle;

import protoko.com.protoko.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang3.text.WordUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.input_namaToko) EditText _namaToko;
    @BindView(R.id.input_alamatToko) EditText _alamatToko;
    @BindView(R.id.input_telpToko) EditText _telpToko;
    @BindView(R.id.actKec) AutoCompleteTextView _actKec;
    @BindView(R.id.input_namaPemilik) EditText _namaPemilik;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.btPeriksaEmail) Button _btPeriksaEmail;
    @BindView(R.id.tvhasilEmail) TextView _tvHasilEmail;
    @BindView(R.id.btNext) Button _btNext;
    @BindView(R.id.btBack) Button _btBack;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private String Storage_Path = "toko/";
    private String imgURL;
    private String imgURL2;
    private ImageButton btRemove;
    private ImageButton btRemove2;
    private Uri FilePathUri;
    private ImageView SelectImage;
    private ImageView SelectImage2;
    private StorageReference storageReference;
    private boolean foto = false;
    private boolean foto2 = false;

    private int Image_Request_Code = 7;
    private int Image_Request_Code2 = 8;

    private byte[] compImg;
    private ByteArrayOutputStream baos;

    private List<String> emailList = new ArrayList<>();

    private String[] kec = {"Abung Barat, Lampung Utara","Abung Kunang, Lampung Utara","Abung Pekurun, Lampung Utara","Abung Selatan, Lampung Utara","Abung Semuli, Lampung Utara","Abung Surakarta, Lampung Utara","Abung Tengah, Lampung Utara","Abung Timur, Lampung Utara","Abung Tinggi, Lampung Utara","Adiluwih, Pringsewu","Air Hitam, Lampung Barat","Air Naningan, Tanggamus","Ambarawa, Pringsewu","Anak Ratu Aji, Lampung Tengah","Anak Tuha, Lampung Tengah","Bahuga, Way Kanan","Bakauheni, Lampung Selatan","Balik Bukit, Lampung Barat","Bandar Mataram, Lampung Tengah","Bandar Negeri Semuong, Tanggamus","Bandar Negeri Suoh, Lampung Barat","Bandar Sribhawono, Lampung Timur","Bandar Surabaya, Lampung Tengah","Bangunrejo, Lampung Tengah","Banjar Agung, Tulang Bawang","Banjar Baru, Tulang Bawang","Banjar Margo, Tulang Bawang","Banjit, Way Kanan","Banyumas, Pringsewu","Baradatu, Way Kanan","Batanghari Nuban, Lampung Timur","Batanghari, Lampung Timur","Batu Brak, Lampung Barat","Batu Ketulis, Lampung Barat","Bekri, Lampung Tengah","Belalau, Lampung Barat","Bengkunat Belimbing, Pesisir Barat","Bengkunat, Pesisir Barat","Blambangan Pagar, Lampung Utara","Blambangan Umpu, Way Kanan","Braja Slebah, Lampung Timur","Buay Bahuga, Way Kanan","Bukit Kemuning, Lampung Utara","Bulok, Tanggamus","Bumi Agung, Lampung Timur","Bumi Agung, Way Kanan","Bumi Nabung, Lampung Tengah","Bumi Ratu Nuban, Lampung Tengah","Bumi Waras, Bandar Lampung","Bunga Mayang, Lampung Utara","Candipuro, Lampung Selatan","Cukuh Balak, Tanggamus","Dente Teladas, Tulang Bawang","Enggal, Bandar Lampung","Gading Rejo, Pringsewu","Gadingrejo, Pringsewu","Gedong Tataan, Pesawaran","Gedung Aji Baru, Tulang Bawang","Gedung Aji, Tulang Bawang","Gedung Meneng, Tulang Bawang","Gedung Surian, Lampung Barat","Gisting, Tanggamus","Gunung Agung, Tulang Bawang Barat","Gunung Labuhan, Way Kanan","Gunung Pelindung, Lampung Timur","Gunung Sugih, Lampung Tengah","Gunung Terang, Tulang Bawang Barat","Hulu Sungkai, Lampung Utara","Jabung, Lampung Timur","Jati Agung, Lampung Selatan","Kalianda, Lampung Selatan","Kalirejo, Lampung Tengah","Karya Penggawa, Pesisir Barat","Kasui, Way Kanan","Katibung, Lampung Selatan","Kebun Tebu, Lampung Barat","Kedamaian, Bandar Lampung","Kedaton, Bandar Lampung","Kedondong, Pesawaran","Kelumbayan Barat, Tanggamus","Kelumbayan, Tanggamus","Kemiling, Bandar Lampung","Ketapang, Lampung Selatan","Ketimbang","Kota Agung Barat, Tanggamus","Kota Agung Pusat, Tanggamus","Kota Agung Timur, Tanggamus","Kota Gajah, Lampung Tengah","Kotaagung, Tanggamus","Kotabumi Kota, Lampung Utara","Kotabumi Selatan, Lampung Utara","Kotabumi Utara, Lampung Utara","Krui Selatan, Pesisir Barat","Labuhan Maringgai, Lampung Timur","Labuhan Ratu, Bandar Lampung","Labuhan Ratu, Lampung Timur","Lambu Kibang, Tulang Bawang Barat","Langkapura, Bandar Lampung","Lemong, Pesisir Barat","Limau, Tanggamus","Lumbok Seminung, Lampung Barat","Marga Punduh, Pesawaran","Marga Sekampung, Lampung Timur","Margatiga, Lampung Timur","Mataram Baru, Lampung Timur","Melinting, Lampung Timur","Menggala Timur, Tulang Bawang","Menggala, Tulang Bawang","Meraksa Aji, Tulang Bawang","Merbau Mataram, Lampung Selatan","Mesuji Timur, Mesuji","Mesuji, Mesuji","Metro Barat, Metro","Metro Kibang, Lampung Timur","Metro Pusat, Metro","Metro Selatan, Metro","Metro Timur, Metro","Metro Utara, Metro","Muara Sungkai, Lampung Utara","Natar, Lampung Selatan","Negara Batin, Way Kanan","Negeri Agung, Way Kanan","Negeri Besar, Way Kanan","Negeri Katon, Pesawaran","Ngambur, Pesisir Barat","Padang Cermin, Pesawaran","Padang Ratu, Lampung Tengah","Pagar Dewa, Lampung Barat","Pagar Dewa, Tulang Bawang Barat","Pagelaran Utara, Pringsewu","Pagelaran, Pringsewu","Pakuan Ratu, Way Kanan","Palas, Lampung Selatan","Panca Jaya, Mesuji","Panjang, Bandar Lampung","Pardasuka, Pringsewu","Pasir Sakti, Lampung Timur","Pekalongan, Lampung Timur","Pematang Sawa, Tanggamus","Penawar Aji, Tulang Bawang","Penawar Tama, Tulang Bawang","Penengahan, Lampung Selatan","Pesisir Selatan, Pesisir Barat","Pesisir Tengah, Pesisir Barat","Pesisir Utara, Pesisir Barat","Pringsewu, Pringsewu","Pubian, Lampung Tengah","Pugung, Tanggamus","Pulau Panggung, Tanggamus","Pulau Pisang, Pesisir Barat","Punduh Pidada, Pesawaran","Punggur, Lampung Tengah","Purbolinggo, Lampung Timur","Putra Rumbia, Lampung Tengah","Rajabasa, Bandar Lampung","Rajabasa, Lampung Selatan","Raman Utara, Lampung Timur","Rawa Jitu Utara, Mesuji","Rawa Pitu, Tulang Bawang","Rawajitu Selatan, Tulang Bawang","Rawajitu Timur, Tulang Bawang","Rebang Tangkas, Way Kanan","Rumbia, Lampung Tengah","Sekampung Udik, Lampung Timur","Sekampung, Lampung Timur","Sekincau, Lampung Barat","Selagai Lingga, Lampung Tengah","Semaka, Tanggamus","Sendang Agung, Lampung Tengah","Seputih Agung, Lampung Tengah","Seputih Banyak, Lampung Tengah","Seputih Mataram, Lampung Tengah","Seputih Raman, Lampung Tengah","Seputih Surabaya, Lampung Tengah","Sidomulyo, Lampung Selatan","Simpang Pematang, Mesuji","Sragi, Lampung Selatan","Sukabumi, Bandar Lampung","Sukadana, Lampung Timur","Sukarame, Bandar Lampung","Sukau, Lampung Barat","Sukoharjo, Pringsewu","Sumber Jaya, Lampung Barat","Sumberejo, Tanggamus","Sungkai Barat, Lampung Utara","Sungkai Jaya, Lampung Utara","Sungkai Selatan, Lampung Utara","Sungkai Tengah, Lampung Utara","Sungkai Utara, Lampung Utara","Suoh, Lampung Barat","Talang Padang, Tanggamus","Tanjung Bintang, Lampung Selatan","Tanjung Karang Barat, Bandar Lampung","Tanjung Karang Pusat, Bandar Lampung","Tanjung Karang Timur, Bandar Lampung","Tanjung Raja, Lampung Utara","Tanjung Raya, Mesuji","Tanjung Senang, Bandar Lampung","Tanjungsari, Lampung Selatan","Tegineneng, Pesawaran","Teluk Betung Barat, Bandar Lampung","Teluk Betung Selatan, Bandar Lampung","Teluk Betung Timur, Bandar Lampung","Teluk Betung Utara, Bandar Lampung","Way Halim, Bandar Lampung"};

    private static final String Database_Path = "protoko_db/toko";
    private DatabaseReference databaseReference;

    private String email;
    private String password;

    private ScrollView svDaftar;

    private boolean emailReady = false;
    private boolean emailValid = true;
    private boolean emailKlik = false;
    private ProgressDialog progressDialog;

    private String lokasi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        svDaftar = (ScrollView)findViewById(R.id.svDaftar);
        Intent i = getIntent();
        Bundle bd = i.getExtras();
        if(i.getStringExtra("lokasi") != null){
            lokasi = (String) bd.get("lokasi");
            bd.remove("lokasi");
        }

        if(lokasi != null){
            final LinearLayout layBerikutnya = (LinearLayout)findViewById(R.id.layBerikutnya);
            final LinearLayout layDaftar = (LinearLayout)findViewById(R.id.layDaftar);
            final LinearLayout llLokasi = (LinearLayout)findViewById(R.id.llLokasi);
            layBerikutnya.setVisibility(View.VISIBLE);
            layDaftar.setVisibility(View.GONE);
            llLokasi.setVisibility(View.GONE);
            Snackbar.make(svDaftar,"Lokasi toko berhasil ditandai.", Snackbar.LENGTH_SHORT).show();
        }else{
            final LinearLayout layBerikutnya = (LinearLayout)findViewById(R.id.layBerikutnya);
            final LinearLayout layDaftar = (LinearLayout)findViewById(R.id.layDaftar);
            layBerikutnya.setVisibility(View.GONE);
            layDaftar.setVisibility(View.GONE);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        cekEmail();

        storageReference = FirebaseStorage.getInstance().getReference();

        SelectImage = (ImageView)findViewById(R.id.gambarPromo);
        SelectImage2 = (ImageView)findViewById(R.id.gambarPromo2);

        progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);

        ButterKnife.bind(this);

        ArrayAdapter<String> adapterKec = new ArrayAdapter<String>(this,R.layout.dropdown, kec);
        _actKec.setThreshold(1);
        _actKec.setAdapter(adapterKec);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _btPeriksaEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailKlik = true;
                emailValid = true;
                for(String emailStr: emailList) {
                    if(emailStr.trim().contains(_emailText.getText())){
                        emailValid = false;
                    }

                }
                if(emailReady) {
                    if (emailValid) {
                        _tvHasilEmail.setText("Alamat email masih tersedia.");
                    } else {
                        _tvHasilEmail.setText("Alamat email sudah digunakan.");
                    }
                }

            }
        });

        _emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailKlik = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }
        _signupButton.setEnabled(false);
        progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Proses ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uploadFoto();
                        }
                    }
                });
    }

    private void createFirebaseUserProfile(final FirebaseUser user) {
        UserProfileChangeRequest addProfileName = new UserProfileChangeRequest.Builder()
                .setDisplayName(WordUtils.capitalizeFully(_namaToko.getText().toString()))
                .setPhotoUri(Uri.parse(imgURL2))
                .build();
        user.updateProfile(addProfileName);
    }

    private void uploadFoto(){
        if(foto2) {
            Bitmap bitmapTwo = ((BitmapDrawable)SelectImage2.getDrawable()).getBitmap();
            ByteArrayOutputStream imageTwoBytes = new ByteArrayOutputStream();
            bitmapTwo.compress(Bitmap.CompressFormat.JPEG, 100, imageTwoBytes);
            byte[] dataTwo = imageTwoBytes.toByteArray();

            StorageReference filepathTwo = storageReference.child(Storage_Path + System.currentTimeMillis() + ".jpg");
            filepathTwo.putBytes(dataTwo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    try {
                        imgURL2 = downloadUrl.toString();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }

                    if(foto) {
                        Bitmap bitmapOne = ((BitmapDrawable)SelectImage.getDrawable()).getBitmap();
                        ByteArrayOutputStream imageOneBytes = new ByteArrayOutputStream();
                        bitmapOne.compress(Bitmap.CompressFormat.JPEG, 100, imageOneBytes);
                        byte[] dataOne = imageOneBytes.toByteArray();

                        StorageReference filepathOne = storageReference.child(Storage_Path + System.currentTimeMillis() + ".jpg");
                        filepathOne.putBytes(dataOne).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                @SuppressWarnings("VisibleForTests")
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                imgURL = downloadUrl.toString();
                                createFirebaseUserProfile(firebaseAuth.getCurrentUser());
                                daftarToko(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                kirimVerikasiEmail();
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignupActivity.this, R.style.AppCompatAlertDialogStyle);
                                try {
                                    alertDialogBuilder
                                            .setTitle("Verifikasi Email Anda")
                                            .setMessage("Kami telah mengirimkan email ke " + FirebaseAuth.getInstance().getCurrentUser().getEmail() + " untuk  memverifikasi alamat email Anda. Silahkan klik link di email tersebut untuk dapat masuk ke akun Anda.")
                                            .setCancelable(false)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    onSignupSuccess();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }
                                final AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

                                TextView pesan = (TextView) alertDialog.findViewById(android.R.id.message);
                                pesan.setTextSize(15);

                                Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                b.setTextColor(getResources().getColor(R.color.colorPrimary));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

    private void kirimVerikasiEmail(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                            }
                        }
                    });
        }
    }

    private void onSignupSuccess() {
        FirebaseAuth.getInstance().signOut();
        Snackbar.make(svDaftar,"Daftar berhasil.", Snackbar.LENGTH_SHORT).show();
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    private void onSignupFailed() {
        _signupButton.setEnabled(true);
    }

    private Pattern letter = Pattern.compile("[a-zA-z]");
    private Pattern digit = Pattern.compile("[0-9]");
    private boolean ok(String password) {
        Matcher hasLetter = letter.matcher(password);
        Matcher hasDigit = digit.matcher(password);
        return hasLetter.find() && hasDigit.find();
    }


    private void daftarToko(String id){
        String namaToko = WordUtils.capitalizeFully(_namaToko.getText().toString());
        String alamatToko = WordUtils.capitalizeFully(_alamatToko.getText().toString());
        String namaPemilik = _namaPemilik.getText().toString();
        String email = _emailText.getText().toString();
        String kec = _actKec.getText().toString();
        String telpToko = _telpToko.getText().toString();
        TokoUpload tu = new TokoUpload(alamatToko, email, namaPemilik, namaToko, telpToko, kec, imgURL, imgURL2, lokasi, "0",0);
        databaseReference.child(id+"/info").setValue(tu);
    }

    private boolean validate() {
        boolean valid = true;
        String kec = _actKec.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        String namaToko = WordUtils.capitalizeFully(_namaToko.getText().toString());
        String alamatToko = WordUtils.capitalizeFully(_alamatToko.getText().toString());
        String namaPemilik = _namaPemilik.getText().toString();
        String telpToko = _telpToko.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Alamat email tidak valid");
            Snackbar.make(svDaftar,"Alamat email tidak valid.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if ((ok(password))&&(password.length()>8)){
            _passwordText.setError(null);
        } else {
            _passwordText.setError("Kata sandi harus lebih dari 8 karakter dan alfanumerik");
            Snackbar.make(svDaftar,"Kata sandi harus lebih dari 8 karakter dan alfanumerik.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        }
        if (reEnterPassword.isEmpty() || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Kata sandi tidak cocok");
            Snackbar.make(svDaftar,"Kata sandi tidak cocok.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        if (kec.isEmpty()) {
            _actKec.setError("Kecamatan belum diisi");
            Snackbar.make(svDaftar,"Kecamatan belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            _actKec.setError(null);
        }
        if (namaToko.isEmpty()) {
            _namaToko.setError("Nama Toko belum diisi");
            Snackbar.make(svDaftar,"Nama Toko belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            _namaToko.setError(null);
        }
        if (alamatToko.isEmpty()) {
            _alamatToko.setError("Alamat Toko belum diisi");
            Snackbar.make(svDaftar,"Alamat Toko belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            _alamatToko.setError(null);
        }
        if (namaPemilik.isEmpty()) {
            _namaPemilik.setError("Nama pemilik belum diisi");
            Snackbar.make(svDaftar,"Nama pemilik belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            _namaPemilik.setError(null);
        }
        if (telpToko.isEmpty()) {
            _telpToko.setError("Nomor telepon belum diisi");
            Snackbar.make(svDaftar,"Nomor telepon belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            _telpToko.setError(null);
        }
        if(!foto || !foto2){
            valid = false;
            Snackbar.make(svDaftar,"Foto Toko atau KTP belum dipilih.", Snackbar.LENGTH_SHORT).show();
        }
        if(!emailValid || !emailReady){
            valid = false;
            Snackbar.make(svDaftar,"Email sudah terdaftar, gunakan email yang lain.", Snackbar.LENGTH_SHORT).show();
        }
        if(!emailKlik){
            valid = false;
            Snackbar.make(svDaftar,"Klik tombol periksa email untuk memeriksa email valid.", Snackbar.LENGTH_SHORT).show();
        }
        return valid;
    }


    public void berikutnyaDaftar(View view) {
        final LinearLayout layBerikutnya = (LinearLayout)findViewById(R.id.layBerikutnya);
        final LinearLayout layDaftar = (LinearLayout)findViewById(R.id.layDaftar);
        layBerikutnya.setVisibility(View.GONE);
        layDaftar.setVisibility(View.VISIBLE);
    }

    public void sebelumnyaDaftar(View view) {
        final LinearLayout layBerikutnya = (LinearLayout)findViewById(R.id.layBerikutnya);
        final LinearLayout layDaftar = (LinearLayout)findViewById(R.id.layDaftar);
        layBerikutnya.setVisibility(View.VISIBLE);
        layDaftar.setVisibility(View.GONE);
    }

    private void cekEmail(){
        Query searchQuery = databaseReference.orderByChild("info/");
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                TokoUpload infoToko = new TokoUpload();
                while((iterator.hasNext())){
                    infoToko = iterator.next().getValue(TokoUpload.class);
                }
                emailList.add(infoToko.getEmail());
                emailReady = true;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void uploadGambar(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Upload Foto")
                .setCancelable(true)
                .setPositiveButton("Kamera",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        ambilGambar();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void uploadGambar2(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Upload Foto")
                .setCancelable(true)
                .setPositiveButton("Kamera",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        ambilGambar2();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void ambilGambar() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1);
    }

    private void ambilGambar2() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 2);
    }

    private void removeImg1() {
        SelectImage.setImageResource(R.drawable.ic_add_img);
        btRemove = (ImageButton)findViewById(R.id.btRemove1);
        btRemove.setVisibility(View.GONE);
        foto = false;
    }

    private void removeImg2() {
        SelectImage2.setImageResource(R.drawable.ic_add_img2);
        btRemove2 = (ImageButton)findViewById(R.id.btRemove2);
        btRemove2.setVisibility(View.GONE);
        foto2 = false;
    }

    public void removeImg1v(View view) {
        removeImg1();
    }

    public void removeImg2v(View view) {
        removeImg2();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null && ((requestCode==Image_Request_Code)||(requestCode==Image_Request_Code2))) {

            FilePathUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                baos = new ByteArrayOutputStream();
                bitmap = getResizedBitmap(bitmap, 640);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                compImg = baos.toByteArray();
                Bitmap bitmap2 = BitmapFactory.decodeByteArray(compImg,0,compImg.length);

                if (requestCode == Image_Request_Code) {
                    SelectImage.setImageBitmap(bitmap2);
                    btRemove = (ImageButton)findViewById(R.id.btRemove1);
                    btRemove.setVisibility(View.VISIBLE);
                    foto = true;
                }else if(requestCode == Image_Request_Code2) {
                    SelectImage2.setImageBitmap(bitmap2);
                    btRemove2 = (ImageButton)findViewById(R.id.btRemove2);
                    btRemove2.setVisibility(View.VISIBLE);
                    foto2 = true;
                }

                Snackbar.make(svDaftar,"Foto berhasil dipilih.", Snackbar.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if ((requestCode == 1)||(requestCode == 2)||(requestCode == 3)||(requestCode == 4)) {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                baos = new ByteArrayOutputStream();
                bitmap = getResizedBitmap(bitmap, 640);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                compImg = baos.toByteArray();
                Bitmap bitmap2 = BitmapFactory.decodeByteArray(compImg, 0, compImg.length);
                if (requestCode == 1) {
                    SelectImage.setImageBitmap(bitmap2);
                    btRemove = (ImageButton) findViewById(R.id.btRemove1);
                    btRemove.setVisibility(View.VISIBLE);
                    foto = true;
                } else if (requestCode == 2) {
                    SelectImage2.setImageBitmap(bitmap2);
                    btRemove2 = (ImageButton) findViewById(R.id.btRemove2);
                    btRemove2.setVisibility(View.VISIBLE);
                    foto2 = true;
                }
                Snackbar.make(svDaftar, "Foto berhasil dipilih.", Snackbar.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void tandaiLokasi(View view) {
        Intent semua = new Intent(this, MapsActivity.class);
        semua.putExtra("id","daftar");
        startActivity(semua);
    }
}