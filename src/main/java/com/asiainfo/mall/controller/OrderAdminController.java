package com.asiainfo.mall.controller;

import com.asiainfo.mall.common.ApiRestResponse;
import com.asiainfo.mall.service.OrderService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderAdminController {

    @Autowired
    OrderService orderService;

    @GetMapping("admin/order/list")
    @ApiOperation("管理员订单列表")
    public ApiRestResponse listForAdmin(@RequestParam Integer pageNum,@RequestParam Integer pageSize) {
        PageInfo pageInfo = orderService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @PostMapping("admin/order/delivered")
    @ApiOperation("管理员发货")
    public ApiRestResponse delivered(@RequestParam String orderNo) {
        orderService.deliver(orderNo);
        return ApiRestResponse.success();
    }

    @PostMapping("order/finish")
    @ApiOperation("完结订单")
    public ApiRestResponse finish(@RequestParam String orderNo) {
        orderService.finish(orderNo);
        return ApiRestResponse.success();
    }
}
