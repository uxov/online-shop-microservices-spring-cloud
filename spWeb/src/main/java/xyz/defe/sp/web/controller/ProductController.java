package xyz.defe.sp.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.PageQuery;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.response.ResponseDataResult;
import xyz.defe.sp.web.service.ProductService;

import java.util.List;

@RestController
@ResponseDataResult
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("products")
    public ResponseData<List<Product>> getProducts(PageQuery pageQuery){
        return productService.getProducts(pageQuery);
    }
}
