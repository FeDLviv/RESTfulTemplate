package net.omisoft.rest.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.util.IOUtils;
import com.google.common.io.CharStreams;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.configuration.PropertiesConfiguration;
import net.omisoft.rest.service.s3.S3Service;
import net.omisoft.rest.service.s3.S3ServiceImpl;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.time.Instant;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class S3ServiceTest {

    private final static String S3_ACCESS_KEY = "admin";
    private final static String S3_SECRET_KEY = "admin123";
    private final static String S3_ENDPOINT = "http://127.0.0.1:9000";
    private final static String S3_REGION = "eu-central-1";
    private final static String S3_BUCKET = "test";
    private final static String SOME_TEXT = "Hello";

    @TestConfiguration
    public static class TokenTaskTestContextConfiguration {

        @Bean
        public AmazonS3 amazonS3() {
            AWSCredentials credentials = new BasicAWSCredentials(S3_ACCESS_KEY, S3_SECRET_KEY);
            AmazonS3 s3Client = AmazonS3ClientBuilder
                    .standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(S3_ENDPOINT, S3_REGION))
                    .withPathStyleAccessEnabled(true)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();
            s3Client.createBucket(new CreateBucketRequest(S3_BUCKET));
            return s3Client;
        }

        @Bean
        public PropertiesConfiguration propertiesConfiguration() {
            PropertiesConfiguration properties = new PropertiesConfiguration();
            PropertiesConfiguration.Amazon amazon = new PropertiesConfiguration.Amazon();
            amazon.setBucket(S3_BUCKET);
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

    @ClassRule
    public static ExternalResource resource = new ExternalResource() {

        @Override
        protected void before() throws Throwable {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command("docker-compose", "-f", "docker-compose-minio.yml", "up", "-d");
            builder.start().waitFor();

            HttpClient client;
            HttpResponse response = null;
            while (response == null) {
                try {
                    Thread.sleep(500);
                    client = HttpClientBuilder.create().build();
                    response = client.execute(new HttpGet(S3_ENDPOINT));
                } catch (Exception ex) {
                    response = null;
                }
            }
        }

        @Override
        protected void after() {
            Runtime rt = Runtime.getRuntime();
            try {
                rt.exec("docker-compose -f docker-compose-minio.yml down").waitFor();
            } catch (InterruptedException | IOException ex) {
                ex.printStackTrace();
            }
        }

    };

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Autowired
    private S3Service s3Service;

    @Test
    public void work() throws IOException {
        //prepare
        File file = temporaryFolder.newFile("temp.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        writer.append(SOME_TEXT);
        writer.close();

        //test - fileCount()
        assertEquals(s3Service.fileCount(), 0);

        //test - uploadFile()
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(new FileInputStream(file)));
        String s3FileName = s3Service.uploadFile(multipartFile);
        assertEquals(s3Service.fileCount(), 1);

        //test - downloadFile()
        InputStream downloadFile = s3Service.downloadFile(s3FileName);
        String textFromS3File = CharStreams.toString(new InputStreamReader(downloadFile));
        assertEquals(textFromS3File, SOME_TEXT);

        //test - deleteFile
        s3Service.deleteFile(s3FileName);
        assertEquals(s3Service.fileCount(), 0);

        //test - getPreSignedUrl()
        multipartFile = new MockMultipartFile("newFile", file.getName(), "text/plain", IOUtils.toByteArray(new FileInputStream(file)));
        s3FileName = s3Service.uploadFile(multipartFile);
        URL url = s3Service.getPreSignedUrl(s3FileName, Instant.now().plusSeconds(30));
        textFromS3File = CharStreams.toString(new InputStreamReader(url.openStream()));
        assertEquals(textFromS3File, SOME_TEXT);

        //test - deleteFiles
        s3Service.deleteFiles(new String[]{s3FileName});
        assertEquals(s3Service.fileCount(), 0);
    }

}