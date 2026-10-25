-- t_sku 商品sku和库存数量
create table t_sku
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    sku           varchar(32)  default ''                not null comment 'sku编码',
    sku_name      varchar(128) default ''                not null comment 'sku名称',
    total_qty     bigint(12)  default 0                 not null comment '库存总数',
    available_qty bigint(12)  default 0                 not null comment '可用库存',
    consumed_qty  bigint(12)  default 0                 not null comment '消耗库存',
    frozen_qty    bigint(12)  default 0                 not null comment '冻结库存',
    delete_flag   tinyint      default 0                 not null comment '0-未删除, 1-已删除',
    creator       varchar(64)  default 'system'          not null comment '创建人',
    updater       varchar(64)  default 'system'          not null comment '更新人',
    create_time   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    unique idx_sku (sku)
) comment '商品sku和库存数量' charset = utf8mb4;

-- t_order 订单和订单状态
create table t_order
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    order_code      varchar(50)    default ''       not null comment '订单号',
    idempotency_key VARCHAR(64)                     not null comment '幂等键',
    order_status    tinyint(1)     default 0        not null comment '0-待支付, 1-已支付,2-已取消',
    original_price  decimal(10, 2)                  not null comment '原价总和',
    discount_price  decimal(10, 2) default 0.00     not null comment '优惠金额',
    pay_price       decimal(10, 2)                  not null comment '实际支付金额',
    pay_time        datetime                        null comment '支付完成时间',
    cancel_reason   varchar(256)                    null comment '取消原因',
    delete_flag     tinyint        default 0        not null comment '0-未删除, 1-已删除',
    creator         varchar(64)    default 'system' not null comment '创建人',
    updater         varchar(64)    default 'system' not null comment '更新人',
    create_time   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    unique unique_index_order (order_code),
    unique unique_index_ide(idempotency_key)
) comment 't_order 订单和订单状态' charset = utf8mb4;

-- t_order_item 订单明细
create table t_order_item
(
    id                  bigint auto_increment comment '主键ID'
        primary key,
    order_code          varchar(50)                  not null comment '订单号',
    sku                 varchar(32)                  not null comment 'SKU编码',
    sku_name            varchar(128)                 not null comment '商品快照名称',
    original_unit_price decimal(10, 2)               not null comment '原价单价快照',
    actual_unit_price   decimal(10, 2)               not null comment '最终成交单价快照（计算秒杀/区域价后）',
    quantity            int                          not null comment '购买数量',
    delete_flag         tinyint     default 0        not null comment '0-未删除, 1-已删除',
    creator             varchar(64) default 'system' not null comment '创建人',
    updater             varchar(64) default 'system' not null comment '更新人',
    create_time   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_order (order_code, delete_flag)
) comment '订单明细表' charset = utf8mb4;