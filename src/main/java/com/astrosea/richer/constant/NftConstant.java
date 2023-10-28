package com.astrosea.richer.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NftConstant {
    public static final List<Integer> NFT_LV6_IDS = new ArrayList<>(Arrays.asList(
            9994, 7766, 6336, 6254, 5868, 4504, 4200, 3443, 3350, 2933, 2249, 2023, 1620, 970, 777, 555, 138, 3
    ));


    /**
     * 第一波发售
     */
    public static final String FIRST_RELEASE_1  = "1";



    /**
     * 分发状态
     */
    public static final int UNSEND_0 = 0;
    public static final int RECEIVED_1 = 1;


    /**
     * 售卖状态
     */
    public static final int UNSOLD = 0;// 未出售

    public static final int SOLD = 1;// 已出售

    /**
     * 缴税状态
     */
    public static final int UNTAXED = 0;// 未缴税
    public static final int TAX_PAID = 1;// 已缴税


}
