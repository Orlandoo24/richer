package com.astrosea.richer.constant;

import java.math.BigDecimal;

public class PayConstant {

    /**
     * og 身份支付
     */
    public static final int OG_PAY = 0;

    /**
     * WL 身份支付
     */
    public static final int WL_PAY = 1;

    /**
     * PUL 身份支付
     */
    public static final int PUL_PAY = 2;



    /**
     * og、wl 权益未使用或未开始支付
     */
    public static final int RIGHT_UNUSED_0 = 0;


    /**
     * og、wl 权益正在使用或已经使用完毕
     */
    public static final int RIGHT_USED_1 = 1;

    /**
     * og、wl 能 mint 的次数
     */
    public static final int VIP_AMT_1 = 1;


    /**
     * 正在支付
     */
    public static final int Paying_1 = 1;

    /**
     * 已支付
     */
    public static final int Paid_2 = 2;

    /**
     * 狗🐶币已到钱包
     */
    public static final int Received_3 = 3;

    /**
     * OG 需支付的🐶币
     */
    public static final  BigDecimal OG_COST_500 = new BigDecimal("500");

    /**
     * WL 需支付的🐶币
     */
    public static final BigDecimal WL_COST_800 = new BigDecimal("800");

    /**
     * PUL 需支付的🐶币 正式数据
     */
    public static final BigDecimal PUB_COST_1000 = new BigDecimal("1000");

    /**
     * PUL 需支付的🐶币 测试数据
     */
    public static final BigDecimal PUB_COST_1000_TEST = new BigDecimal("0.02");

    /**
     * mint 最小单位
     */
    public static final int MINT_1 = 1;

    /**
     * 每个用户公售可以 mint 的最多次数
     */
    public static final int PUB_MINT_10 = 10;

    /**
     * 每个用户公售可以 mint 的最多次数测试数据
     */
    public static final int PUB_MINT_10000_TEST = 10000;

    /**
     * 总体的NFT数量 9982
     */
    public static final Integer MAX_1W = 10000;
    public static final Integer MAX_9982 = 9982;

}
