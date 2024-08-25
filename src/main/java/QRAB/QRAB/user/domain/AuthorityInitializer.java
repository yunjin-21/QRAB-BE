package QRAB.QRAB.user.domain;

import QRAB.QRAB.user.repository.AuthorityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration // 스프링의 구성 클래스를 나타냄
public class AuthorityInitializer {
    @Bean //CommandLineRunner 가 스프링 컨택스트 빈으로 등록
    @Transactional
    public CommandLineRunner initAuthorities(AuthorityRepository authorityRepository){
        return args -> {
            if(!authorityRepository.existsById("ROLE_USER")){ //권한이 이미 존재하는지 확인
                authorityRepository.save(new Authority("ROLE_USER")); //db에 권한을 추가
            }
            if (!authorityRepository.existsById("ROLE_ADMIN")){// 권한이 이미 존재하는지 확인
                authorityRepository.save(new Authority("ROLE_ADIN"));// db에 권한을 추가
            }
        };
    }
}
