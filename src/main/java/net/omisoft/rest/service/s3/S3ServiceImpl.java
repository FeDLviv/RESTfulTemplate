package net.omisoft.rest.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.AllArgsConstructor;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.configuration.PropertiesConfiguration;
import net.omisoft.rest.exception.BadRequestException;
import net.omisoft.rest.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Service
@AllArgsConstructor
public class S3ServiceImpl implements S3Service {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");

    private final AmazonS3 s3client;
    private final MessageSourceConfiguration message;
    private final PropertiesConfiguration propertiesConfiguration;
    private final Random random = new Random();

    @Override
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        checkFileEmpty(multipartFile);
        String fileName = generateUniqueFileName(multipartFile.getOriginalFilename());
        File file = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }
        PutObjectRequest putObjectRequest = new PutObjectRequest(propertiesConfiguration.getAmazon().getBucket(), fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        s3client.putObject(putObjectRequest);
        file.delete();
        return fileName;
    }

    @Override
    public InputStream downloadFile(String fileName) {
        try {
            S3Object obj = s3client.getObject(propertiesConfiguration.getAmazon().getBucket(), fileName);
            return obj.getObjectContent();
        } catch (AmazonS3Exception ex) {
            throw new ResourceNotFoundException(ex.getLocalizedMessage());
        }
    }

    @Override
    public void deleteFile(String keyName) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(propertiesConfiguration.getAmazon().getBucket(), keyName);
        s3client.deleteObject(deleteObjectRequest);
    }

    @Override
    public void deleteFiles(String[] keyNames) {
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(propertiesConfiguration.getAmazon().getBucket())
                .withKeys(keyNames);
        s3client.deleteObjects(deleteObjectsRequest);
    }

    @Override
    public int fileCount() {
        ObjectListing listing = s3client.listObjects(propertiesConfiguration.getAmazon().getBucket());
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
