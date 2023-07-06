package com.asiainfo.mall.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

//前端的入参类
public class AddCategoryReq {

//    限制字符串长度，非空
    @Size(min = 2, max = 5,message = "name长度需在2-5之间")
    @NotNull(message = "name不能为null")
    private String name;
//限制类别最大值为3，非空
    @NotNull(message = "type不能为null")
    @Max(3)
    private Integer type;

    @NotNull
    private Integer parentId;

    @NotNull
    private Integer orderNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
}
