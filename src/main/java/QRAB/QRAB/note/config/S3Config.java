package QRAB.QRAB.note.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
@Getter
@Configuration // spring application context 에 빈을 정의
public class S3Config {
    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct //Spring bean 이 초기화된 후 자동으로 호출
    public void initializeS3Client(){
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials)) //고정된 자격증명을 사용하는 제공자 객체
                .withRegion(this.region)
                .build();
    }
//S3 버킷에 파일을 업르드하고 업로드된 파일의 공개 URL을 반환
    public String upload(MultipartFile file) throws IOException{
        String fileName = file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(),null)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return s3Client.getUrl(bucket, fileName).toString(); //업르된 파일의 URL을 반환
    }

    public String uploadScreenshot(File file, String fileName){
        s3Client.putObject(new PutObjectRequest(bucket, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return s3Client.getUrl(bucket, fileName).toString(); //업르된 파일의 URL을 반환
    }
}
