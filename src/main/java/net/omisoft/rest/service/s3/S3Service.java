package net.omisoft.rest.service.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface S3Service {

    String uploadFile(MultipartFile multipartFile) throws IOException;

    InputStream downloadFile(String fileName);

    void deleteFile(String keyName);

    void deleteFiles(String[] keyNames);

    int fileCount();

}