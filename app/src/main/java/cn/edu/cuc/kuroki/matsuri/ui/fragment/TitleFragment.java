package cn.edu.cuc.kuroki.matsuri.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import cn.edu.cuc.kuroki.matsuri.R;
import cn.edu.cuc.kuroki.matsuri.ui.SplashActivity;
import cn.edu.cuc.kuroki.matsuri.ui.view.MtrMainButton;
import cn.edu.cuc.kuroki.matsuri.utils.MainUtils;

/**
 * Title Fragment
 * Matsuri 标题界面，提供四个按钮，分别为
 *      START 开始游戏（进入 GalFragment）
 *      CONTINUE 继续游戏（读取 Q.Load，进入 GalFragment，若没有则同开始游戏）
 *      CONFIG 设置（进入 ConfigFragment）
 *      EXIT 退出游戏
 */
public class TitleFragment extends Fragment {
    // views
    private ConstraintLayout container_constraintLayout;
    private ConstraintLayout temporary_constraintLayout;
    private ImageView main_imageView;
    private ImageView title_imageView;
    private MtrMainButton start_button;
    private MtrMainButton continue_button;
    private MtrMainButton config_button;
    private MtrMainButton exit_button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_title, container, false);
        bindViews(view);

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting animations
                AlphaAnimation alphaAnimation = MainUtils.getFadeOutAnimation(1500);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // replace to new GalFragment
                        FragmentManager fragmentManager = MainUtils.getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mainFragmentContainer_constraintLayout, new GalFragment());
                        fragmentTransaction.commit();

                        // FadeIn
                        container_constraintLayout.startAnimation(MainUtils.getFadeInAnimation(1500));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                // FadeOut
                container_constraintLayout.startAnimation(alphaAnimation);
            }
        });

        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting animations
                AlphaAnimation alphaAnimation = MainUtils.getFadeOutAnimation(1500);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // load Q.Load in SharedPreferences "save"
                        SharedPreferences srpf = MainUtils.getContext().getSharedPreferences("save", Context.MODE_PRIVATE);
                        // get game script pointer
                        int save = srpf.getInt("qs", 0);
                        save--;
                        // get BGM
                        String savedBGM = srpf.getString("qsBGM", "Destiny");
                        // get CG
                        String savedCG = srpf.getString("qsCG", "その他_血の匂いを覚えている_01");

                        // replace to GalFragment with saveData
                        FragmentManager fragmentManager = MainUtils.getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mainFragmentContainer_constraintLayout, new GalFragment(save, savedBGM, savedCG));
                        fragmentTransaction.commit();

                        // FadeIn
                        container_constraintLayout.startAnimation(MainUtils.getFadeInAnimation(1500));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                // FadeOut
                container_constraintLayout.startAnimation(alphaAnimation);
            }
        });

        config_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ConfigFragment FadeIn
                FragmentManager fragmentManager = MainUtils.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.temporary_constraintLayout, new ConfigFragment());
                fragmentTransaction.commit();

                temporary_constraintLayout.startAnimation(MainUtils.getFadeInAnimation(1600));
                temporary_constraintLayout.setVisibility(View.VISIBLE);
            }
        });

        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EXIT App Using SingleTask
                Intent intent = new Intent(MainUtils.getContext(), SplashActivity.class);
                intent.putExtra("EXIT_TAG", "SINGLETASK");
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * 在 onStart 时播放 BGM（Matsuri 为 Destiny）
     */
    @Override
    public void onStart() {
        super.onStart();

        setAnims();
        MainUtils.getBGMBinder().prepareAndStart("Destiny.mp3");
    }

    private void bindViews(View view) {
        container_constraintLayout = getActivity().findViewById(R.id.mainFragmentContainer_constraintLayout);
        temporary_constraintLayout = getActivity().findViewById(R.id.temporary_constraintLayout);
        main_imageView = view.findViewById(R.id.titleMain_imageView);
        title_imageView = view.findViewById(R.id.gameTitle_imageView);
        start_button = view.findViewById(R.id.start_button);
        continue_button = view.findViewById(R.id.continue_button);
        config_button = view.findViewById(R.id.config_button);
        exit_button = view.findViewById(R.id.exit_button);
    }

    private void setAnims() {
        final AlphaAnimation title_fadeIn_animation = new AlphaAnimation(0, 0.8f);
        title_fadeIn_animation.setFillAfter(true);
        title_fadeIn_animation.setDuration(2000);

        final AlphaAnimation startBtn_fadeIn_animation = new AlphaAnimation(0, 1);
        startBtn_fadeIn_animation.setFillAfter(true);
        startBtn_fadeIn_animation.setDuration(2000);
        final AlphaAnimation continueBtn_fadeIn_animation = new AlphaAnimation(0, 1);
        continueBtn_fadeIn_animation.setFillAfter(true);
        continueBtn_fadeIn_animation.setDuration(2000);
        final AlphaAnimation configBtn_fadeIn_animation = new AlphaAnimation(0, 1);
        configBtn_fadeIn_animation.setFillAfter(true);
        configBtn_fadeIn_animation.setDuration(2000);
        final AlphaAnimation exitBtn_fadeIn_animation = new AlphaAnimation(0, 1);
        exitBtn_fadeIn_animation.setFillAfter(true);
        exitBtn_fadeIn_animation.setDuration(2000);

        title_imageView.startAnimation(title_fadeIn_animation);
        title_imageView.setVisibility(View.VISIBLE);

        new Runnable() {
            @Override
            public void run() {
                Handler handler = MainUtils.getHandler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        start_button.startAnimation(startBtn_fadeIn_animation);
                        start_button.setVisibility(View.VISIBLE);
                    }
                }, 1000);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        continue_button.startAnimation(continueBtn_fadeIn_animation);
                        continue_button.setVisibility(View.VISIBLE);
                    }
                }, 1500);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        config_button.startAnimation(configBtn_fadeIn_animation);
                        config_button.setVisibility(View.VISIBLE);
                    }
                }, 2000);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit_button.startAnimation(exitBtn_fadeIn_animation);
                        exit_button.setVisibility(View.VISIBLE);
                    }
                }, 2500);
            }
        }.run();
    }
}
