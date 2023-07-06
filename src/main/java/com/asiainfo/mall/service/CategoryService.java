package com.asiainfo.mall.service;

import com.asiainfo.mall.model.pojo.Category;
import com.asiainfo.mall.model.request.AddCategoryReq;
import com.asiainfo.mall.model.vo.CategoryVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CategoryService {
    public void add(AddCategoryReq addCategoryReq);

    void update(Category updateCategory);

    void delete(Integer id);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    List<CategoryVO> listForCustomer();
}
