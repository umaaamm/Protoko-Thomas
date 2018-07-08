package protoko.com.protoko;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private ListView lvSearch;
    private TagAdapter tagAdapter;
    private SearchView searchView;
    private boolean searchReady = false;

    private DatabaseReference databaseRef;
    private static final String Database_Path = "protoko_db/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        this.setTitle("Cari Promo");
        searchReady = false;
        lvSearch = (ListView) findViewById(R.id.lvSearch);

        databaseRef = FirebaseDatabase.getInstance().getReference(Database_Path);
        databaseRef.child("tag").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Updated");
                final ArrayList<Tag> tagList = new ArrayList<>();

                for(DataSnapshot tagSnapshot : dataSnapshot.getChildren()){
                    if(dataSnapshot.getChildrenCount()>0){
                        searchReady = true;
                    }
                    String tag = tagSnapshot.getKey();
                    tagList.add(new Tag(tag));
                }
                if(searchReady) {
                    tagAdapter = new TagAdapter(tagList, SearchActivity.this);
                    lvSearch.setAdapter(tagAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private class TagAdapter extends BaseAdapter {

        class ViewHolder {
            TextView txtTag;
        }

        List<Tag> tagList;
        public Context context;
        ArrayList<Tag> arraylist;

        private TagAdapter(List<Tag> apps, Context context) {
            this.tagList = apps;
            this.context = context;
            arraylist = new ArrayList<>();
            arraylist.addAll(tagList);
        }

        @Override
        public int getCount()
        {
            return tagList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = convertView;
            final ViewHolder viewHolder;

            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.item_tag, null);
                viewHolder = new ViewHolder();
                viewHolder.txtTag = (TextView) rowView.findViewById(R.id.txtTag);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.txtTag.setText(tagList.get(position).getTag() + "");

            viewHolder.txtTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchView.setQuery(viewHolder.txtTag.getText(), false);
                }
            });

            return rowView;
        }

        int countSpace(String text){
            return text.length()-text.replaceAll(" ","").length();
        }

        void filter(String charText) {
            int i = 0;
            charText = charText.toLowerCase(Locale.getDefault());
            tagList.clear();
            if (charText.length() == 0) {
                for (Tag postDetail : arraylist) {
                    if(i<8) {
                        if (countSpace(postDetail.getTag())==1) {
                            tagList.add(postDetail);
                            i++;
                        }
                    }else{
                        break;
                    }
                }
            } else {
                for (Tag postDetail : arraylist) {
                    if(i<8) {
                        if (charText.length() != 0 && postDetail.getTag().toLowerCase(Locale.getDefault()).startsWith(charText) && (countSpace(postDetail.getTag()) <= countSpace(charText)+2)) {
                            tagList.add(postDetail);
                            i++;
                        }
                    }else{
                        break;
                    }
                }
            }
            notifyDataSetChanged();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchItem.expandActionView();
        searchView.setQueryHint("Cari Promo");

        final Intent i = new Intent(this, SearchResultNewActivity.class);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                i.putExtra("query", query);
                startActivity(i);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                lvSearch.setVisibility(View.VISIBLE);
                if(searchReady) {
                    tagAdapter.filter(searchQuery.trim());
                    lvSearch.invalidate();
                }
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        return true;
    }
}
