package xyz.defe.sp.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories("xyz.defe.sp.user.dao")
@EntityScan(basePackages = { "xyz.defe.sp.common.entity.spUser", "xyz.defe.sp.user.entity" })
@ComponentScan(basePackages = {"xyz.defe.sp.user", "xyz.defe.sp.common"},
		excludeFilters = {
				@ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "xyz.defe.sp.common.dao.*"),
				@ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "xyz.defe.sp.common.service.*")
		}
)
public class SpUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpUserApplication.class, args);
	}

}
