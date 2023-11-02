package com.astrosea.richer.mapper;

import com.astrosea.richer.pojo.OrderDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper extends BaseMapper<OrderDo> {

    @Select(" SELECT id, order_id, address, order_time, status, reward_amt, insert_time, update_time, is_deleted\n" +
            "        FROM rich_order\n" +
            "        WHERE order_id = #{orderId}")
    OrderDo selectOneOrder(Long order);
}
