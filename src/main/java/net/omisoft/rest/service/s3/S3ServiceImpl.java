package net.omisoft.rest.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.AllArgsConstructor;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.exception.BadRequestException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Service
@AllArgsConstructor
public class S3ServiceImpl implements S3Service {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");

    private final AmazonS3 s3client;
    private final MessageSourceConfiguration message;
    private final Environment environment;
    private final Random random = new Random();

    @Override
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        checkFileEmpty(multipartFile);
        String fileName = generateUniqueFileName(multipartFile.getOriginalFilename());
        File file = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }
        s3client.putObject(new PutObjectRequest(environment.getProperty("app.amazon.bucket"), fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        file.delete();
        return fileName;
    }

    @Override
    public void deleteFile(String keyName) {
        s3client.deleteObject(new DeleteObjectRequest(environment.getProperty("app.amazon.bucket"), keyName));
    }

    @Override
    public void deleteFiles(String[] keyNames) {
        DeleteObjectsRequest request = new DeleteObjectsRequest(environment.getProperty("app.amazon.bucket"))
                .withKeys(keyNames);
        s3client.deleteObjects(request);
    }

    @Override
    public int fileCount() {
        ObjectListing listing = s3client.listObjects(environment.getProperty("app.amazon.bucket"));
        return listing.getObjectSummaries().size();

    }

    private void checkFileEmpty(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new BadRequestException(message.getMessage("exception.file.empty"));
        }
    }

    private String generateUniqueFileName(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        fileName = DATE_FORMAT.format(new Date()) + "_" + random.nextInt(9999) + "." + fileExtension;
        return fileName;
    }

}
