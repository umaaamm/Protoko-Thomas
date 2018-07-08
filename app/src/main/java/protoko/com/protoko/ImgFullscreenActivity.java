package protoko.com.protoko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ImgFullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_fullscreen);

        final ImageView imgFullScreen = (ImageView)findViewById(R.id.imgFullScreen);
        final ImageView imgFullScreen_load = (ImageView)findViewById(R.id.imgFullScreen_load);
        Intent i = getIntent();
        Bundle bd = i.getExtras();
        String url = (String) bd.get("imgURL");
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        imgFullScreen_load.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        imgFullScreen_load.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imgFullScreen);
    }
}