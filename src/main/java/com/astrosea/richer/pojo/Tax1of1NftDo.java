package com.astrosea.richer.pojo;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tax_1of1_nft")
public class Tax1of1NftDo extends BaseEntity {

    /**
     * nft 内部 id
     */
    private Long astroseaId;

    /**
     * 数字编号
     */
    private Integer nftId;

    /**
     * 名称
     */
    private String name;

    /**
     * 创世文本
     */
    private String gtId;


    /**
     * utxo数组
     */
    private String nftUtxo;

    /**
     * 稀有度
     */
    private String rarity;

    /**
     * url
     */
    private String imgUrl;

    /**
     * 星际文件存储 url
     */
    private String ipfsUrl;

    /**
     * 所属钱包地址
     */
    private String address;

    /**
     * 缴税状态
     */
    private Integer taxStatus;

    /**
     * 售卖状态
     */
    private Integer sellStatus;

    /**
     * 发售标记
     */
    private String otherJson;

}

