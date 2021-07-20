package xyz.defe.sp.web.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.RestUtil;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.PageQuery;
import xyz.defe.sp.common.pojo.ResponseData;

import java.util.List;

@Service
public class ProductService extends BaseService{
    public List<Product> getProducts(PageQuery pageQuery) throws Exception {
        ResponseData<List<Product>> responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .get(baseURL + "productService/products?pageNum={pageNum}&pageSize={pageSize}",
                            new ParameterizedTypeReference<ResponseData<List<Product>>>(){},
                            pageQuery.getPageNum(), pageQuery.getPageSize());
        });
        return responseData.getData();
    }

}
