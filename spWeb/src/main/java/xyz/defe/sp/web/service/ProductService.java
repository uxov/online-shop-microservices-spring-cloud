package xyz.defe.sp.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.PageQuery;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.web.api.ProductFeignClient;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductFeignClient productFeignClient;

    public ResponseData<List<Product>> getProducts(PageQuery pageQuery) {
        return productFeignClient
                .getProducts(pageQuery.getPageNum(), pageQuery.getPageSize());
    }

}
