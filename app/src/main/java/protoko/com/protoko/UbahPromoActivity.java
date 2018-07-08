package protoko.com.protoko;

import android.app.ProgressDialog;
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
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import protoko.com.protoko.adapter.NumberTextWatcherForThousand;

public class UbahPromoActivity extends AppCompatActivity {

    private String Storage_Path = "img/";
    private int pos;
    private String namaProduk;
    private String deskripsiProduk;
    private String durasiPromo;
    private String hargaBaru;
    private String hargaLama;
    private String imgUrl;
    private String imgUrl2;
    private String imgUrl3;
    private String imgUrl4;
    private String jenisPromo_2;
    private String jumlahBeli;
    private String jumlahGratis;
    private String kategori;
    private String zID;
    private String id;
    private String updateTime;
    private String time;

    private Button UploadButton;
    private Button btUbahGambar;
    private Button btUbahGambar_Cancel;
    private EditText etJudulPromo;
    private EditText etDeskripsiPromo;
    private EditText etHargaLama;
    private EditText etHargaBaru;
    private EditText etJumlahBeli;
    private EditText etJumlahGratis;
    private EditText etHargaProduk;
    private EditText etDurasiPromo;
    private ImageView SelectImage;
    private ImageView SelectImage2;
    private ImageView SelectImage3;
    private ImageView SelectImage4;
    private LinearLayout llUbahGambar;
    private Spinner spKategori;
    private ScrollView svUbah;
    private ImageButton btRemove;
    private ImageButton btRemove2;
    private ImageButton btRemove3;
    private ImageButton btRemove4;
    private Uri FilePathUri;
    private Pattern letter = Pattern.compile("[a-z]");
    private Matcher hasLetter;
    private RadioButton rbDiskon;
    private RadioButton rbGratisSama;
    private RadioButton rbGratisBeda;
    private String imgURL = "null";
    private String imgURL2 = "null";
    private String imgURL3 = "null";
    private String imgURL4 = "null";
    private String[] tag_arr;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String jenisPromo = "Diskon";
    private boolean foto = false;
    private boolean foto2 = false;
    private boolean foto3 = false;
    private boolean foto4 = false;
    private boolean ubah = false;
    private boolean readyUpload = true;

    private static final String Database_Path2 = "protoko_db/toko";
    private static final String Database_Path = "protoko_db/produk";
    private static final String Database_Path_Tag = "protoko_db/tag";

    private DatabaseReference mDatabaseReference;
    private DatabaseReference databasetagref;
    private Query query;

    private String userName = "";
    private String namaToko = "";
    private String lokasiToko = "";
    private String alamatToko = "";
    private String teleponToko = "";
    private String latlong = "";
    private int Image_Request_Code = 7;
    private int Image_Request_Code2 = 8;
    private int Image_Request_Code3 = 9;
    private int Image_Request_Code4 = 10;

