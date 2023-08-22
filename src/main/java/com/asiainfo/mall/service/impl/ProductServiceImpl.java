package com.asiainfo.mall.service.impl;

import com.asiainfo.mall.common.Constant;
import com.asiainfo.mall.exception.MallException;
import com.asiainfo.mall.exception.MallExceptionEnum;
import com.asiainfo.mall.model.dao.ProductMapper;
import com.asiainfo.mall.model.pojo.Product;
import com.asiainfo.mall.model.query.ProductListQuery;
import com.asiainfo.mall.model.request.AddProductReq;
import com.asiainfo.mall.model.request.ProductListReq;
import com.asiainfo.mall.model.vo.CategoryVO;
import com.asiainfo.mall.service.CategoryService;
import com.asiainfo.mall.service.ProductService;
import com.asiainfo.mall.util.ExcelUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryService categoryService;

    @Override
    public void add(AddProductReq addProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq,product);
        Product productOld = productMapper.selectByName(addProductReq.getName());
        if (productOld != null) {
            throw new MallException(MallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new MallException(MallExceptionEnum.CREATE_FAILED);
        }
    }

    @Override
    public void update(Product product) {
        Product productOld = productMapper.selectByName(product.getName());
        if (productOld != null && product.getId() != productOld.getId()) {
            throw new MallException(MallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count == 0) {
            throw new MallException(MallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id) {
        Product productOld = productMapper.selectByPrimaryKey(id);
        //查不到该记录，无法删除
        if (productOld == null) {
            throw new MallException(MallExceptionEnum.DELETE_FAILED);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new MallException(MallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        productMapper.batchUpdateSellStatus(ids, sellStatus);
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectListForAdmin();
        PageInfo pageInfo = new PageInfo(products);
        return pageInfo;
    }

    @Override
    public Product detail(Integer id) {
        return productMapper.selectByPrimaryKey(id);
    }


    @Override
    public PageInfo list(ProductListReq productListReq) {
        ProductListQuery productListQuery = new ProductListQuery();
        if (!StringUtils.isEmpty(productListReq.getKeyword())) {
            String keyword = new StringBuilder().append("%").append(productListReq.getKeyword()).append("%").toString();
            productListQuery.setKeyword(keyword);
        }

        if (productListReq.getCategoryId() != null) {
            List<CategoryVO> categoryVOS = categoryService.listForCustomer(productListReq.getCategoryId());
            ArrayList<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVOS,categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }

        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(), orderBy);
        }else{
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
        }

        List<Product> products = productMapper.selectList(productListQuery);
        PageInfo pageInfo = new PageInfo(products);
        return pageInfo;
    }

    private void getCategoryIds(List<CategoryVO>categoryVOS,ArrayList<Integer> categoryIds){
        for (CategoryVO categoryVO : categoryVOS) {
            if (categoryVO != null) {
                categoryIds.add(categoryVO.getId());
                getCategoryIds(categoryVO.getChildCategory(),categoryIds);
            }
        }
    }

    @Override
    public void addProductByExcel(File destFile) throws IOException {
        List<Product> products = readProductFromExcel(destFile);
        System.out.println(products);
        for (Product product : products) {
            Product productOld = productMapper.selectByName(product.getName());
            if (productOld != null) {
                throw new MallException(MallExceptionEnum.NAME_EXISTED);
            }
            int count = productMapper.insertSelective(product);
            if (count == 0) {
                throw new MallException(MallExceptionEnum.CREATE_FAILED);
            }
        }
    }

    private List<Product> readProductFromExcel(File excelFile) throws IOException {
        ArrayList<Product> listProducts = new ArrayList<>();
        FileInputStream fileInputStream = new FileInputStream(excelFile);
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet firstSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = firstSheet.iterator();
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            Product product = new Product();
            while (cellIterator.hasNext()) {
                Cell nextCell = cellIterator.next();
                int columnIndex = nextCell.getColumnIndex();
                switch(columnIndex){
                    case 0:
                        product.setName((String) ExcelUtil.getCellValue(nextCell));
                        break;
                    case 1:
                        product.setImage((String) ExcelUtil.getCellValue(nextCell));
                        break;
                    case 2:
                        product.setDetail((String) ExcelUtil.getCellValue(nextCell));
                        break;
                    case 3:
                        Double cellValue = (Double) ExcelUtil.getCellValue(nextCell);
                        product.setCategoryId(cellValue.intValue());
                        break;
                    case 4:
                        cellValue = (Double) ExcelUtil.getCellValue(nextCell);
                        product.setPrice(cellValue.intValue());
                        break;
                    case 5:
                        cellValue = (Double) ExcelUtil.getCellValue(nextCell);
                        product.setStock(cellValue.intValue());
                        break;
                    case 6:
                        cellValue = (Double) ExcelUtil.getCellValue(nextCell);
                        product.setStatus(cellValue.intValue());
                        break;
                    default:
                        break;

                }
            }
            listProducts.add(product);
        }
        workbook.close();
        fileInputStream.close();
        return listProducts;
    }

}
