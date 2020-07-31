package cn.edu.cuc.kuroki.matsuri.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用于设置 matsuri app config 的静态类
 * @see ConfigType
 */
public class ConfigUtils {
    /**
     * loading all local app config or default config (if isFirstRun)
     * @param context context
     */
    public static void loadConfig(Context context) {
        SharedPreferences srpf = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        if (srpf.getBoolean("isFirstRun", true)) {
            SharedPreferences.Editor editor = srpf.edit();

            editor.putFloat(ConfigType.MAIN_VOLUME.name(), DefaultConfig.getMainVolume());
            editor.putFloat(ConfigType.BGM_VOLUME.name(), DefaultConfig.getBgmVolume());
            editor.putFloat(ConfigType.SE_VOLUME.name(), DefaultConfig.getSeVolume());

            editor.putBoolean("isFirstRun", false);
            editor.apply();
        }

        // loading all config
        MainUtils.getBGMBinder().setVolume(srpf.getFloat(ConfigType.BGM_VOLUME.name(), 0.8f));
    }

    /**
     * set app BGM Volume
     * @param context context
     */
    public static void setBGMVolume(Context context) {
        SharedPreferences srpf = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        MainUtils.getBGMBinder().setVolume(srpf.getFloat(ConfigType.BGM_VOLUME.name(), 0.8f));
    }
}
