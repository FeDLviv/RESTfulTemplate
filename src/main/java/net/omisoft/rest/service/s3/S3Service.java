package net.omisoft.rest.service.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;

public interface S3Service {

    int MAX_EXPIRE = 604800;

    String uploadFile(MultipartFile multipartFile) throws IOException;

    InputStream downloadFile(String fileName);

    void deleteFile(String keyName);

    void deleteFiles(String[] keyNames);

    int fileCount();

    /**
     * Get pre-signed URL
     *
     * @param fileName   The file name
     * @param expiration Set the pre-signed URL to expire, the maximum value you can set - seven days (https://docs.aws.amazon.com/en_us/AmazonS3/latest/API/sigv4-query-string-auth.html)
     * @return URL The pre-signed URL
     */
    URL getPreSignedUrl(String fileName, Instant expiration);

}