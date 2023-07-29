package com.astrosea.richer.pojo;

import com.astrosea.richer.utils.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tax_all_nft")
public class TaxAllNftDo extends BaseEntity {

    private String name;

    private Integer riAstroseaId;

    private String gtId;

    private String nowXtIndex;

    private String nowXtId;

    private String rarity;

    private String imgUrl;

    private String address;

    private Integer taxStatus;

    private Integer sellStatus;

    private String otherJson;

}
