package com.corey.mall.orderinventory.order.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.corey.mall.orderinventory.order.persistence.entity.OrderItemEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItemEntity> {
}
