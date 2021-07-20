package xyz.defe.sp.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableDiscoveryClient
@SpringBootApplication
@EnableJpaRepositories(basePackages = {"xyz.defe.sp.product.dao"})
@EntityScan(basePackages = {"xyz.defe.sp.common.entity.spProduct", "xyz.defe.sp.product.entity" })
@ComponentScan(basePackages = {"xyz.defe.sp.product", "xyz.defe.sp.common"},
		excludeFilters = {
				@ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "xyz.defe.sp.common.dao.*"),
				@ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "xyz.defe.sp.common.service.*")
		}
)
public class SpProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpProductApplication.class, args);
	}

}
