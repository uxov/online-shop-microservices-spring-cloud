package xyz.defe.sp.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = {"xyz.defe.sp.order.dao", "xyz.defe.sp.common.dao"})
@EntityScan(basePackages = {"xyz.defe.sp.common.entity.general", "xyz.defe.sp.common.entity.spOrder"})
@SpringBootApplication(scanBasePackages = {"xyz.defe.sp.order", "xyz.defe.sp.common"})
public class SpOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpOrderApplication.class, args);
	}

}
