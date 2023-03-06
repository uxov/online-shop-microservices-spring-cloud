package xyz.defe.sp.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.PageQuery;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.response.ResponseDataResult;
import xyz.defe.sp.product.service.ProductService;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@ResponseDataResult
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("products")
    public ResponseData addProducts(@RequestBody List<Product> products) {
        productService.addProducts(products);
        return new ResponseData().setMessage("added products successful");
    }

    @GetMapping("products")
    public List<Product> getProducts(PageQuery pageQuery) {
        return productService.getProducts(pageQuery);
    }

    @GetMapping("product/{id}")
    public Product getById(@PathVariable String id) {
        return productService.getById(id);
    }

    @GetMapping("products/{ids}")
    public List<Product> getByIds(@PathVariable String ids) {
        return productService.getByIds(ids);
    }

    /**
     * @param ids
     * @return {data : {productId : quantity}}
     */
    @GetMapping("quantity/{ids}")
    public Map<String, Integer> getQuantity(@PathVariable String ids) {
        return productService.getQuantity(ids);
    }

    /**
     * restore product quantity
     * @param restoreMap {orderId : {productId : count}}
     * @return {data: Set<String>}  restore successful order's id
     */
    @PostMapping("quantity/restore")
    public Set<String> restoreProductQuantity(@RequestBody Map<String, Map<String, Integer>> restoreMap) {
        return productService.restoreProductQuantity(restoreMap);
    }
}
