package xavireig.com.pacecalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public class Splash extends Activity {

    private final int DURATION = 5000;
    private Thread mSplashThread;
    private final String TAPPX_KEY = "/120940746/Pub-2167-Android-6569";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        com.tappx.TAPPXAdInterstitial.ConfigureAndShow(this, TAPPX_KEY);

        mSplashThread = new Thread() {

            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(DURATION);
                    } catch (InterruptedException e) {
                    } finally {
                        finish();
                        Intent intent = new Intent(getBaseContext(),
                                main.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.animation_splash, R.anim.animation_splash2);

                    }
                }
            }
        };
        mSplashThread.start();
    }
}
