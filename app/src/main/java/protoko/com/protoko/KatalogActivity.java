package protoko.com.protoko;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class KatalogActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private DatabaseReference mDatabaseReference;

    public static RelativeLayout svKatalog;

    private RecyclerView recyclerView;

    private ProgressBar pb;

    public static RecyclerView.Adapter adapter ;

    private List<PromoUpload> list = new ArrayList<>();

    private static final String Database_Path = "protoko_db/produk/";
    private static final String Database_Path2 = "protoko_db/toko";

    private String userName = "";
    private Query query;
    private String namaToko = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        pb = (ProgressBar)findViewById(R.id.pb);

        svKatalog = (RelativeLayout)findViewById(R.id.svKatalog);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(KatalogActivity.this, 2));

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            userName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }else{
            userName = "";
        }

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path2);
        query = mDatabaseReference.child(userName+"/info");
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                namaToko = snapshot.child("namaToko").getValue(String.class);
                findToko(namaToko);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void findToko(String name){
        Query katalogQuery = databaseReference.orderByChild("namaToko").equalTo(name);
        katalogQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                int i = 0;
                String[] produk = new String[21];
                while((iterator.hasNext())){
                    produk[i] = iterator.next().getValue().toString();
                    i++;
                }
                PromoUpload promoUpload = new PromoUpload();
                promoUpload.setDeskripsi(produk[0]);
                promoUpload.setDurasi(produk[1]);
                promoUpload.setHargaBaru(Integer.valueOf(produk[2]));
                promoUpload.setHargaLama(Integer.valueOf(produk[3]));
                promoUpload.setId(produk[4]);
                promoUpload.setImageURL(produk[5]);
                promoUpload.setImageURL2(produk[6]);
                promoUpload.setImageURL3(produk[7]);
                promoUpload.setImageURL4(produk[8]);
                promoUpload.setJenisPromo(produk[9]);
                promoUpload.setJudulPromo(produk[10]);
                promoUpload.setJumlahBeli(Integer.valueOf(produk[11]));
                promoUpload.setJumlahGratis(Integer.valueOf(produk[12]));
                promoUpload.setKategori(produk[13]);
                promoUpload.setLatlong(produk[14]);
                promoUpload.setLokasiToko(produk[15]);
                promoUpload.setMeter(Integer.valueOf(produk[16]));
                promoUpload.setNamaToko(produk[17]);
                promoUpload.setTimeStamp(produk[18]);
                promoUpload.setzID(produk[19]);
                promoUpload.setzUpdateTime(produk[20]);
                Collections.reverse(list);
                list.add(promoUpload);
                Collections.reverse(list);
                adapter = new RecyclerViewKatalogAdapter(getApplicationContext(), list);
                try {
                    recyclerView.setAdapter(adapter);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                pb.setVisibility(View.GONE);
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