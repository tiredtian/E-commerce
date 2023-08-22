package com.asiainfo.mall.service;

import com.asiainfo.mall.model.request.CreateOrderReq;
import com.asiainfo.mall.model.vo.OrderStatisticsVO;
import com.asiainfo.mall.model.vo.OrderVO;
import com.github.pagehelper.PageInfo;

import java.util.Date;
import java.util.List;

public interface OrderService {
    String create(CreateOrderReq createOrderReq);

    OrderVO detail(String orderNo);


    PageInfo listForCustomer(Integer pageNum, Integer pageSize);

    void cancel(String orderNo);

    String qrcode(String orderNo);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    void deliver(String orderNo);

    void finish(String orderNo);


    void pay(String orderNo);

    List<OrderStatisticsVO> statistics(Date startDate, Date endDate);
}
