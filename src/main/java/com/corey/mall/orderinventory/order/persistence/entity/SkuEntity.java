package com.corey.mall.orderinventory.order.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_sku")
public class SkuEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sku;

    @TableField("sku_name")
    private String skuName;

    @TableField("total_qty")
    private Long totalQty;

    @TableField("available_qty")
    private Long availableQty;

    @TableField("consumed_qty")
    private Long consumedQty;

    @TableField("frozen_qty")
    private Long frozenQty;

    @TableField("delete_flag")
    private Integer deleteFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public Long getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Long totalQty) {
        this.totalQty = totalQty;
    }

    public Long getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Long availableQty) {
        this.availableQty = availableQty;
    }

    public Long getConsumedQty() {
        return consumedQty;
    }

    public void setConsumedQty(Long consumedQty) {
        this.consumedQty = consumedQty;
    }

    public Long getFrozenQty() {
        return frozenQty;
    }

    public void setFrozenQty(Long frozenQty) {
        this.frozenQty = frozenQty;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
