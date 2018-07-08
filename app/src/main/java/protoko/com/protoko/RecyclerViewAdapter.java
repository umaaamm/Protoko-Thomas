package protoko.com.protoko;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import static protoko.com.protoko.AllProductResultActivity.gpsLong;
import static protoko.com.protoko.AllProductResultActivity.gpsLat;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<PromoUpload> MainPromoUploadList;
    private int diskon;
    private String lokasi = "-";

    public RecyclerViewAdapter(Context context, List<PromoUpload> TempList) {

        this.MainPromoUploadList = TempList;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);

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

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PromoUpload uploadInfo = MainPromoUploadList.get(position);

        String latlong = uploadInfo.getLatlong();
        if (!latlong.isEmpty()) {
            String[] lok = latlong.split(",");
            Double bLong = Double.parseDouble(lok[1]);
            Double bLat = Double.parseDouble(lok[0]);
            DecimalFormat kl = new DecimalFormat("#.#");
            kl.setRoundingMode(RoundingMode.CEILING);
            Double kilo = haversine(bLat, bLong, gpsLat, gpsLong);
            int meter = (int)(kilo * 1000);
            if (meter > 100) {
                lokasi = String.valueOf(kl.format(kilo)) + " km";
            } else {
                lokasi = String.valueOf(meter) + " m";
            }
            uploadInfo.setMeter(meter);
        }
        holder.imgJarak.setBackgroundResource(R.drawable.ic_gps_on);
        holder.txtJarak.setText(lokasi);

        holder.txtjudulPromo.setText(uploadInfo.getJudulPromo());
        holder.txtNamaToko.setText(uploadInfo.getNamaToko());
        holder.txtLokasi.setText(uploadInfo.getLokasiToko());
        if (uploadInfo.getJenisPromo().contains("Gratis Produk")){
            holder.txtHargaProduk.setVisibility(View.VISIBLE);
            holder.txtHargaProduk.setTextColor(Color.parseColor("#900c3f"));
            holder.txtHargaProduk.setBackgroundResource(android.R.color.transparent);
            holder.txtHargaProduk.setText("Rp. "+ getMoney(String.valueOf(uploadInfo.getHargaLama())));
            holder.txtHargaLama.setText("Beli : " + String.valueOf(uploadInfo.getJumlahBeli()));
            if (uploadInfo.getJenisPromo().contains("Gratis Produk Lain")) {
                holder.txtHargaBaru.setText("Gratis Produk Lain : " + String.valueOf(uploadInfo.getJumlahGratis()));
            }else{
                holder.txtHargaBaru.setText("Gratis Produk Sama : " + String.valueOf(uploadInfo.getJumlahGratis()));
            }
            holder.txtHargaLama.setPaintFlags(0);
        }else{
            holder.txtHargaProduk.setTextColor(Color.parseColor("#FFFFFF"));
            diskon = (int)(((float)(uploadInfo.getHargaLama() - uploadInfo.getHargaBaru())/(float) uploadInfo.getHargaLama())*100.0);
            holder.txtHargaProduk.setText(" Diskon "+diskon+"% ");
            holder.txtHargaProduk.setBackgroundResource(R.drawable.diskon);
            holder.txtHargaProduk.setVisibility(View.VISIBLE);
            holder.txtHargaLama.setText("Rp. "+ getMoney(String.valueOf(uploadInfo.getHargaLama())));
            holder.txtHargaBaru.setText("Rp. "+ getMoney(String.valueOf(uploadInfo.getHargaBaru())));
            holder.txtHargaLama.setPaintFlags(holder.txtHargaLama.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        Glide.with(context).load(uploadInfo.getImageURL())
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

        public ImageView imageView, imgLoad, imgJarak;
        public TextView txtjudulPromo, txtHargaBaru, txtHargaLama, txtNamaToko, txtHargaProduk, txtLokasi, txtJarak;
        public RelativeLayout rlItem;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imgJarak = (ImageView) itemView.findViewById(R.id.jarak);
            imgLoad = (ImageView) itemView.findViewById(R.id.imgLoad);
            txtjudulPromo = (TextView) itemView.findViewById(R.id.txtJudulPromo);
            txtHargaLama = (TextView) itemView.findViewById(R.id.txtHargaLama);
            txtHargaBaru = (TextView) itemView.findViewById(R.id.txtHargaBaru);
            txtHargaProduk= (TextView) itemView.findViewById(R.id.txtHargaProduk);
            txtNamaToko = (TextView) itemView.findViewById(R.id.txtNamaToko);
            txtLokasi = (TextView) itemView.findViewById(R.id.txtLokasi);
            txtJarak = (TextView) itemView.findViewById(R.id.txtJarak);
            rlItem = (RelativeLayout)itemView.findViewById(R.id.rlItem);

            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    passData(getAdapterPosition());
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
        i.putExtra("time",  MainPromoUploadList.get(position).getTimeStamp());
        i.putExtra("updateTime",  MainPromoUploadList.get(position).getzUpdateTime());
        int diskon2 = (int)(((float)(MainPromoUploadList.get(position).getHargaLama() - MainPromoUploadList.get(position).getHargaBaru())/(float)MainPromoUploadList.get(position).getHargaLama())*100.0);
        i.putExtra("diskon",  diskon2);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2){
        final double R = 6378.16;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon /2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c ;
    }
}
