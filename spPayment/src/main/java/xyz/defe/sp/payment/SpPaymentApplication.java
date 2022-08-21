package xyz.defe.sp.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableJpaRepositories("xyz.defe.sp.payment.dao")
@EntityScan(basePackages = {
		"xyz.defe.sp.common.entity.general",
		"xyz.defe.sp.common.entity.spPayment",
		"xyz.defe.sp.payment.entity" })
@ComponentScan(basePackages = {"xyz.defe.sp.payment", "xyz.defe.sp.common"},
		excludeFilters = {
				@ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "xyz.defe.sp.common.dao.*"),
				@ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "xyz.defe.sp.common.service.*")
		}
)
public class SpPaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpPaymentApplication.class, args);
	}

}