    private byte[] compImg;
    private ByteArrayOutputStream baos;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_promo);

        svUbah = (ScrollView) findViewById(R.id.svUbah);
        llUbahGambar = (LinearLayout)findViewById(R.id.llGambar);
        storageReference = FirebaseStorage.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            try {
                userName = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        } else {
            userName = "";
        }
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        databasetagref = FirebaseDatabase.getInstance().getReference(Database_Path_Tag);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path2);
        UploadButton = (Button) findViewById(R.id.ButtonUploadImage);
        btUbahGambar = (Button) findViewById(R.id.btUbahGambar);
        btUbahGambar_Cancel = (Button) findViewById(R.id.btUbahGambar_Cancel);
        etJudulPromo = (EditText) findViewById(R.id.etJudulPromo);
        etDeskripsiPromo = (EditText) findViewById(R.id.deskripsiPromo);
        rbDiskon = (RadioButton) findViewById(R.id.radioDiskon);
        rbGratisSama = (RadioButton) findViewById(R.id.radioGratis);
        rbGratisBeda = (RadioButton) findViewById(R.id.radioGratisProdukLain);

        final LinearLayout layDiskon = (LinearLayout) findViewById(R.id.layDiskon);
        final LinearLayout layGratis = (LinearLayout) findViewById(R.id.layGratis);
        final LinearLayout layGratisProdukLain = (LinearLayout) findViewById(R.id.layGratisProdukLain);
        final LinearLayout layGambar3 = (LinearLayout) findViewById(R.id.layGambar3);

        etDeskripsiPromo.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() == R.id.deskripsiPromo) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        etHargaLama = (EditText) findViewById(R.id.etHargaLama);
        etHargaLama.addTextChangedListener(new NumberTextWatcherForThousand(etHargaLama));
        etHargaBaru = (EditText) findViewById(R.id.etHargaBaru);
        etHargaBaru.addTextChangedListener(new NumberTextWatcherForThousand(etHargaBaru));
        etJumlahBeli = (EditText) findViewById(R.id.etBeli);
        etJumlahGratis = (EditText) findViewById(R.id.etGratis);
        etHargaProduk = (EditText) findViewById(R.id.etHargaProduk);
        etHargaProduk.addTextChangedListener(new NumberTextWatcherForThousand(etHargaProduk));
        etDurasiPromo = (EditText) findViewById(R.id.durasiPromo);
        spKategori = (Spinner) findViewById(R.id.sp_kategori);
        query = mDatabaseReference.child(userName + "/info");
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                namaToko = snapshot.child("namaToko").getValue(String.class);
                lokasiToko = snapshot.child("kec").getValue(String.class);
                alamatToko = snapshot.child("alamatToko").getValue(String.class);
                teleponToko = snapshot.child("telpToko").getValue(String.class);
                latlong = snapshot.child("lokasiPeta").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        SelectImage = (ImageView) findViewById(R.id.gambarPromo);
        SelectImage2 = (ImageView) findViewById(R.id.gambarPromo2);
        SelectImage3 = (ImageView) findViewById(R.id.gambarPromo3);
        SelectImage4 = (ImageView) findViewById(R.id.gambarPromo4);

        Intent i = getIntent();
        Bundle bd = i.getExtras();
        pos = (int) bd.get("pos");
        namaProduk = (String) bd.get("namaProduk");
        deskripsiProduk = (String) bd.get("deskripsiProduk");
        durasiPromo = (String) bd.get("durasiPromo");
        hargaBaru = (String) bd.get("hargaBaru");
        hargaLama = (String) bd.get("hargaLama");
        imgUrl = (String) bd.get("imgUrl");
        imgUrl2 = (String) bd.get("imgUrl2");
        imgUrl3 = (String) bd.get("imgUrl3");
        imgUrl4 = (String) bd.get("imgUrl4");
        jenisPromo_2 = (String) bd.get("jenisPromo");
        jumlahBeli = (String) bd.get("jumlahBeli");
        jumlahGratis = (String) bd.get("jumlahGratis");
        kategori = (String) bd.get("kategori");
        zID = (String) bd.get("zID");
        id = (String) bd.get("id");
        time = (String) bd.get("time");

        etJudulPromo.setText(namaProduk);
        etDeskripsiPromo.setText(deskripsiProduk);
        etDurasiPromo.setText(durasiPromo);
        etHargaBaru.setText(hargaBaru);
        etHargaLama.setText(hargaLama);
        etHargaProduk.setText(hargaBaru);
        etJumlahBeli.setText(jumlahBeli);
        etJumlahGratis.setText(jumlahGratis);

        ArrayAdapter<CharSequence> adpKategori = ArrayAdapter.createFromResource(this, R.array.kategori, R.layout.support_simple_spinner_dropdown_item);
        adpKategori.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spKategori.setAdapter(adpKategori);
        if(!kategori.equals(null)){
            int kategoriPos = adpKategori.getPosition(kategori);
            spKategori.setSelection(kategoriPos);
        }

        if(jenisPromo_2.contains("Diskon")){
            layDiskon.setVisibility(View.VISIBLE);
            layGratis.setVisibility(View.GONE);
            layGambar3.setVisibility(View.VISIBLE);
            jenisPromo = "Diskon";
            rbGratisBeda.setChecked(false);
            rbGratisSama.setChecked(false);
            rbDiskon.setChecked(true);
        }else if(jenisPromo_2.contains("Gratis Produk Sama")){
            layGratis.setVisibility(View.VISIBLE);
            layDiskon.setVisibility(View.GONE);
            layGratisProdukLain.setVisibility(View.INVISIBLE);
            layGambar3.setVisibility(View.VISIBLE);
            jenisPromo = "Gratis Produk Sama";
            rbDiskon.setChecked(false);
            rbGratisBeda.setChecked(false);
            rbGratisSama.setChecked(true);
        }else if(jenisPromo_2.contains("Gratis Produk Lain")){
            layGratis.setVisibility(View.VISIBLE);
            layDiskon.setVisibility(View.GONE);
            layGratisProdukLain.setVisibility(View.VISIBLE);
            layGambar3.setVisibility(View.INVISIBLE);
            jenisPromo = "Gratis Produk Lain";
            rbDiskon.setChecked(false);
            rbGratisSama.setChecked(false);
            rbGratisBeda.setChecked(true);
        }

        bd.remove("pos");
        bd.remove("namaProduk");
        bd.remove("deskripsiProduk");
        bd.remove("durasiPromo");
        bd.remove("hargaBaru");
        bd.remove("hargaLama");
        bd.remove("imgUrl");
        bd.remove("imgUrl2");
        bd.remove("imgUrl3");
        bd.remove("imgUrl4");
        bd.remove("jenisPromo");
        bd.remove("jumlahBeli");
        bd.remove("jumlahGratis");
        bd.remove("kategori");
        bd.remove("zID");
        bd.remove("id");
        bd.remove("time");

        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadProduk();
            }
        });
    }

    private void clearHarga() {
        etHargaLama.setText("");
        etHargaBaru.setText("");
        etJumlahBeli.setText("");
        etJumlahGratis.setText("");
        etHargaProduk.setText("");
        removeImg4();
    }

    public void onRadioButtonClicked(View view) {

        final LinearLayout layDiskon = (LinearLayout) findViewById(R.id.layDiskon);
        final LinearLayout layGratis = (LinearLayout) findViewById(R.id.layGratis);
        final LinearLayout layGratisProdukLain = (LinearLayout) findViewById(R.id.layGratisProdukLain);
        final LinearLayout layGambar3 = (LinearLayout) findViewById(R.id.layGambar3);
        rbDiskon = (RadioButton) findViewById(R.id.radioDiskon);
        rbGratisSama = (RadioButton) findViewById(R.id.radioGratis);
        rbGratisBeda = (RadioButton) findViewById(R.id.radioGratisProdukLain);

        switch (view.getId()) {
            case R.id.radioDiskon:
                layDiskon.setVisibility(View.VISIBLE);
                layGratis.setVisibility(View.GONE);
                layGambar3.setVisibility(View.VISIBLE);
                jenisPromo = "Diskon";
                rbGratisBeda.setChecked(false);
                rbGratisSama.setChecked(false);
                clearHarga();
                break;
            case R.id.radioGratis:
                layGratis.setVisibility(View.VISIBLE);
                layDiskon.setVisibility(View.GONE);
                layGratisProdukLain.setVisibility(View.INVISIBLE);
                layGambar3.setVisibility(View.VISIBLE);
                jenisPromo = "Gratis Produk Sama";
                rbDiskon.setChecked(false);
                rbGratisBeda.setChecked(false);
                clearHarga();
                break;
            case R.id.radioGratisProdukLain:
                layGratis.setVisibility(View.VISIBLE);
                layDiskon.setVisibility(View.GONE);
                layGratisProdukLain.setVisibility(View.VISIBLE);
                layGambar3.setVisibility(View.INVISIBLE);
                rbDiskon.setChecked(false);
                rbGratisSama.setChecked(false);
                clearHarga();
                jenisPromo = "Gratis Produk Lain";
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null && ((requestCode == Image_Request_Code) || (requestCode == Image_Request_Code2) || (requestCode == Image_Request_Code3) || (requestCode == Image_Request_Code4))) {

            FilePathUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                baos = new ByteArrayOutputStream();
                bitmap = getResizedBitmap(bitmap, 640);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                compImg = baos.toByteArray();
                Bitmap bitmap2 = BitmapFactory.decodeByteArray(compImg, 0, compImg.length);

                if (requestCode == Image_Request_Code) {
                    SelectImage.setImageBitmap(bitmap2);
                    btRemove = (ImageButton) findViewById(R.id.btRemove1);
                    btRemove.setVisibility(View.VISIBLE);
                    foto = true;
                    readyUpload = true;
                } else if (requestCode == Image_Request_Code2) {
                    SelectImage2.setImageBitmap(bitmap2);
                    btRemove2 = (ImageButton) findViewById(R.id.btRemove2);
                    btRemove2.setVisibility(View.VISIBLE);
                    foto2 = true;
                } else if (requestCode == Image_Request_Code3) {
                    SelectImage3.setImageBitmap(bitmap2);
                    btRemove3 = (ImageButton) findViewById(R.id.btRemove3);
                    btRemove3.setVisibility(View.VISIBLE);
                    foto3 = true;
                } else if (requestCode == Image_Request_Code4) {
                    SelectImage4.setImageBitmap(bitmap2);
                    btRemove4 = (ImageButton) findViewById(R.id.btRemove4);
                    btRemove4.setVisibility(View.VISIBLE);
                    foto4 = true;
                }

                Snackbar.make(svUbah, "Gambar berhasil dipilih.", Snackbar.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if ((requestCode == 1) || (requestCode == 2) || (requestCode == 3) || (requestCode == 4)) {
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
                    readyUpload = true;
                } else if (requestCode == 2) {
                    SelectImage2.setImageBitmap(bitmap2);
                    btRemove2 = (ImageButton) findViewById(R.id.btRemove2);
                    btRemove2.setVisibility(View.VISIBLE);
                    foto2 = true;
                } else if (requestCode == 3) {
                    SelectImage3.setImageBitmap(bitmap2);
                    btRemove3 = (ImageButton) findViewById(R.id.btRemove3);
                    btRemove3.setVisibility(View.VISIBLE);
                    foto3 = true;
                } else if (requestCode == 4) {
                    SelectImage4.setImageBitmap(bitmap2);
                    btRemove4 = (ImageButton) findViewById(R.id.btRemove4);
                    btRemove4.setVisibility(View.VISIBLE);
                    foto4 = true;
                }
                Snackbar.make(svUbah, "Gambar berhasil dipilih.", Snackbar.LENGTH_SHORT).show();
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

    private void UploadProduk() {

        if ((readyUpload)&&(etJudulPromo.getText().toString().length() > 4) &&
                (etDeskripsiPromo.getText().toString().length() > 4)
                && (!etDurasiPromo.getText().toString().isEmpty())
                && (((!etHargaLama.getText().toString().isEmpty()) && (!etDurasiPromo.getText().toString().isEmpty())) || (!etHargaProduk.getText().toString().isEmpty()))
                ) {

            progressDialog.setMessage("Proses ...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            if(ubah) {
                if (foto2) {
                    removeFile(imgUrl2);
                    Bitmap bitmapTwo = ((BitmapDrawable) SelectImage2.getDrawable()).getBitmap();
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
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }

                if (foto3) {
                    removeFile(imgUrl3);
                    Bitmap bitmapThree = ((BitmapDrawable) SelectImage3.getDrawable()).getBitmap();
                    ByteArrayOutputStream imageThreeBytes = new ByteArrayOutputStream();
                    bitmapThree.compress(Bitmap.CompressFormat.JPEG, 100, imageThreeBytes);
                    byte[] dataThree = imageThreeBytes.toByteArray();

                    StorageReference filepathThree = storageReference.child(Storage_Path + System.currentTimeMillis() + ".jpg");
                    filepathThree.putBytes(dataThree).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            try {
                                imgURL3 = downloadUrl.toString();
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }

                if (foto4) {
                    removeFile(imgUrl4);
                    Bitmap bitmapFour = ((BitmapDrawable) SelectImage4.getDrawable()).getBitmap();
                    ByteArrayOutputStream imageFourBytes = new ByteArrayOutputStream();
                    bitmapFour.compress(Bitmap.CompressFormat.JPEG, 100, imageFourBytes);
                    byte[] dataFour = imageFourBytes.toByteArray();

                    StorageReference filepathFour = storageReference.child(Storage_Path + System.currentTimeMillis() + ".jpg");
                    filepathFour.putBytes(dataFour).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            try {
                                imgURL4 = downloadUrl.toString();
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }

                if(foto) {
                    removeFile(imgUrl);
                    Bitmap bitmapOne = ((BitmapDrawable) SelectImage.getDrawable()).getBitmap();
                    final ByteArrayOutputStream imageOneBytes = new ByteArrayOutputStream();
                    bitmapOne.compress(Bitmap.CompressFormat.JPEG, 100, imageOneBytes);
                    final byte[] dataOne = imageOneBytes.toByteArray();

                    StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + ".jpg");
                    storageReference2nd.putBytes(dataOne)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    @SuppressWarnings("VisibleForTests")
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    try {
                                        imgURL = downloadUrl.toString();
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }
                                    ubahData();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                }
            }else{
                imgURL = imgUrl;
                imgURL2 = imgUrl2;
                imgURL3 = imgUrl3;
                imgURL4 = imgUrl4;
                ubahData();
            }
        } else {
            Snackbar.make(svUbah, "Data belum lengkap.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void ubahData(){
        if (jenisPromo == "Diskon") {
            etHargaProduk.setText(etHargaBaru.getText().toString());
            etJumlahBeli.setText("1");
            etJumlahGratis.setText("1");
        } else {
            etHargaBaru.setText(etHargaProduk.getText().toString());
            etHargaLama.setText(etHargaProduk.getText().toString());
        }

        String judulPromo = etJudulPromo.getText().toString().trim();
        String deskripsi = etDeskripsiPromo.getText().toString().trim();
        int hargaLama = Integer.parseInt(etHargaLama.getText().toString().replace(".", "").trim());
        int hargaBaru = Integer.parseInt(etHargaBaru.getText().toString().replace(".", "").trim());
        int jumlahBeli = Integer.parseInt(etJumlahBeli.getText().toString().trim());
        int jumlahGratis = Integer.parseInt(etJumlahGratis.getText().toString().trim());
        String kategori = spKategori.getSelectedItem().toString();
        updateTime = String.valueOf(System.currentTimeMillis());

        String durasi = etDurasiPromo.getText().toString().trim();

        PromoUpload PromoUpload = new PromoUpload(id, zID, imgURL, imgURL2, imgURL3, imgURL4, judulPromo, jenisPromo, namaToko, lokasiToko, hargaLama, hargaBaru, jumlahBeli, jumlahGratis, deskripsi, kategori, time, durasi, updateTime, latlong, 0);

        databaseReference.child(zID).removeValue();
        tag_arr = namaProduk.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", " ").split(" ");
        String tagUpload_2 = "";
        for (int k = 0; k < tag_arr.length; k++) {
            for (int i = 0; i < tag_arr.length; i++) {
                for (int j = k; j <= i; j++) {
                    tagUpload_2 += tag_arr[j] + " ";
                }
                hasLetter = letter.matcher(tagUpload_2);
                if ((tagUpload_2.length() < 3) || (!hasLetter.find())) {
                    tagUpload_2 = "";
                }
                if ((!tagUpload_2.isEmpty()) && (tagUpload_2.length() > 2)) {
                    databasetagref.child(tagUpload_2).child(zID).removeValue();
                    tagUpload_2 = "";
                }
            }
            tagUpload_2 = "";
        }

        databaseReference.child(zID).setValue(PromoUpload);
        tag_arr = judulPromo.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", " ").split(" ");
        String tagUpload = "";
        for (int k = 0; k < tag_arr.length; k++) {
            for (int i = 0; i < tag_arr.length; i++) {
                for (int j = k; j <= i; j++) {
                    tagUpload += tag_arr[j] + " ";
                }
                hasLetter = letter.matcher(tagUpload);
                if ((tagUpload.length() < 3) || (!hasLetter.find())) {
                    tagUpload = "";
                }
                if ((!tagUpload.isEmpty()) && (tagUpload.length() > 2)) {
                    databasetagref.child(tagUpload).child(zID).setValue(zID);
                    tagUpload = "";
                }
            }
            tagUpload = "";
        }
        progressDialog.dismiss();

        Snackbar.make(svUbah, "Promo berhasil diubah.", Snackbar.LENGTH_SHORT).show();
        final Intent intent = new Intent(this, KatalogActivity.class);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        startActivity(intent);
                    }
                }, 1000);
    }

    public void uploadGambar(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Upload Gambar Promo")
                .setCancelable(true)
                .setPositiveButton("Galeri", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), Image_Request_Code);
                    }
                })
                .setNegativeButton("Kamera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ambilGambar();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void uploadGambar2(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Upload Gambar Promo")
                .setCancelable(true)
                .setPositiveButton("Galeri", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), Image_Request_Code2);
                    }
                })
                .setNegativeButton("Kamera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ambilGambar2();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void uploadGambar3(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Upload Gambar Promo")
                .setCancelable(true)
                .setPositiveButton("Galeri", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), Image_Request_Code3);
                    }
                })
                .setNegativeButton("Kamera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ambilGambar3();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void uploadGambar4(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Upload Gambar Promo")
                .setCancelable(true)
                .setPositiveButton("Galeri", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), Image_Request_Code4);
                    }
                })
                .setNegativeButton("Kamera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ambilGambar4();
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

    private void ambilGambar3() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 3);
    }

    private void ambilGambar4() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 4);
    }

    private void removeImg1() {
        SelectImage.setImageResource(R.drawable.ic_add_img);
        btRemove = (ImageButton) findViewById(R.id.btRemove1);
        btRemove.setVisibility(View.GONE);
        foto = false;
    }

    private void removeImg2() {
        SelectImage2.setImageResource(R.drawable.ic_add_img2);
        btRemove2 = (ImageButton) findViewById(R.id.btRemove2);
        btRemove2.setVisibility(View.GONE);
        foto2 = false;
    }

    private void removeImg3() {
        SelectImage3.setImageResource(R.drawable.ic_add_img2);
        btRemove3 = (ImageButton) findViewById(R.id.btRemove3);
        btRemove3.setVisibility(View.GONE);
        foto3 = false;
    }

    private void removeImg4() {
        SelectImage4.setImageResource(R.drawable.ic_add_img);
        btRemove4 = (ImageButton) findViewById(R.id.btRemove4);
        btRemove4.setVisibility(View.GONE);
        foto4 = false;
    }

    public void removeImg1v(View view) {
        removeImg1();
    }

    public void removeImg2v(View view) {
        removeImg2();
    }

    public void removeImg3v(View view) {
        removeImg3();
    }

    public void removeImg4v(View view) {
        removeImg4();
    }

    public void ubahGambar(View view) {
        llUbahGambar.setVisibility(View.VISIBLE);
        btUbahGambar.setVisibility(View.GONE);
        btUbahGambar_Cancel.setVisibility(View.VISIBLE);
        ubah = true;
        readyUpload = false;
    }

    public void ubahGambar_cancel(View view) {
        llUbahGambar.setVisibility(View.GONE);
        btUbahGambar.setVisibility(View.VISIBLE);
        btUbahGambar_Cancel.setVisibility(View.GONE);
        foto = false;
        foto2 = false;
        foto3 = false;
        foto4 = false;
    }

    private void removeFile(String url) {
        if (!url.contains("null")) {
            String urlStr = url.split("img%2F")[1];
            String urlStr2 = urlStr.split("\\?")[0];
            StorageReference fileDelete = FirebaseStorage.getInstance().getReference().child("img/" + urlStr2);
            fileDelete.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
    }
}