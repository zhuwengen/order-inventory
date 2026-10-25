package com.corey.mall.orderinventory.order.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.corey.mall.orderinventory.order.persistence.entity.SkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SkuMapper extends BaseMapper<SkuEntity> {

    /**
     * 创建待支付订单时，将可用库存原子转为冻结库存；库存尚未实际消耗。
     * 条件判断与字段更新置于同一条 SQL 中，避免并发下超卖。
     */
    @Update("""
            update t_sku
            set available_qty = available_qty - #{quantity},
                frozen_qty = frozen_qty + #{quantity}
            where sku = #{sku}
              and delete_flag = 0
              and available_qty >= #{quantity}
            """)
    int freezeAvailableQuantity(@Param("sku") String sku, @Param("quantity") int quantity);
}
