package com.astrosea.richer.utils;

import static com.astrosea.richer.constant.NftConstant.NFT_LV6_IDS;

public class RarityUtil {

    public static boolean isLV1(Integer nftId) {
        return NFT_LV6_IDS.contains(nftId);
    }

}
