package com.astrosea.richer.oss.service;

public interface OssService {

    /**
     * 通过编号获取 nft  url
     * @param num
     * @return
     */
    String getUrlByNum(Integer num);

    /**
     * 通过编号获取 nft 带域名的 url
     * @param num
     * @return
     */
    String getEndpointUrlByNum(Integer num);

    String getUrlByName(String name);











}
