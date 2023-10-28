package com.astrosea.richer.mapper;

import com.astrosea.richer.pojo.OrderDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<OrderDo> {
}
