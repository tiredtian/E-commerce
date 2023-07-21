package com.asiainfo.mall.service;

import com.asiainfo.mall.model.vo.CartVO;

import java.util.List;

public interface CartService {
    List<CartVO> add(Integer userId, Integer productId, Integer count);
}
