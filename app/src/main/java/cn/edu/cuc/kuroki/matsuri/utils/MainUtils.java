package cn.edu.cuc.kuroki.matsuri.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentManager;

import cn.edu.cuc.kuroki.matsuri.R;
import cn.edu.cuc.kuroki.matsuri.service.BGMService;
import cn.edu.cuc.kuroki.matsuri.ui.MainActivity;
import cn.edu.cuc.kuroki.matsuri.ui.SplashActivity;

/**
 * 主工具类
 */
public class MainUtils {
    private static int mainWidth;
    private static int mainHeight;

    /**
     * 获取屏幕高度
     * @param context context
     * @return screen Height (px)
     */
    public static int getScreenHeight(Activity context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**
     * 获取屏幕宽度
     * @param context context
     * @return screen Width (px)
     */
    public static int getScreenWidth(Activity context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * 获取屏幕比例
     * @param context context
     * @return screen Ratio
     */
    public static double getScreenRatio(Activity context) {
        return (double) getScreenWidth(context) / getScreenHeight(context);
    }

    /**
     * 将 View 按 @config/mainRatio 的比例适配外层 constraintLayout
     * @param context context
     * @param view 要设置的 View
     * @param constraintLayout View 所在的 ConstraintLayout
     */
    public static void setMainView(Activity context, View view, ConstraintLayout constraintLayout) {
        if (mainWidth == 0) {
            double ratio = context.getResources().getFraction(R.fraction.mainRatio, 1, 1);
            if (ratio > getScreenRatio(context)) {
                mainWidth = getScreenWidth(context);
                mainHeight = (int)(getScreenWidth(context) * ratio);
            } else {
                mainWidth = (int)(getScreenHeight(context) / ratio);
                mainHeight = getScreenHeight(context);
            }
        }

        ConstraintSet c = new ConstraintSet();
        c.clone(constraintLayout);
        c.constrainHeight(view.getId(), mainHeight);
        c.constrainWidth(view.getId(), mainWidth);
        c.applyTo(constraintLayout);
    }

    /**
     * 将 view 的 background 从 fromDrawable 渐变过渡至 toDrawable
     * @param context context
     * @param fromDrawableId fromDrawableId
     * @param toDrawableId toDraawableId
     * @param view view
     * @param duration 过渡时间（ms）
     */
    public static void performBackgroundTransition(Context context, int fromDrawableId, int toDrawableId, View view, int duration) {
        TransitionDrawable imageTransitionDrawable = null;
        imageTransitionDrawable = new TransitionDrawable(
            new Drawable[]{
                context.getResources().getDrawable(fromDrawableId),
                context.getResources().getDrawable(toDrawableId)
            }
        );
        imageTransitionDrawable.setCrossFadeEnabled(true);
        view.setBackgroundDrawable(imageTransitionDrawable);
        imageTransitionDrawable.startTransition(duration);
    }

    /**
     * @return context
     */
    public static Context getContext() {
        return SplashActivity.getContext();
    }

    /**
     * @return uiThread handler
     */
    public static Handler getHandler() {
        return SplashActivity.getHandler();
    }

    /**
     * @return BGMBinder
     * @see cn.edu.cuc.kuroki.matsuri.service.BGMService.BGMBinder
     */
    public static BGMService.BGMBinder getBGMBinder () {
        return SplashActivity.getBGMBinder();
    }

    /**
     * @return FragmentManager
     */
    public static FragmentManager getFragmentManager() {
        return MainActivity.getMainFragmentManager();
    }

    /**
     * 返回一个用于淡入的动画
     * @param millis 过渡时间（ms）
     * @return AlphaAnimation
     */
    public static AlphaAnimation getFadeInAnimation(int millis) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(millis);
        return alphaAnimation;
    }

    /**
     * 返回一个用于淡出的动画
     * @param millis 过渡时间（ms）
     * @return AlphaAnimation
     */
    public static AlphaAnimation getFadeOutAnimation(int millis) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(millis);
        return alphaAnimation;
    }
}
