package cn.edu.cuc.kuroki.matsuri.utils;

/**
 * 保存默认设置信息的静态类
 * @see ConfigType
 */
class DefaultConfig {
    private final static Float mainVolume = 0.8f;
    private final static Float bgmVolume = 0.8f;
    private final static Float seVolume = 0.8f;

    /**
     * get default BGM Volume
     * @return Float
     */
    static Float getBgmVolume() {
        return bgmVolume;
    }

    /**
     * get default BGM Volume
     * @return Float
     */
    static Float getMainVolume() {
        return mainVolume;
    }

    /**
     * get default BGM Volume
     * @return Float
     */
    static Float getSeVolume() {
        return seVolume;
    }
}
