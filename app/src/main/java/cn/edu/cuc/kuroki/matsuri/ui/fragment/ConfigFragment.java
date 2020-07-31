package cn.edu.cuc.kuroki.matsuri.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import cn.edu.cuc.kuroki.matsuri.R;
import cn.edu.cuc.kuroki.matsuri.ui.view.MtrMainButton;
import cn.edu.cuc.kuroki.matsuri.utils.ConfigType;
import cn.edu.cuc.kuroki.matsuri.utils.MainUtils;

/**
 * Config Fragment
 * 设置界面，可以进行各种设置（目前只实现了 BGM 音量设置）
 *      ps: 计划还有 总音量、音效（SE）音量、CV 音量（ps：不可能有的ww）、文字播放速度、auto 模式速度（然而实际连 auto 模式都没有实现qwq）
 */
public class ConfigFragment extends Fragment {
    // views
    private ConstraintLayout temporary_constraintLayout;
    private ImageView main_imageView;
    private SeekBar bgmVolume_seekBar;
    private MtrMainButton title_button;
    private MtrMainButton back_button;

    // config info
    private float bgmVolume;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config, container, false);
        bindViews(view);

        // get current config info
        final SharedPreferences srpf = MainUtils.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        bgmVolume = srpf.getFloat(ConfigType.BGM_VOLUME.name(), 1f);
        bgmVolume_seekBar.setProgress((int)(100 * bgmVolume));

        bgmVolume_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // update BGM Volume
                bgmVolume = (float) progress / 100f;
                MainUtils.getBGMBinder().setVolume(bgmVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // save BGM Volume Info to SharedPreferences
                srpf.edit().putFloat(ConfigType.BGM_VOLUME.name(), bgmVolume).apply();
            }
        });

        title_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back to title
                // see TitleFragment config_button OnClick （逆过程）

                FragmentManager fragmentManager = MainUtils.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainFragmentContainer_constraintLayout, new TitleFragment());
                fragmentTransaction.commit();

                AlphaAnimation alphaAnimation = MainUtils.getFadeOutAnimation(1600);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        temporary_constraintLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                temporary_constraintLayout.startAnimation(alphaAnimation);
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FadeOut hide temporary_constraintLayout
                AlphaAnimation alphaAnimation = MainUtils.getFadeOutAnimation(1600);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        temporary_constraintLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                temporary_constraintLayout.startAnimation(alphaAnimation);
            }
        });

        return view;
    }

    private void bindViews(View view) {
        temporary_constraintLayout = getActivity().findViewById(R.id.temporary_constraintLayout);
        main_imageView = view.findViewById(R.id.configMain_imageView);
        title_button = view.findViewById(R.id.cfgTitle_button);
        back_button = view.findViewById(R.id.cfgBack_button);
        bgmVolume_seekBar = view.findViewById(R.id.bgmVolume_seekBar);
    }
}
