package protoko.com.protoko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Iterator;

public class DaftarTokoActivity extends AppCompatActivity {

    private static final String Database_Path = "protoko_db/toko";
    private DatabaseReference databaseReference;

    private ListView lvToko;
    ArrayAdapter<String> adapter;
    SearchView svCariToko;
    ArrayList<String> listToko = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_toko);
        lvToko = (ListView)findViewById(R.id.lvToko);
        svCariToko = (SearchView) findViewById(R.id.svCariToko);
        adapter = new ArrayAdapter<String>(this, R.layout.list_item_toko, R.id.itemToko, listToko);
        lvToko.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        Query searchQuery = databaseReference.orderByChild("info/namaToko").startAt("");
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                TokoUpload tokoUpload;
                while((iterator.hasNext())){
                    tokoUpload = iterator.next().getValue(TokoUpload.class);
                    listToko.add(tokoUpload.getNamaToko());
                    try {
                        adapter.notifyDataSetChanged();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
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

        svCariToko.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        lvToko.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent semua = new Intent(getBaseContext(), KategoriActivity.class);
                String query = ((TextView)view.findViewById(R.id.itemToko)).getText().toString();
                semua.putExtra("query", query);
                semua.putExtra("id","namaToko");
                startActivity(semua);
            }
        });
    }
}