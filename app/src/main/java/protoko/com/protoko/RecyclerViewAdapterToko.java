package protoko.com.protoko;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.apache.commons.lang3.text.WordUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import static protoko.com.protoko.NearestTokoActivity.gpsLat;
import static protoko.com.protoko.NearestTokoActivity.gpsLong;


public class RecyclerViewAdapterToko extends RecyclerView.Adapter<RecyclerViewAdapterToko.ViewHolder> {

    private Context context;
    private List<TokoUpload> TokoList;
    private String lokasi = "-";

    public RecyclerViewAdapterToko(Context context, List<TokoUpload> TempList) {

        this.TokoList = TempList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items_toko, parent, false);

        return new ViewHolder(view);
    }

    
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TokoUpload uploadInfo = TokoList.get(position);

        String latlong = uploadInfo.getLokasiPeta();
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
        holder.txtJarak.setText(lokasi);

        String namaToko = uploadInfo.getNamaToko();
        namaToko = WordUtils.capitalizeFully(namaToko);
        holder.txtNamaToko.setText(namaToko);
        holder.txtNamaTokoSearch.setText(uploadInfo.getNamaToko());
        holder.txtLokasi.setText(uploadInfo.getKec());
        String alamat = uploadInfo.getAlamatToko();
        alamat = WordUtils.capitalizeFully(alamat);
        holder.txtAlamat.setText(alamat);

        Glide.with(context).load(uploadInfo.getFotoURL())
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

        return TokoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView, imgLoad;
        public TextView txtNamaToko, txtAlamat, txtLokasi, txtJarak, txtNamaTokoSearch;
        public RelativeLayout rlItem;
        public Button btLihat;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imgLoad = (ImageView) itemView.findViewById(R.id.imgLoad);
            txtNamaToko = (TextView) itemView.findViewById(R.id.txtNamaToko);
            txtNamaTokoSearch = (TextView) itemView.findViewById(R.id.txtNamaTokoSearch);
            txtAlamat = (TextView) itemView.findViewById(R.id.txtAlamat);
            txtLokasi = (TextView) itemView.findViewById(R.id.txtLokasi);
            txtJarak = (TextView) itemView.findViewById(R.id.txtJarak);
            rlItem = (RelativeLayout)itemView.findViewById(R.id.rlItem);
            btLihat = (Button)itemView.findViewById(R.id.btKatalog);

            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                }
            });

            btLihat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent semua = new Intent(context, KategoriActivity.class);
                    semua.putExtra("query",txtNamaTokoSearch.getText());
                    semua.putExtra("id","namaToko");
                    semua.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(semua);
                }
            });
        }
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
