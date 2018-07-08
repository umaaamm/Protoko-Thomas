package protoko.com.protoko;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.nineoldandroids.view.ViewHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static protoko.com.protoko.RecyclerViewKatalogAdapter.SECONDS_IN_A_DAY;

public class ProdukDetilActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

    private String namaProduk;
    private String deskripsiProduk;
    private String durasiPromo;
    private String hargaBaru;
    private String hargaLama;
    private String imgUrl;
    private String imgUrl2;
    private String imgUrl3;
    private String imgUrl4;
    private String jenisPromo;
    private String jumlahBeli;
    private String jumlahGratis;
    private String kategori;
    private String lokasi;
    private String namaToko;
    private String updateTime;
    private String time;
    private String diskon;
    private String updatePromo;
    private String alamatToko;
    private String latlong;

    private static final String Database_Path = "protoko_db/toko";
    private DatabaseReference mDatabaseReference;

    private ViewPager viewPager;
    private RelativeLayout relProduk;
    private LinearLayout sliderDotspanel;
    private LinearLayout llImgBonus;
    private int dotscount;
    private ImageView[] dots;

    private TextView tvNamaProduk;
    private TextView tvHargaProduk;
    private TextView tvHargaLama;
    private TextView tvHargaBaru;
    private TextView tvDeskripsiProduk;
    private TextView tvDurasiPromo;
    private TextView tvJenisPromo;
    private TextView tvKategoriPromo;
    private TextView tvPromoBerakhir;
    private TextView tvNamaToko;
    private TextView tvAlamatToko;
    private TextView tvKec;
    private TextView tvTelepon;
    private TextView tvDiskon;
    private TextView tvUpdate;
    private ImageView gambar4;
    private ImageView gambar4_load;

    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("");
        setContentView(R.layout.activity_produk_detil);
        tvNamaProduk = (TextView) findViewById(R.id.tvNamaProduk);
        tvHargaProduk = (TextView) findViewById(R.id.tvHargaProduk);
        tvHargaLama = (TextView) findViewById(R.id.tvHargaLama);
        tvHargaBaru = (TextView) findViewById(R.id.tvHargaBaru);
        tvDeskripsiProduk = (TextView) findViewById(R.id.tvDeskripsiProduk);
        tvDurasiPromo = (TextView) findViewById(R.id.tvDurasiPromo);
        tvJenisPromo = (TextView) findViewById(R.id.tvJenisPromo);
        tvKategoriPromo = (TextView) findViewById(R.id.tvKategoriPromo);
        tvPromoBerakhir = (TextView) findViewById(R.id.tvPromoBerakhir2);
        tvNamaToko = (TextView) findViewById(R.id.tvNamaTokoPromo);
        tvAlamatToko = (TextView) findViewById(R.id.tvAlamatTokoPromo);
        tvKec = (TextView) findViewById(R.id.tvKecamatanTokoPromo);
        tvTelepon = (TextView) findViewById(R.id.tvTeleponTokoPromo);
        tvDiskon = (TextView) findViewById(R.id.tvDiskon);
        tvUpdate = (TextView) findViewById(R.id.tvUpdatePromo);

        gambar4 = (ImageView) findViewById(R.id.imgPromo4);
        gambar4_load = (ImageView) findViewById(R.id.imgPromo4_load);
        llImgBonus = (LinearLayout) findViewById(R.id.llImgBonus);

        relProduk = (RelativeLayout) findViewById(R.id.relProduk);

        Intent i = getIntent();
        Bundle bd = i.getExtras();
        namaProduk = (String) bd.get("namaProduk");
        deskripsiProduk = (String) bd.get("deskripsiProduk");
        durasiPromo = (String) bd.get("durasiPromo");
        hargaBaru = (String) bd.get("hargaBaru");
        hargaLama = (String) bd.get("hargaLama");
        imgUrl = (String) bd.get("imgUrl");
        imgUrl2 = (String) bd.get("imgUrl2");
        imgUrl3 = (String) bd.get("imgUrl3");
        imgUrl4 = (String) bd.get("imgUrl4");
        jenisPromo = (String) bd.get("jenisPromo");
        jumlahBeli = (String) bd.get("jumlahBeli");
        jumlahGratis = (String) bd.get("jumlahGratis");
        kategori = (String) bd.get("kategori");
        lokasi = (String) bd.get("lokasi");
        namaToko = (String) bd.get("namaToko");
        getInfo(namaToko);
        updateTime = (String) bd.get("updateTime");
        time = (String) bd.get("time");
        diskon = String.valueOf(bd.get("diskon"));

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
        bd.remove("lokasi");
        bd.remove("namaToko");
        bd.remove("updateTime");
        bd.remove("time");
        bd.remove("diskon");

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);

        String[] img = {imgUrl, imgUrl2, imgUrl3};
        int k = 0;
        for (String link : img) {
            if (!link.contains("null")) {
                viewPagerAdapter.setImage(link, k);
                k++;
            }
        }

        viewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.setFullscreen(true);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for (int j = 0; j < dotscount; j++) {

            dots[j] = new ImageView(this);
            dots[j].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[j], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 4000);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbarbagus));
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        mToolbarView = findViewById(R.id.toolbarbagus);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.colorPrimary)));

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        if (jenisPromo.contains("Gratis Produk")) {
            tvHargaProduk.setVisibility(View.VISIBLE);
            tvDiskon.setVisibility(View.GONE);
            tvHargaProduk.setText("Rp. " + getMoney(String.valueOf(hargaBaru)));
            tvHargaLama.setText("Beli : " + String.valueOf(jumlahBeli));
            if (jenisPromo.contains("Gratis Produk Lain")) {
                tvHargaBaru.setText("Gratis Produk Lain : " + String.valueOf(jumlahGratis));
            } else {
                tvHargaBaru.setText("Gratis Produk Sama : " + String.valueOf(jumlahGratis));
            }
            tvHargaLama.setPaintFlags(0);
        } else {
            tvHargaProduk.setVisibility(View.GONE);
            tvDiskon.setVisibility(View.VISIBLE);
            tvDiskon.setText(tvDiskon.getText() + diskon + "%");
            tvHargaLama.setText("Rp. " + getMoney(String.valueOf(hargaLama)));
            tvHargaBaru.setText("Rp. " + getMoney(String.valueOf(hargaBaru)));
            tvHargaLama.setPaintFlags(tvHargaLama.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        tvDeskripsiProduk.setText(deskripsiProduk);
        tvNamaProduk.setText(namaProduk);

        Date date = new Date();
        date.setTime(Long.valueOf(time));
        String formattedDate = new SimpleDateFormat("dd MMM yyyy").format(date);

        long durasi = TimeUnit.DAYS.toMillis(Integer.parseInt(durasiPromo));
        Date date2 = new Date();
        date2.setTime(Long.parseLong(time) + durasi);
        formattedDate = formattedDate + " - " + new SimpleDateFormat("dd MMM yyyy").format(date2);

        long dif = (Long.parseLong(time) + durasi) - System.currentTimeMillis();
        long hari = (TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS));

        tvDurasiPromo.setText(formattedDate);
        tvJenisPromo.setText(jenisPromo);
        tvKategoriPromo.setText(kategori);

        long diffSec = dif / 1000;
        long days = diffSec / SECONDS_IN_A_DAY;
        long secondsDay = diffSec % SECONDS_IN_A_DAY;
        long seconds = secondsDay % 60;
        long minutes = (secondsDay / 60) % 60;
        long hours = (secondsDay / 3600);

        if (dif <= 0) {
            tvPromoBerakhir.setText("-");
        } else {
            tvPromoBerakhir.setText(days + " hari, " + hours + " jam");
        }
        tvNamaToko.setText(namaToko);
        tvKec.setText(lokasi);

        Date date3 = new Date();
        date3.setTime(Long.valueOf(updateTime));
        updatePromo = new SimpleDateFormat("dd MMM yyyy").format(date3);
        tvUpdate.setText(updatePromo);

        if (jenisPromo.contains("Gratis Produk")) {
            if (jenisPromo.contains("Gratis Produk Sama")) {
                imgUrl4 = imgUrl;
            }
            llImgBonus.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imgUrl4)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            gambar4_load.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            gambar4_load.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(gambar4);
        } else {
            llImgBonus.setVisibility(View.GONE);
        }

        MobileAds.initialize(this, getString(R.string.admob_app_id));

        final LinearLayout adContainer = (LinearLayout)findViewById(R.id.llad);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.ad_unit_id));

        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                adContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                adContainer.setVisibility(View.VISIBLE);
                super.onAdLoaded();
            }
        });

        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice(getString(R.string.test_id));
        adContainer.addView(adView);
        adView.loadAd(adRequestBuilder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_produk_detil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(relProduk, scrollY / 600);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    public void telp(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+tvTelepon.getText()));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    public void bonusFul(View view) {
        Intent intent = new Intent(this,ImgFullscreenActivity.class);
        intent.putExtra("imgURL",imgUrl4);
        startActivity(intent);
    }

    public void klikKategori(View view) {
        Intent semua = new Intent(this, KategoriActivity.class);
        semua.putExtra("query",tvKategoriPromo.getText());
        semua.putExtra("id","kategori");
        startActivity(semua);
    }

    public void klikJenis(View view) {
        Intent semua = new Intent(this, KategoriActivity.class);
        semua.putExtra("query",tvJenisPromo.getText());
        semua.putExtra("id","jenisPromo");
        startActivity(semua);
    }

    public void klikNamaToko(View view) {
        Intent semua = new Intent(this, KategoriActivity.class);
        semua.putExtra("query",tvNamaToko.getText());
        semua.putExtra("id","namaToko");
        startActivity(semua);
    }

    public void klikKecamatan(View view) {
        Intent semua = new Intent(this, KategoriActivity.class);
        semua.putExtra("query",tvKec.getText());
        semua.putExtra("id","lokasiToko");
        startActivity(semua);
    }

    public void showSearch(MenuItem item) {
        Intent search = new Intent(this, SearchActivity.class);
        startActivity(search);
    }

    public void peta(View view) {
        Intent peta = new Intent(this, MapsActivity.class);
        peta.putExtra("namaToko", namaToko);
        peta.putExtra("id","detil");
        peta.putExtra("alamatToko", alamatToko);
        peta.putExtra("lokasi", latlong);
        startActivity(peta);
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            ProdukDetilActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(viewPager.getCurrentItem() == 0){
                        viewPager.setCurrentItem(1);
                    } else if(viewPager.getCurrentItem() == 1){
                        viewPager.setCurrentItem(2);
                    } else {
                        viewPager.setCurrentItem(0);
                    }

                }
            });

        }
    }

    private String getMoney(String str2){
        StringBuilder str = new StringBuilder(str2);
        int idx = str.length()-3;

        while(idx >0){
            str.insert(idx,".");
            idx = idx-3;
        }

        return str.toString();
    }

    private void getInfo(String namaToko){
        tvAlamatToko = (TextView) findViewById(R.id.tvAlamatTokoPromo);
        tvTelepon = (TextView) findViewById(R.id.tvTeleponTokoPromo);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        Query searchQuery = mDatabaseReference.orderByChild("info/namaToko").equalTo(namaToko);
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                TokoUpload infoToko = new TokoUpload();
                while((iterator.hasNext())){
                    infoToko = iterator.next().getValue(TokoUpload.class);

                }
                tvAlamatToko.setText(infoToko.getAlamatToko());
                alamatToko = infoToko.getAlamatToko();
                tvTelepon.setText(infoToko.getTelpToko());
                latlong = infoToko.getLokasiPeta();
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
}
