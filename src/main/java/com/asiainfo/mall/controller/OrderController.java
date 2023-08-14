package com.asiainfo.mall.controller;

import com.asiainfo.mall.common.ApiRestResponse;
import com.asiainfo.mall.model.request.CreateOrderReq;
import com.asiainfo.mall.model.vo.OrderVO;
import com.asiainfo.mall.service.OrderService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 订单controller
 */
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("order/create")
    @ApiOperation("创建订单")
    public ApiRestResponse create(@RequestBody CreateOrderReq createOrderReq) {
        String orderNo = orderService.create(createOrderReq);
        return ApiRestResponse.success(orderNo);
    }

    @GetMapping("order/detail")
    @ApiOperation("前台订单详情")
    public ApiRestResponse detail(@RequestParam String orderNO) {
        OrderVO orderVO = orderService.detail(orderNO);
        return ApiRestResponse.success(orderVO);
    }

    @GetMapping("order/list")
    @ApiOperation("前台订单列表")
    public ApiRestResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = orderService.listForCustomer(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @PostMapping("order/cancel")
    @ApiOperation("前台取消订单")
    public ApiRestResponse cancel(@RequestParam String orderNo) {
        orderService.cancel(orderNo);
        return ApiRestResponse.success();
    }

    @PostMapping ("order/qrcode")
    @ApiOperation("生成支付二维码")
    public ApiRestResponse qrcode(@RequestParam String orderNo) {
        String pngAddress = orderService.qrcode(orderNo);
        return ApiRestResponse.success(pngAddress);
    }

    @GetMapping("order/pay")
    @ApiOperation("支付接口")
    public ApiRestResponse pay(@RequestParam String orderNo) {
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }
}
