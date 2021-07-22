package xyz.defe.sp.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.defe.sp.common.pojo.PageQuery;
import xyz.defe.sp.common.response.ResponseDataResult;
import xyz.defe.sp.web.service.ProductService;

@RestController
@ResponseDataResult
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("list")
    public Object getProducts(PageQuery pageQuery){
        return productService.getProducts(pageQuery);
    }
}
