package cn.edu.cuc.kuroki.matsuri.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import cn.edu.cuc.kuroki.matsuri.R;
import cn.edu.cuc.kuroki.matsuri.service.BGMService;
import cn.edu.cuc.kuroki.matsuri.utils.ConfigUtils;

/**
 * Splash Activity
 * Play start animation & Prepare Service and config data
 */
public class SplashActivity extends AppCompatActivity {
    // global vars
    private static Context context;
    private static Handler handler;
    private static BGMService.BGMBinder  bgmBinder;

    // service
    ServiceConnection bgmServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bgmBinder = (BGMService.BGMBinder) service;
            // TODO: 保证 onServiceConnected 已经回调才能进入 MainActivity
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    // anims
    private AlphaAnimation fadeIn_animation = new AlphaAnimation(0, 1);
    private AlphaAnimation fadeOut_animation = new AlphaAnimation(1, 0);

    // views
    private ImageView main_imageView;

    /**
     * init Data, set FullScreen, Service & Animations
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bindViews();
        if (Build.VERSION.SDK_INT < 19) { // lower api
            main_imageView.setSystemUiVisibility(View.GONE);
        } else {
            //for new api versions.
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            main_imageView.setSystemUiVisibility(uiOptions);
        }

        context = getApplicationContext();
        handler = new Handler();

        setAnims();

        setServices();
    }

    /**
     * 用于退出 app。
     * 在 Matsuri 中，只有在按 Exit Button 退出 App 时才会通过一个带有 "EXIT_TAG" 的 intent 回到该 Activity
     * @param intent intent with "EXIT_TAG"
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String tag = intent.getStringExtra("EXIT_TAG");
        if (tag != null&& !TextUtils.isEmpty(tag)) {
            if ("SINGLETASK".equals(tag)) {//退出程序
                finish();
            }
        }
    }

    /**
     * bind views
     */
    private void bindViews() {
        main_imageView = findViewById(R.id.splashMain_imageView);
    }

    /**
     * set animations
     */
    private void setAnims() {
        fadeIn_animation.setFillAfter(true);
        fadeIn_animation.setDuration(2000);
        fadeOut_animation.setFillAfter(true);
        fadeOut_animation.setDuration(2000);

        fadeIn_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Runnable() {
                    @Override
                    public void run() {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                main_imageView.startAnimation(fadeOut_animation);
                            }
                        }, 1000);
                    }
                }.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        fadeOut_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        main_imageView.startAnimation(fadeIn_animation);
    }

    /**
     * set Services
     */
    private void setServices() {
        Intent bgmServiceIntent = new Intent(SplashActivity.this, BGMService.class);
        bindService(bgmServiceIntent, bgmServiceConnection, BIND_AUTO_CREATE);
    }

    /**
     * @return context
     * @see cn.edu.cuc.kuroki.matsuri.utils.MainUtils
     */
    public static Context getContext() {
        return context;
    }

    /**
     * @return UI Thread handler
     * @see cn.edu.cuc.kuroki.matsuri.utils.MainUtils
     */
    public static Handler getHandler() {
        return handler;
    }

    /**
     * @return BGMBinder
     * @see cn.edu.cuc.kuroki.matsuri.service.BGMService.BGMBinder
     * @see cn.edu.cuc.kuroki.matsuri.utils.MainUtils
     */
    public static BGMService.BGMBinder getBGMBinder() {
        return bgmBinder;
    }
}
