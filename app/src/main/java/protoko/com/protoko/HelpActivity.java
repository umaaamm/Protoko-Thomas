package protoko.com.protoko;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HelpActivity extends AppCompatActivity {

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tentang);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                return false;
            }
        });

        MobileAds.initialize(this, getString(R.string.admob_app_id));

        LinearLayout adContainer = (LinearLayout)findViewById(R.id.adContainer);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.ad_unit_id));
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice(getString(R.string.test_id));
        adContainer.addView(adView);
        adView.loadAd(adRequestBuilder.build());
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add("Apa itu ProToko?");
        listDataHeader.add("Mengapa menggunakan ProToko?");
        listDataHeader.add("Bagaimana cara melihat promo-promo produk di ProToko ?");
        listDataHeader.add("Bagaimana cara memesan produk di ProToko?");
        listDataHeader.add("Apakah ProToko menerima layanan pesan antar?");
        listDataHeader.add("Apakah ada diskon khusus di ProToko?");
        listDataHeader.add("Bagaimana cara mendaftar jadi mitra di ProToko?");

        List<String> p1 = new ArrayList<String>();
        p1.add("ProToko adalah aplikasi yang menyediakan berbagai promo Toko di sekitar kita.");

        List<String> p2 = new ArrayList<String>();
        p2.add("Karena menggunakan ProToko dapat memudahkan kita dalam memperoleh informasi promo Toko terpercaya di sekitar kita dengan cepat.");

        List<String> p3 = new ArrayList<String>();
        p3.add("Pilih salah satu kategori barang promo di menu Beranda, kamu dapat melihat berbagai promo dari Toko-Toko mitra kami");

        List<String> p4 = new ArrayList<String>();
        p4.add("Maaf, saat ini ProToko belum menyediakan fitur pemesanan barang.");

        List<String> p5 = new ArrayList<String>();
        p5.add("Maaf, saat ini ProToko belum menyediakan fitur pesan antar.");

        List<String> p6 = new ArrayList<String>();
        p6.add("Setiap promo yang ada di ProToko sesuai dengan ketentuan dari masing-masing Toko.");

        List<String> p7 = new ArrayList<String>();
        p7.add("Untuk mendaftar menjadi mitra di ProToko, kamu dapat memilih menu Masuk sebagai Toko, kemudian " +
                "pilih tombol Daftar Akun. Ikuti setiap instruksi yang tertera. Kalau sudah, tunggu tim kami untuk memverifikasi data kamu.");

        listDataChild.put(listDataHeader.get(0), p1);
        listDataChild.put(listDataHeader.get(1), p2);
        listDataChild.put(listDataHeader.get(2), p3);
        listDataChild.put(listDataHeader.get(3), p4);
        listDataChild.put(listDataHeader.get(4), p5);
        listDataChild.put(listDataHeader.get(5), p6);
        listDataChild.put(listDataHeader.get(6), p7);
    }
}