package protoko.com.protoko;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RecyclerViewKatalogAdapter extends RecyclerView.Adapter<RecyclerViewKatalogAdapter.ViewHolder> {

    private DatabaseReference databaseReference;

    private final String Database_Path = "protoko_db/produk";
    public static int SECONDS_IN_A_DAY = 24 * 60 * 60;

    private Context context;
    private List<PromoUpload> MainPromoUploadList;
    private int diskon;

    public RecyclerViewKatalogAdapter(Context context, List<PromoUpload> TempList) {

        this.MainPromoUploadList = TempList;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items_katalog, parent, false);

        return new ViewHolder(view);
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

    private PromoUpload UploadInfo;

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        UploadInfo = MainPromoUploadList.get(position);

        long durasi = TimeUnit.DAYS.toMillis(Integer.parseInt(UploadInfo.getDurasi()));
        String time = UploadInfo.getTimeStamp();
        long dif = (Long.parseLong(time) + durasi) - System.currentTimeMillis();
        long diffSec = dif / 1000;
        long days = diffSec / SECONDS_IN_A_DAY;
        long secondsDay = diffSec % SECONDS_IN_A_DAY;
        long hours = (secondsDay / 3600);
        if(dif<=0){
            holder.txtDurasiPromo.setText("Promo berakhir");
        }else{
            holder.txtDurasiPromo.setText(days+" hari, "+hours+" jam");
        }
        holder.txtjudulPromo.setText(UploadInfo.getJudulPromo());
        holder.txtNamaToko.setText(UploadInfo.getNamaToko());
        holder.txtLokasi.setText(UploadInfo.getLokasiToko());
        if (UploadInfo.getJenisPromo().contains("Gratis Produk")){
            holder.txtHargaProduk.setBackgroundResource(android.R.color.transparent);
            holder.txtHargaLama.setPaintFlags(0);
            holder.txtHargaProduk.setText("Rp. "+ getMoney(String.valueOf(UploadInfo.getHargaBaru())));
            holder.txtHargaLama.setText("Beli : " + String.valueOf(UploadInfo.getJumlahBeli()));
            if (UploadInfo.getJenisPromo().contains("Gratis Produk Lain")) {
                holder.txtHargaBaru.setText("Gratis Produk Lain : " + String.valueOf(UploadInfo.getJumlahGratis()));
            }else{
                holder.txtHargaBaru.setText("Gratis Produk Sama: " + String.valueOf(UploadInfo.getJumlahGratis()));
            }
        }else{
            holder.txtHargaProduk.setTextColor(Color.parseColor("#FFFFFF"));
            holder.txtHargaProduk.setBackgroundResource(R.drawable.diskon);
            holder.txtHargaLama.setPaintFlags(holder.txtHargaLama.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            diskon = (int)(((float)(UploadInfo.getHargaLama() - UploadInfo.getHargaBaru())/(float)UploadInfo.getHargaLama())*100.0);
            holder.txtHargaProduk.setText(" Diskon "+diskon+"% ");
            holder.txtHargaLama.setText("Rp. "+ getMoney(String.valueOf(UploadInfo.getHargaLama())));
            holder.txtHargaBaru.setText("Rp. "+ getMoney(String.valueOf(UploadInfo.getHargaBaru())));
        }

        Glide.with(context).load(UploadInfo.getImageURL())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        holder.imgLoad.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.imgLoad.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {

        return MainPromoUploadList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView, imgLoad;
        public TextView txtjudulPromo, txtHargaBaru, txtHargaLama, txtNamaToko, txtHargaProduk, txtLokasi, txtDurasiPromo;
        public Button btUbah, btHapus;

        public ViewHolder(final View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imgLoad = (ImageView) itemView.findViewById(R.id.imgLoad);
            txtjudulPromo = (TextView) itemView.findViewById(R.id.txtJudulPromo);
            txtHargaLama = (TextView) itemView.findViewById(R.id.txtHargaLama);
            txtHargaBaru = (TextView) itemView.findViewById(R.id.txtHargaBaru);
            txtHargaProduk= (TextView) itemView.findViewById(R.id.txtHargaProduk);
            txtNamaToko = (TextView) itemView.findViewById(R.id.txtNamaToko);
            txtLokasi = (TextView) itemView.findViewById(R.id.txtLokasi);
            txtDurasiPromo = (TextView)itemView.findViewById(R.id.txtDurasiPromo);
            btUbah = (Button)itemView.findViewById(R.id.btUbahPromo);
            btHapus = (Button)itemView.findViewById(R.id.btHapusPromo);

            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    passData(getAdapterPosition());
                }
            });

            btUbah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    passData_2(getAdapterPosition());
                }
            });

            btHapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context = itemView.getContext();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                    alertDialogBuilder
                            .setMessage("Apakah Anda yakin ingin menghapus promo ini? ")
                            .setCancelable(true)
                            .setPositiveButton("Hapus",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    String zId = MainPromoUploadList.get(getAdapterPosition()).getzID();
                                    databaseReference.child(zId).removeValue();
                                    removeTag(MainPromoUploadList.get(getAdapterPosition()).getJudulPromo(), MainPromoUploadList.get(getAdapterPosition()).getzID());
                                    removeFile(MainPromoUploadList.get(getAdapterPosition()).getImageURL());
                                    removeFile(MainPromoUploadList.get(getAdapterPosition()).getImageURL2());
                                    removeFile(MainPromoUploadList.get(getAdapterPosition()).getImageURL3());
                                    removeFile(MainPromoUploadList.get(getAdapterPosition()).getImageURL4());
                                    MainPromoUploadList.remove(getAdapterPosition());
                                    KatalogActivity.adapter.notifyItemRemoved(getAdapterPosition());
                                    KatalogActivity.adapter.notifyItemRangeChanged(getAdapterPosition(), getItemCount());
                                    Snackbar.make(KatalogActivity.svKatalog, "Promo berhasil dihapus.", Snackbar.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Batal",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.dismiss();
                                }
                            });
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    TextView pesan = (TextView) alertDialog.findViewById(android.R.id.message);
                    pesan.setTextSize(15);

                    Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    b.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

                    Button c = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    c.setTextColor(ContextCompat.getColor(context, R.color.colorHitam));
                }
            });
        }
    }

    private void passData(int position){
        Intent i = new Intent(context, ProdukDetilActivity.class);
        i.putExtra("namaProduk", MainPromoUploadList.get(position).getJudulPromo());
        i.putExtra("deskripsiProduk", MainPromoUploadList.get(position).getDeskripsi());
        i.putExtra("durasiPromo", MainPromoUploadList.get(position).getDurasi());
        i.putExtra("hargaBaru", String.valueOf(MainPromoUploadList.get(position).getHargaBaru()));
        i.putExtra("hargaLama", String.valueOf(MainPromoUploadList.get(position).getHargaLama()));
        i.putExtra("imgUrl", MainPromoUploadList.get(position).getImageURL());
        i.putExtra("imgUrl2", MainPromoUploadList.get(position).getImageURL2());
        i.putExtra("imgUrl3", MainPromoUploadList.get(position).getImageURL3());
        i.putExtra("imgUrl4", MainPromoUploadList.get(position).getImageURL4());
        i.putExtra("jenisPromo", MainPromoUploadList.get(position).getJenisPromo());
        i.putExtra("jumlahBeli", String.valueOf(MainPromoUploadList.get(position).getJumlahBeli()));
        i.putExtra("jumlahGratis", String.valueOf(MainPromoUploadList.get(position).getJumlahGratis()));
        i.putExtra("kategori", MainPromoUploadList.get(position).getKategori());
        i.putExtra("lokasi", MainPromoUploadList.get(position).getLokasiToko());
        i.putExtra("namaToko", MainPromoUploadList.get(position).getNamaToko());
        i.putExtra("time", MainPromoUploadList.get(position).getTimeStamp());
        i.putExtra("updateTime",  MainPromoUploadList.get(position).getzUpdateTime());
        int diskon2 = (int)(((float)(MainPromoUploadList.get(position).getHargaLama() - MainPromoUploadList.get(position).getHargaBaru())/(float)MainPromoUploadList.get(position).getHargaLama())*100.0);
        i.putExtra("diskon",  diskon2);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private void passData_2(int position){
        Intent i = new Intent(context, UbahPromoActivity.class);
        i.putExtra("namaProduk", MainPromoUploadList.get(position).getJudulPromo());
        i.putExtra("deskripsiProduk", MainPromoUploadList.get(position).getDeskripsi());
        i.putExtra("durasiPromo", MainPromoUploadList.get(position).getDurasi());
        i.putExtra("hargaBaru", String.valueOf(MainPromoUploadList.get(position).getHargaBaru()));
        i.putExtra("hargaLama", String.valueOf(MainPromoUploadList.get(position).getHargaLama()));
        i.putExtra("imgUrl", MainPromoUploadList.get(position).getImageURL());
        i.putExtra("imgUrl2", MainPromoUploadList.get(position).getImageURL2());
        i.putExtra("imgUrl3", MainPromoUploadList.get(position).getImageURL3());
        i.putExtra("imgUrl4", MainPromoUploadList.get(position).getImageURL4());
        i.putExtra("jenisPromo", MainPromoUploadList.get(position).getJenisPromo());
        i.putExtra("jumlahBeli", String.valueOf(MainPromoUploadList.get(position).getJumlahBeli()));
        i.putExtra("jumlahGratis", String.valueOf(MainPromoUploadList.get(position).getJumlahGratis()));
        i.putExtra("kategori", MainPromoUploadList.get(position).getKategori());
        i.putExtra("id",  MainPromoUploadList.get(position).getId());
        i.putExtra("zID", MainPromoUploadList.get(position).getzID());
        i.putExtra("time", MainPromoUploadList.get(position).getTimeStamp());
        i.putExtra("pos", position);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private void removeFile(String url){
        if(!url.contains("null")) {
            System.out.println(url);
            String urlStr = url.split("img%2F")[1];
            System.out.println(urlStr);
            String urlStr2 = urlStr.split("\\?")[0];
            System.out.println(urlStr2);
            StorageReference fileDelete = FirebaseStorage.getInstance().getReference().child("img/" + urlStr2);
            fileDelete.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
    }

    private void removeTag(String judul, String PromoUploadId){
        Pattern letter = Pattern.compile("[a-z]");
        Matcher hasLetter;
        DatabaseReference databasetagref;
        String Database_Path_Tag = "protoko_db/tag";
        databasetagref = FirebaseDatabase.getInstance().getReference(Database_Path_Tag);
        String[] tag_arr = judul.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", " ").split(" ");
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
                    databasetagref.child(tagUpload).child(PromoUploadId).removeValue();
                    tagUpload = "";
                }
            }
            tagUpload = "";
        }
    }
}
