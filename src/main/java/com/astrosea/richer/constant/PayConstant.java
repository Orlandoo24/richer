package com.astrosea.richer.constant;

public class PayConstant {

    /**
     * og 支付
     */
    public static final int OG_PAY = 0;

    /**
     * og 支付
     */
    public static final int WL_PAY = 1;

    /**
     * og 支付
     */
    public static final int PUL_PAY = 2;



    /**
     * og、wl 权益未使用或未开始支付
     */
    public static final int Unused = 0;


    /**
     * og、wl 权益正在使用或已经使用完毕
     */
    public static final int Used = 1;

    /**
     * 正在支付
     */
    public static final int Paying = 1;

    /**
     * 已支付
     */
    public static final int Paid = 2;

    /**
     * 狗🐶币已到钱包
     */
    public static final int Received = 3;

    /**
     * OG 需支付的🐶币
     */
    public static final int OG_COST_500 = 500;

    /**
     * WL 需支付的🐶币
     */
    public static final int WL_COST_800 = 800;

    /**
     * PUL 需支付的🐶币
     */
    public static final int PUB_COST_1000 = 1000;

    /**
     * mint 最小单位
     */
    public static final int MINT_1 = 1;

    /**
     * 每个用户公售可以 mint 的最多次数
     */
    public static final int PUB_MINT_10 = 10;

    /**
     * 总体的NFT数量
     */
    public static final Integer MAX_1W = 10000;

}
