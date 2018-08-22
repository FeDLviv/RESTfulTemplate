package net.omisoft.rest.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class S3Configuration {

    private final PropertiesConfiguration propertiesConfiguration;

    @Bean
    public AmazonS3 s3client() {
        AWSCredentials credentials = new BasicAWSCredentials(propertiesConfiguration.getAmazon().getAccessKeyId(),
                propertiesConfiguration.getAmazon().getSecretAccessKey());
        return AmazonS3ClientBuilder.standard()
                .withRegion(propertiesConfiguration.getAmazon().getRegion())
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

}
