package com.asiainfo.mall.controller;

import com.asiainfo.mall.common.ApiRestResponse;
import com.asiainfo.mall.common.Constant;
import com.asiainfo.mall.common.ValidList;
import com.asiainfo.mall.exception.MallException;
import com.asiainfo.mall.exception.MallExceptionEnum;
import com.asiainfo.mall.model.pojo.Product;
import com.asiainfo.mall.model.request.AddProductReq;
import com.asiainfo.mall.model.request.UpdateProductReq;
import com.asiainfo.mall.service.ProductService;
import com.asiainfo.mall.service.UploadService;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 后台商品管理controller
 */
@Controller
@Validated
public class ProductAdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UploadService uploadService;

    @PostMapping("/admin/product/add")
    @ResponseBody
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq) {
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }

    @PostMapping("/admin/upload/file")
    @ResponseBody
    public ApiRestResponse upload(HttpServletRequest httpServletRequest,
                                  @RequestParam("file") MultipartFile file) {
        String result = uploadService.uploadFile(file);
        return ApiRestResponse.success(result);
    }


    private URI getHost(URI uri) {
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
                    null, null, null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }

    @PostMapping("/admin/product/update")
    @ResponseBody
    public ApiRestResponse updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        productService.update(product);
        return ApiRestResponse.success();
    }

    @PostMapping("/admin/product/delete")
    @ResponseBody
    public ApiRestResponse deleteProduct(@RequestParam Integer id) {
        productService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("批量上下架")
    @PostMapping("/admin/product/batchUpdateSellStatus")
    @ResponseBody
    public ApiRestResponse batchUpdateSellStatus(@RequestParam Integer[] ids, @RequestParam Integer sellStatus) {
        productService.batchUpdateSellStatus(ids, sellStatus);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台商品列表")
    @PostMapping("/admin/product/list")
    @ResponseBody
    public ApiRestResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = productService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("后台批量上传商品")
    @PostMapping("/admin/upload/product")
    public ApiRestResponse uploadProduct(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        String newFileName = uploadService.getNewFileName(multipartFile);
        //创建文件
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        //检查文件夹
        uploadService.createFile(multipartFile, fileDirectory, destFile);
        productService.addProductByExcel(destFile);
        return ApiRestResponse.success();
    }

    @PostMapping("/admin/upload/image")
    @ResponseBody
    public ApiRestResponse uploadImage(HttpServletRequest httpServletRequest,
                                  @RequestParam("file") MultipartFile file) throws IOException {
        String result = uploadService.uploadImage(file);
        return ApiRestResponse.success(result);
    }

    @ApiOperation("后台批量更新商品 手动验证")
    @PostMapping("/admin/product/batchUpdate1")
    public ApiRestResponse batchUpdateProduct1(@Valid @RequestBody List<UpdateProductReq> updateProductReqList) {
        //需要校验列表时，注解校验失效
        //解决方法1:手动校验
        for (UpdateProductReq updateProductReq : updateProductReqList) {
            if (updateProductReq.getPrice() < 1) {
                throw new MallException(MallExceptionEnum.PRICE_TOO_LOW);
            }
            if (updateProductReq.getStock() > 10000) {
                throw new MallException(MallExceptionEnum.STOCK_TOO_MANY);
            }
            Product product = new Product();
            BeanUtils.copyProperties(updateProductReq, product);
            productService.update(product);
        }
        return ApiRestResponse.success();
    }

    @ApiOperation("后台批量更新商品 ValidList验证")
    @PostMapping("/admin/product/batchUpdate2")
    public ApiRestResponse batchUpdateProduct2(@Valid @RequestBody ValidList<UpdateProductReq> updateProductReqList) {
        //需要校验列表时，注解校验失效
        //解决方法2: 验证列表校验ValidList
        for (UpdateProductReq updateProductReq : updateProductReqList) {
            Product product = new Product();
            BeanUtils.copyProperties(updateProductReq, product);
            productService.update(product);
        }
        return ApiRestResponse.success();
    }

    @ApiOperation("后台批量更新商品 @Validated验证")
    @PostMapping("/admin/product/batchUpdate3")
    public ApiRestResponse batchUpdateProduct3(@Valid @RequestBody List<UpdateProductReq> updateProductReqList) {
        //需要校验列表时，注解校验失效
        //解决方法2: 验证列表校验ValidList
        for (UpdateProductReq updateProductReq : updateProductReqList) {
            Product product = new Product();
            BeanUtils.copyProperties(updateProductReq, product);
            productService.update(product);
        }
        return ApiRestResponse.success();
    }

}
