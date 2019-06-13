package net.omisoft.rest.cucumber;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.configuration.PropertiesConfiguration;
import net.omisoft.rest.service.s3.S3Service;
import net.omisoft.rest.service.s3.S3ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
        SpringBootConfigCucumberTest.TokenTaskTestContextConfiguration.class
})
public abstract class SpringBootConfigCucumberTest {

    @TestConfiguration
    public static class TokenTaskTestContextConfiguration {

        @Bean
        public AmazonS3 amazonS3() {
            AWSCredentials credentials = new BasicAWSCredentials(CucumberTest.S3_ACCESS_KEY, CucumberTest.S3_SECRET_KEY);
            AmazonS3 s3Client = AmazonS3ClientBuilder
                    .standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(CucumberTest.S3_ENDPOINT, CucumberTest.S3_REGION))
                    .withPathStyleAccessEnabled(true)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();
            s3Client.createBucket(new CreateBucketRequest(CucumberTest.S3_BUCKET));
            return s3Client;
        }

        @Bean
        @Primary
        public PropertiesConfiguration propertiesConfiguration() {
            PropertiesConfiguration properties = new PropertiesConfiguration();
            PropertiesConfiguration.Amazon amazon = new PropertiesConfiguration.Amazon();
            amazon.setBucket(CucumberTest.S3_BUCKET);
            properties.setAmazon(amazon);
            return properties;
        }

        @MockBean
        private MessageSourceConfiguration messageSourceConfiguration;

        @Bean
        public S3Service s3Service() {
            return new S3ServiceImpl(amazonS3(), messageSourceConfiguration, propertiesConfiguration());
        }

    }

    @Autowired
    protected S3Service s3Service;

}