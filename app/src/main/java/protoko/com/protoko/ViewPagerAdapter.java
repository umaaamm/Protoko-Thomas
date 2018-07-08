package protoko.com.protoko;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Arrays;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private String[] images = new String[0];
    private ImageView imageView_load;
    private boolean fullscreen = false;

    public void setImage(String url, int position){
        images = Arrays.copyOf(images, images.length+1);
        images[position] = url;
    }

    public void setFullscreen(boolean fullscreen){
        this.fullscreen = fullscreen;
    }

    private boolean getFullscreen(){
        return fullscreen;
    }

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView_load = (ImageView) view.findViewById(R.id.imageView_load);
        Glide.with(context)
                .load(images[position])
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        imageView_load.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        imageView_load.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFullscreen()) {
                    Intent intent = new Intent(context, ImgFullscreenActivity.class);
                    intent.putExtra("imgURL", images[position]);
                    context.startActivity(intent);
                }
            }
        });
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}