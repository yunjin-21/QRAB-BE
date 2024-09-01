package QRAB.QRAB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing //Spring Date JPA의 Auditing 기능 활성화  - 생성 시간
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class QrabApplication {

	public static void main(String[] args) {
		SpringApplication.run(QrabApplication.class, args);
	}

}
