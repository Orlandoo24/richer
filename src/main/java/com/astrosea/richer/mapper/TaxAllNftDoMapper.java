package com.astrosea.richer.mapper;

import com.astrosea.richer.pojo.TaxAllNftDo;
import com.astrosea.richer.vo.dto.HolderDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaxAllNftDoMapper extends BaseMapper<TaxAllNftDo> {
    /*
    -- 持有者SQL
SELECT address,
SUM(CASE WHEN rarity = 'lv1' THEN 1 ELSE 0 END) AS lv1_count,
SUM(CASE WHEN rarity = 'lv2' THEN 1 ELSE 0 END) AS lv2_count,
SUM(CASE WHEN rarity = 'lv3' THEN 1 ELSE 0 END) AS lv3_count,
SUM(CASE WHEN rarity = 'lv4' THEN 1 ELSE 0 END) AS lv4_count,
SUM(CASE WHEN rarity = 'lv5' THEN 1 ELSE 0 END) AS lv5_count,
SUM(CASE WHEN rarity = 'lv6' THEN 1 ELSE 0 END) AS lv6_count
FROM (
SELECT address, rarity
FROM tax_all_nft
WHERE is_deleted = b'0' AND other_json = '1' AND sell_status = 1 AND tax_status = 1
UNION ALL
SELECT address, rarity
FROM tax_1of1_nft
WHERE is_deleted = b'0' AND other_json = '1' AND sell_status = 1 AND tax_status = 1
) AS combined_table
GROUP BY address
     */
    @Select("SELECT address, " +
            "SUM(CASE WHEN rarity = 'lv1' THEN 1 ELSE 0 END) AS lv1_count, " +
            "SUM(CASE WHEN rarity = 'lv2' THEN 1 ELSE 0 END) AS lv2_count, " +
            "SUM(CASE WHEN rarity = 'lv3' THEN 1 ELSE 0 END) AS lv3_count, " +
            "SUM(CASE WHEN rarity = 'lv4' THEN 1 ELSE 0 END) AS lv4_count, " +
            "SUM(CASE WHEN rarity = 'lv5' THEN 1 ELSE 0 END) AS lv5_count, " +
            "SUM(CASE WHEN rarity = 'lv6' THEN 1 ELSE 0 END) AS lv6_count " +
            "FROM ( " +
            "SELECT address, rarity " +
            "FROM tax_all_nft " +
            "WHERE is_deleted = b'0' AND other_json = '1' AND sell_status = 1 AND tax_status = 1 " +
            "UNION ALL " +
            "SELECT address, rarity " +
            "FROM tax_1of1_nft " +
            "WHERE is_deleted = b'0' AND other_json = '1' AND sell_status = 1 AND tax_status = 1 " +
            ") AS combined_table " +
            "GROUP BY address")
    @Results({
            @Result(property = "address", column = "address"),
            @Result(property = "lv1amt", column = "lv1_count"),
            @Result(property = "lv2amt", column = "lv2_count"),
            @Result(property = "lv3amt", column = "lv3_count"),
            @Result(property = "lv4amt", column = "lv4_count"),
            @Result(property = "lv5amt", column = "lv5_count"),
            @Result(property = "lv6amt", column = "lv6_count")
    })
    List<HolderDto> getNftCountByRarity();

}
