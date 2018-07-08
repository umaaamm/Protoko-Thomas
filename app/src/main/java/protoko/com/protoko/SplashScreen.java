package protoko.com.protoko;

import android.content.Intent;
import android.os.Handler;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private TextView tvJudul;
    private TextView tvDesk;
    private ViewGroup transContainer;
    private boolean visibleJudul;
    private boolean visibleDesk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        transContainer = (ViewGroup) findViewById(R.id.transContainer);
        tvJudul = (TextView)transContainer.findViewById(R.id.tvJudul);
        tvDesk = (TextView)transContainer.findViewById(R.id.tvDesk);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TransitionManager.beginDelayedTransition(transContainer);
                visibleJudul = !visibleJudul;
                visibleDesk = !visibleDesk;
                tvJudul.setVisibility(visibleJudul ? View.VISIBLE : View.GONE);
                tvDesk.setVisibility(visibleDesk ? View.VISIBLE : View.GONE);
            }
        },1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }
        },2000);

    }
}
