package xyz.defe.sp.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.defe.sp.common.pojo.PageQuery;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.web.service.ProductService;

@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {
    @Autowired
    private ProductService productService;

    @GetMapping("list")
    public ResponseData getProducts(PageQuery pageQuery){
        return response(() -> productService.getProducts(pageQuery));
    }
}
