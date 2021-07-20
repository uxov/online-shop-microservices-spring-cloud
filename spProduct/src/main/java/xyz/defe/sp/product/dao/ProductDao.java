package xyz.defe.sp.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.defe.sp.common.entity.spProduct.Product;

@Repository
public interface ProductDao extends JpaRepository<Product, String> {
}
