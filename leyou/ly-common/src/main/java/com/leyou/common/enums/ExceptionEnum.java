package com.leyou.common.enums;

import lombok.Getter;

@Getter
public enum ExceptionEnum {
    PRICE_CANNOT_BE_NULL(400, "价格不能为空！"),
    DATA_TRANSFER_ERROR(500,"【数据转换】数据转换出错，目标对象{}构造函数异常"), CATEGORY_NOT_FOND(404,"数据没有发现" ),
    BRAND_NOT_FOND(204,"无法查询到品牌信息" ),
    ADD_BRAND_FAILL(500,"添加品牌失败" ),
    INVALID_FILE_TYPE(204,"不支持的图片类型" ),
    INVALID_NOT_FOUND(204,"图片不存在" ), Update_BRAND_FAILL(204,"修改品牌信息失败" ),
    BRANDID_NUM_FAILL(500,"商品分类的数量错误" ), Spec_NOT_FOUND(204,"没有相应的规格参数组信息" ),
    Params_NOT_FOUND(204,"没有相应的规格参数" ),
    GOODS_NOT_FOUND(204,"商品信息查询不到" ), UPDATE_OPERATION_FAIL(500,"阿里云服务不可用" ),
    DATA_INSETR_ERROR(400,"数据提交异常" ),
    Update_Spu_FAILL(204,"修改通用规格参数失败" ),
    Update_SKU_FAILL(204,"修改特殊规格参数失败" ),
    Update_SpuDetail_FAILL(204,"修改通用规格参数详细内容失败" ),
    DELETE_SKU_FAILL(204,"删除特殊规格参数失败" ),
    INVALID_PARAM_ERROR(204,"数据显示错误" ),
    DIRECTORY_WRITER_ERROR(500,"文件夹创建失败" ),
    FILE_WRITER_ERROR(500,"文件创建失败" ),
    SEND_MESSAGE_ERROR(500,"短信发送失败" ),
    INVALID_PHONE_NUMBER(400,"手机号不符合格式" ),
    INVALID_VERIFY_CODE(400,"验证码错误" ),
    INSERT_OPERATION_FAIL(204,"插入数据失败" ),
    INVALID_USERNAME_PASSWORD(403,"用户名或密码错误请重新输入" ),
    NOLOGIN_USERNAME_PASSWORD(403,"用户没有登录" ),
    Sku_NOT_FOND(204,"没有该类的特殊规格参数" ),
    CARTS_NOT_FOUND(400,"没有发现该用户的购物车" ),
    INVALID_SERVER_ID_SECRET(400, "服务不可查询");
    ;
    private int status;
    private String message;

    ExceptionEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }
}