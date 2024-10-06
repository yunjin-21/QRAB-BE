/*package QRAB.QRAB.sms.config;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SMSConfig {

    @Value("${aws.sns.region}")
    private String region;

    @Value("${aws.sns.access-key}")
    private String accessKey;

    @Value("${aws.sns.secret-key}")
    private String secretKey;

    @Bean
    public AmazonSNS amazonSNS() {
        // 자격 증명과 리전 설정을 명시적으로 지정
        return AmazonSNSClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }
}
*/