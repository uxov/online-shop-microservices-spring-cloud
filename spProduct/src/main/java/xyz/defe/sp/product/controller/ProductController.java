package xyz.defe.sp.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.defe.sp.common.ResponseWrap;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.PageQuery;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.product.service.ProductService;

import java.util.List;
import java.util.Map;

@RestController
public class ProductController {
    @Autowired
    private ResponseWrap response;
    @Autowired
    private ProductService productService;

    @PostMapping("")
    public ResponseData addProducts(@RequestBody List<Product> products) {
        return response.wrap(() -> {
            productService.addProducts(products);
            ResponseData responseData = new ResponseData();
            responseData.setMessage("added products successful");
            return responseData;
        });
    }

    @GetMapping("products")
    public ResponseData getProducts(PageQuery pageQuery) {
        return response.wrap(() -> productService.getProducts(pageQuery));
    }

    @GetMapping("product/{id}")
    public ResponseData getById(@PathVariable String id) {
        return response.wrap(() -> productService.getById(id));
    }

    @GetMapping("products/{ids}")
    public ResponseData getByIds(@PathVariable String ids) {
        return response.wrap(() -> productService.getByIds(ids));
    }

    /**
     * @param ids
     * @return {data : {productId : quantity}}
     */
    @GetMapping("quantity/{ids}")
    public ResponseData getQuantity(@PathVariable String ids) {
        return response.wrap(() -> productService.getQuantity(ids));
    }

    /**
     * restore product quantity
     * @param restoreMap {orderId : {productId : count}}
     * @return {data: Set<String>}  restore successful order's id
     */
    @PostMapping("quantity/restore")
    public ResponseData restoreProductQuantity(@RequestBody Map<String, Map<String, Integer>> restoreMap) {
        return response.wrap(() -> productService.restoreProductQuantity(restoreMap));
    }
}
