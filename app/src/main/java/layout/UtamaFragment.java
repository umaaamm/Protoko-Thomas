package layout;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import protoko.com.protoko.Banner;
import protoko.com.protoko.R;
import protoko.com.protoko.ViewPagerAdapter;


public class UtamaFragment extends Fragment {

    private static final String Database_Path = "protoko_db/banner";
    private DatabaseReference databaseReference;
    ViewPagerAdapter viewPagerAdapter;

    public static ViewPager viewPager;
    private LinearLayout sliderDotspanel;
    private ImageView[] dots;
    private int dotscount;

    private LinearLayout llDaftarHome;
    private LinearLayout llPb;

    public UtamaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_utama, container, false);

        viewPager = (ViewPager)rootView.findViewById(R.id.viewPager2);
        sliderDotspanel = (LinearLayout)rootView.findViewById(R.id.SliderDots);
        viewPagerAdapter = new ViewPagerAdapter(getContext());

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        databaseReference.orderByChild("link").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        Banner banner = postSnapshot.getValue(Banner.class);
                        viewPagerAdapter.setImage(banner.getLink(), i);
                        i++;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                viewPagerAdapter.setFullscreen(false);
                viewPager.setAdapter(viewPagerAdapter);
                viewPagerAdapter.notifyDataSetChanged();

                dotscount = viewPagerAdapter.getCount();
                dots = new ImageView[dotscount];

                for (int j = 0; j < dotscount; j++) {

                    dots[j] = new ImageView(getContext());
                    dots[j].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_dot_home));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    params.setMargins(8, 0, 8, 0);
                    sliderDotspanel.addView(dots[j], params);
                }

                dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot_home));

                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                        for (int i = 0; i < dotscount; i++) {
                            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_dot_home));
                        }

                        dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot_home));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        llDaftarHome = (LinearLayout)rootView.findViewById(R.id.llDaftarHome);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            llDaftarHome.setVisibility(View.GONE);
        }

        MobileAds.initialize(getContext(), getString(R.string.admob_app_id));

        LinearLayout adContainer = (LinearLayout)rootView.findViewById(R.id.batas1);
        AdView adView = new AdView(getContext());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.ad_unit_id));
        llPb = (LinearLayout)rootView.findViewById(R.id.llPb);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                llPb.setVisibility(View.VISIBLE);
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
                 llPb.setVisibility(View.GONE);
                super.onAdLoaded();
            }
        });

        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice(getString(R.string.test_id));
        adContainer.addView(adView);
        adView.loadAd(adRequestBuilder.build());

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
