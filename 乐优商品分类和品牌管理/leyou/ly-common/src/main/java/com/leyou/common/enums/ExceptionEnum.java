package com.leyou.common.enums;

import lombok.Getter;

@Getter
public enum ExceptionEnum {
    PRICE_CANNOT_BE_NULL(400, "价格不能为空！"),
    DATA_TRANSFER_ERROR(500,"【数据转换】数据转换出错，目标对象{}构造函数异常"), CATEGORY_NOT_FOND(404,"数据没有发现" ),
    BRAND_NOT_FOND(204,"无法查询到品牌信息" ),
    ADD_BRAND_FAILL(500,"添加品牌失败" );
    ;
    private int status;
    private String message;

    ExceptionEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }
}