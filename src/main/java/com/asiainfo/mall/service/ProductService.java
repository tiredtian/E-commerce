package com.asiainfo.mall.service;

import com.asiainfo.mall.model.pojo.Category;
import com.asiainfo.mall.model.pojo.Product;
import com.asiainfo.mall.model.request.AddCategoryReq;
import com.asiainfo.mall.model.request.AddProductReq;
import com.asiainfo.mall.model.request.ProductListReq;
import com.asiainfo.mall.model.vo.CategoryVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ProductService {

    void add(AddProductReq addProductReq);

    void update(Product product);

    void delete(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    Product detail(Integer id);

    PageInfo list(ProductListReq productListReq);
}
