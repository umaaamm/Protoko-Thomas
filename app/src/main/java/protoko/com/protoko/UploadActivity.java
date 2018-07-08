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
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import protoko.com.protoko.adapter.NumberTextWatcherForThousand;

public class UploadActivity extends AppCompatActivity {

    private String Storage_Path = "img/";
    private Button  UploadButton;
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
    private Spinner spKategori;
    private ScrollView svUpload;
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

    private String PromoUploadId;
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

    private static final String Database_Path2 = "protoko_db/toko";
    public static final String Database_Path = "protoko_db/produk";
    private static final String Database_Path_Tag = "protoko_db/tag";

    private DatabaseReference mDatabaseReference;
    private DatabaseReference databasetagref;
    private Query query;

    private String userName = "";
    private String namaToko = "";
    private String lokasiToko = "";
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
        setContentView(R.layout.activity_upload);

        svUpload = (ScrollView)findViewById(R.id.svUpload);

        storageReference = FirebaseStorage.getInstance().getReference();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            userName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }else{
            userName = "";
        }

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        databasetagref = FirebaseDatabase.getInstance().getReference(Database_Path_Tag);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path2);

        UploadButton = (Button)findViewById(R.id.ButtonUploadImage);
        etJudulPromo = (EditText)findViewById(R.id.etJudulPromo);
        etDeskripsiPromo = (EditText)findViewById(R.id.deskripsiPromo);

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

        etHargaLama = (EditText)findViewById(R.id.etHargaLama);
        etHargaLama.addTextChangedListener(new NumberTextWatcherForThousand(etHargaLama));
        etHargaBaru = (EditText)findViewById(R.id.etHargaBaru);
        etHargaBaru.addTextChangedListener(new NumberTextWatcherForThousand(etHargaBaru));
        etJumlahBeli = (EditText)findViewById(R.id.etBeli);
        etJumlahGratis = (EditText)findViewById(R.id.etGratis);
        etHargaProduk = (EditText)findViewById(R.id.etHargaProduk);
        etHargaProduk.addTextChangedListener(new NumberTextWatcherForThousand(etHargaProduk));
        etDurasiPromo = (EditText)findViewById(R.id.durasiPromo);
        spKategori = (Spinner)findViewById(R.id.sp_kategori);
        SelectImage = (ImageView)findViewById(R.id.gambarPromo);
        SelectImage2 = (ImageView)findViewById(R.id.gambarPromo2);
        SelectImage3 = (ImageView)findViewById(R.id.gambarPromo3);
        SelectImage4 = (ImageView)findViewById(R.id.gambarPromo4);
        progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadProduk();
            }
        });
        query = mDatabaseReference.child(userName+"/info");
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                namaToko = snapshot.child("namaToko").getValue(String.class);
                lokasiToko = snapshot.child("kec").getValue(String.class);
                latlong = snapshot.child("lokasiPeta").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void clearText(){
        etJudulPromo.setText("");
        etDeskripsiPromo.setText("");
        etHargaLama.setText("");
        etHargaBaru.setText("");
        etJumlahBeli.setText("");
        etJumlahGratis.setText("");
        etHargaProduk.setText("");
        etDurasiPromo.setText("");
        SelectImage.setImageResource(R.drawable.ic_add_img);
        SelectImage2.setImageResource(R.drawable.ic_add_img2);
        SelectImage3.setImageResource(R.drawable.ic_add_img2);
        SelectImage4.setImageResource(R.drawable.ic_add_img);
        etJudulPromo.requestFocus();
        hideSoftKeyboard();
    }

    private void clearHarga(){
        etHargaLama.setText("");
        etHargaBaru.setText("");
        etJumlahBeli.setText("");
        etJumlahGratis.setText("");
        etHargaProduk.setText("");
        removeImg4();
    }

    private void hideSoftKeyboard() {
        if(getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void onRadioButtonClicked(View view) {

        final LinearLayout layDiskon = (LinearLayout)findViewById(R.id.layDiskon);
        final LinearLayout layGratis = (LinearLayout)findViewById(R.id.layGratis);
        final LinearLayout layGratisProdukLain = (LinearLayout)findViewById(R.id.layGratisProdukLain);
        final LinearLayout layGambar3 = (LinearLayout)findViewById(R.id.layGambar3);
        rbDiskon = (RadioButton)findViewById(R.id.radioDiskon);
        rbGratisSama = (RadioButton)findViewById(R.id.radioGratis);
        rbGratisBeda = (RadioButton)findViewById(R.id.radioGratisProdukLain);

        switch(view.getId()) {
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

        if (resultCode == RESULT_OK && data != null && data.getData() != null && ((requestCode==Image_Request_Code)||(requestCode==Image_Request_Code2)||(requestCode==Image_Request_Code3)||(requestCode==Image_Request_Code4))) {

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
                }else if(requestCode == Image_Request_Code3) {
                    SelectImage3.setImageBitmap(bitmap2);
                    btRemove3 = (ImageButton)findViewById(R.id.btRemove3);
                    btRemove3.setVisibility(View.VISIBLE);
                    foto3 = true;
                }else if(requestCode == Image_Request_Code4) {
                    SelectImage4.setImageBitmap(bitmap2);
                    btRemove4 = (ImageButton)findViewById(R.id.btRemove4);
                    btRemove4.setVisibility(View.VISIBLE);
                    foto4 = true;
                }
                Snackbar.make(svUpload,"Gambar berhasil dipilih.", Snackbar.LENGTH_SHORT).show();
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
                } else if (requestCode == 3) {
                    SelectImage3.setImageBitmap(bitmap2);
                    btRemove3 = (ImageButton) findViewById(R.id.btRemove3);
                    btRemove3.setVisibility(View.VISIBLE);
                    foto3 = true;
                } else {
                    SelectImage4.setImageBitmap(bitmap2);
                    btRemove4 = (ImageButton) findViewById(R.id.btRemove4);
                    btRemove4.setVisibility(View.VISIBLE);
                    foto4 = true;
                }
                Snackbar.make(svUpload, "Gambar berhasil dipilih.", Snackbar.LENGTH_SHORT).show();
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
        boolean fotoBonus = true;
        if((jenisPromo.contains("Lain"))!=(foto4)){
            fotoBonus = false;
        }
        if ((foto) && (etJudulPromo.getText().toString().length()>4) &&
                (etDeskripsiPromo.getText().toString().length()>4)
                &&(!etDurasiPromo.getText().toString().isEmpty())
                && (((!etHargaLama.getText().toString().isEmpty()) && (!etDurasiPromo.getText().toString().isEmpty()))||(!etHargaProduk.getText().toString().isEmpty()))
                && (fotoBonus)
                ) {

            progressDialog.setMessage("Proses ...");
            progressDialog.setCancelable(false);
            progressDialog.show();

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
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }

            if(foto3) {
                Bitmap bitmapThree = ((BitmapDrawable)SelectImage3.getDrawable()).getBitmap();
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

            if(foto4) {
                Bitmap bitmapFour = ((BitmapDrawable)SelectImage4.getDrawable()).getBitmap();
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

            Bitmap bitmapOne = ((BitmapDrawable)SelectImage.getDrawable()).getBitmap();
            ByteArrayOutputStream imageOneBytes = new ByteArrayOutputStream();
            bitmapOne.compress(Bitmap.CompressFormat.JPEG, 100, imageOneBytes);
            byte[] dataOne = imageOneBytes.toByteArray();

            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + ".jpg" );
            storageReference2nd.putBytes(dataOne)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (jenisPromo.contains("Diskon")) {
                                etHargaProduk.setText(etHargaBaru.getText().toString());
                                etJumlahBeli.setText("1");
                                etJumlahGratis.setText("1");
                            }else{
                                etHargaBaru.setText(etHargaProduk.getText().toString());
                                etHargaLama.setText(etHargaProduk.getText().toString());
                            }

                            String judulPromo = etJudulPromo.getText().toString().trim();
                            String deskripsi = etDeskripsiPromo.getText().toString().trim();
                            int hargaLama = Integer.parseInt(etHargaLama.getText().toString().replace(".","").trim());
                            int hargaBaru = Integer.parseInt(etHargaBaru.getText().toString().replace(".","").trim());
                            int jumlahBeli = Integer.parseInt(etJumlahBeli.getText().toString().trim());
                            int jumlahGratis = Integer.parseInt(etJumlahGratis.getText().toString().trim());
                            String kategori = spKategori.getSelectedItem().toString();
                            String time = String.valueOf(System.currentTimeMillis());
                            String id = String.valueOf((9999999999999L+(-1 * Long.valueOf(time))));
                            String durasi = etDurasiPromo.getText().toString().trim();

                            progressDialog.dismiss();

                            Snackbar.make(svUpload,"Promo berhasil dipasang.", Snackbar.LENGTH_SHORT).show();
                            foto = false;
                            foto2 = false;
                            foto3 = false;
                            foto4 = false;

                            try {
                                baos.flush();
                                baos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            PromoUploadId = databaseReference.push().getKey();

                            @SuppressWarnings("VisibleForTests")
                            PromoUpload PromoUpload = new PromoUpload(id, PromoUploadId,taskSnapshot.getDownloadUrl().toString(), imgURL2, imgURL3, imgURL4, judulPromo, jenisPromo, namaToko, lokasiToko, hargaLama, hargaBaru, jumlahBeli, jumlahGratis, deskripsi, kategori, time, durasi, time, latlong, 0);

                            databaseReference.child(PromoUploadId).setValue(PromoUpload);
                            tag_arr = judulPromo.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", " ").split(" ");
                            String tagUpload = "";
                            for(int k = 0; k < tag_arr.length; k++){
                                for (int i = 0; i < tag_arr.length; i++)
                                {
                                    for (int j = k; j <= i; j++)
                                    {
                                        tagUpload+= tag_arr[j] + " ";
                                    }
                                    hasLetter = letter.matcher(tagUpload);
                                    if((tagUpload.length()<3)||(!hasLetter.find())){
                                        tagUpload = "";
                                    }
                                    if((!tagUpload.isEmpty())&&(tagUpload.length()>2)) {
                                        databasetagref.child(tagUpload).child(PromoUploadId).setValue(PromoUploadId);
                                        tagUpload = "";
                                    }
                                }
                                tagUpload = "";
                            }
                            clearText();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            exception.getMessage();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setMessage("Proses ...");
                        }
                    });
        }
        else {
            Snackbar.make(svUpload,"Data belum lengkap.", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void uploadGambar(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Upload Gambar Promo")
                .setCancelable(true)
                .setPositiveButton("Galeri",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), Image_Request_Code);
                    }
                })
                .setNegativeButton("Kamera",new DialogInterface.OnClickListener() {
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
                .setMessage("Upload Gambar Promo")
                .setCancelable(true)
                .setPositiveButton("Galeri",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), Image_Request_Code2);
                    }
                })
                .setNegativeButton("Kamera",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
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
                .setPositiveButton("Galeri",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), Image_Request_Code3);
                    }
                })
                .setNegativeButton("Kamera",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
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
                .setPositiveButton("Galeri",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), Image_Request_Code4);
                    }
                })
                .setNegativeButton("Kamera",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
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
        btRemove = (ImageButton)findViewById(R.id.btRemove1);
        btRemove.setVisibility(View.GONE);
        foto = false;
    }

    private void removeImg2() {
        SelectImage2.setImageResource(R.drawable.ic_add_img);
        btRemove2 = (ImageButton)findViewById(R.id.btRemove2);
        btRemove2.setVisibility(View.GONE);
        foto2 = false;
    }

    private void removeImg3() {
        SelectImage3.setImageResource(R.drawable.ic_add_img);
        btRemove3 = (ImageButton)findViewById(R.id.btRemove3);
        btRemove3.setVisibility(View.GONE);
        foto3 = false;
    }

    private void removeImg4() {
        SelectImage4.setImageResource(R.drawable.ic_add_img);
        btRemove4 = (ImageButton)findViewById(R.id.btRemove4);
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
}