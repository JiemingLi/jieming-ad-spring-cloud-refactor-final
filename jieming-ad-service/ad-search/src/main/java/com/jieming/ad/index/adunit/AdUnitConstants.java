package com.jieming.ad.index.adunit;

/**
 * Created by Qinyi.
 */
public class AdUnitConstants {

    public static class POSITION_TYPE {

        // 开屏广告
        public static final int KAIPING = 1;
        // 贴片广告，视频播放之前的广告
        public static final int TIEPIAN = 2;
        // 视频播放中所播放的广告
        public static final int TIEPIAN_MIDDLE = 4;
        // 播放视频暂停时所播放的广告
        public static final int TIEPIAN_PAUSE = 8;
        // 视频播放结束后所播放的广告
        public static final int TIEPIAN_POST = 16;
    }
}
