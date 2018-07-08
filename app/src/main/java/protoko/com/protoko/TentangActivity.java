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

public class TentangActivity extends AppCompatActivity {

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
        listDataHeader.add("Versi");
        listDataHeader.add("Pengembang");
        listDataHeader.add("Hubungi Kami");
        List<String> versi = new ArrayList<String>();
        versi.add("v1.0");

        List<String> developer = new ArrayList<String>();
        developer.add("Muhammad Thomas Fadhila Yahya\n\nM. Arief Hidayatullah\n\nSeno Aji Prasesya\n\nDwi Sigit Pramono\n\nRahmat Syaifullah\n\nOkta Febrianto");

        List<String> contact = new ArrayList<String>();
        contact.add("Email : protoko2017@gmail.com");

        listDataChild.put(listDataHeader.get(0), versi);
        listDataChild.put(listDataHeader.get(1), developer);
        listDataChild.put(listDataHeader.get(2), contact);
    }
}
