package cn.edu.cuc.kuroki.matsuri.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;

import cn.edu.cuc.kuroki.matsuri.R;
import cn.edu.cuc.kuroki.matsuri.ui.fragment.TitleFragment;
import cn.edu.cuc.kuroki.matsuri.utils.ConfigUtils;
import cn.edu.cuc.kuroki.matsuri.utils.MainUtils;

/**
 * Main Activity
 * Matsuri 主界面，是之后所有界面的容器，主要负责界面层次控制与对不同大小屏幕的适配。
 * 主要有三层：
 *      底层：与手机屏幕大小相同，是最大的容器
 *      mainFragmentContainer 层：中间层，按照 @config/mainRatio 适配了大小（在 Matsuri 中是 16/9 使用的 cg、立绘大小都是 1280*720），负责在 TitleFragment 和 GalFragment 之间切换
 *      temporary 层：最高层，默认时隐藏，在打开 configFragment 或 slFragment（S/L 界面，暂未实现）时显示。
 */
public class MainActivity extends AppCompatActivity {
    // global vars
    private static FragmentManager fragmentManager;

    // views
    private ConstraintLayout main_constraintLayout;
    private ConstraintLayout mainFragmentContainer_constraintLayout;
    private ConstraintLayout temporary_constraintLayout;

    // fragments
    private TitleFragment titleFragment = new TitleFragment();

    /**
     * init Data, set FullScreen, Service & Animations
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        if (Build.VERSION.SDK_INT < 19) { // lower api
            main_constraintLayout.setSystemUiVisibility(View.GONE);
        } else {
            //for new api versions.
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            main_constraintLayout.setSystemUiVisibility(uiOptions);
        }
        ConfigUtils.loadConfig(this);

        MainUtils.setMainView(this, mainFragmentContainer_constraintLayout, main_constraintLayout);
        MainUtils.setMainView(this, temporary_constraintLayout, main_constraintLayout);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mainFragmentContainer_constraintLayout, titleFragment, "TITLE");
        fragmentTransaction.commit();

        setAnims();
    }

    /**
     * 在 onStart 时控制 BGMService 播放 BGM
     */
    @Override
    protected void onStart() {
        super.onStart();

        MainUtils.getBGMBinder().start();
    }

    /**
     * 在 onPause 时控制 BGMService 暂停播放 BGM
     */
    @Override
    protected void onPause() {
        super.onPause();

        MainUtils.getBGMBinder().pause();
    }

    /**
     * bind views
     */
    private void bindViews() {
        main_constraintLayout = findViewById(R.id.mainActivity_constraintLayout);
        mainFragmentContainer_constraintLayout = findViewById(R.id.mainFragmentContainer_constraintLayout);
        temporary_constraintLayout = findViewById(R.id.temporary_constraintLayout);
    }

    /**
     * set animations
     */
    private void setAnims() {
        AlphaAnimation constraintLayout_fadeIn_animation = new AlphaAnimation(0, 1);
        constraintLayout_fadeIn_animation.setFillAfter(true);
        constraintLayout_fadeIn_animation.setDuration(2000);

        mainFragmentContainer_constraintLayout.startAnimation(constraintLayout_fadeIn_animation);
    }

    /**
     * @return FragmentManager
     * @see MainUtils
     */
    public static FragmentManager getMainFragmentManager() {
        return fragmentManager;
    }
}
