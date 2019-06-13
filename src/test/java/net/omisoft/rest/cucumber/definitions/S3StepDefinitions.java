package net.omisoft.rest.cucumber.definitions;

import com.amazonaws.util.IOUtils;
import com.google.common.io.CharStreams;
import cucumber.api.java.en.When;
import net.omisoft.rest.cucumber.CucumberTest;
import net.omisoft.rest.cucumber.SpringBootConfigCucumberTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.time.Instant;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class S3StepDefinitions extends SpringBootConfigCucumberTest {

    private String s3LastFileName;
    private URL s3LastPreSignedURL;
    private String s3LastFileContent;

    @When("the client call fileCount method, he receives {int}")
    public void theClientCallFileCountMethodHeReceives(int count) {
        assertEquals(s3Service.fileCount(), count);
    }

    @When("client call uploadFile method, with text {string}")
    public void clientCallUploadFileMethodWithText(String text) throws IOException {
        File file = CucumberTest.TEMPORARY_FOLDER.newFile(UUID.randomUUID().toString() + ".txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        writer.append(text);
        writer.close();

        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(new FileInputStream(file)));
        s3LastFileName = s3Service.uploadFile(multipartFile);
    }

    @When("client call downloadFile method - lastFile")
    public void clientCallDownloadFileMethodLastFile() throws IOException {
        InputStream downloadFile = s3Service.downloadFile(s3LastFileName);
        s3LastFileContent = CharStreams.toString(new InputStreamReader(downloadFile));
    }

    @When("the client parse last download file, he receives {string}")
    public void theClientParseLastDownloadFileHeReceives(String text) {
        assertEquals(s3LastFileContent, text);
    }

    @When("client call deleteFile method - lastFile")
    public void clientCallDeleteFileMethodLastFile() {
        s3Service.deleteFile(s3LastFileName);
    }

    @When("client call getPreSignedUrl method - lastFile")
    public void clientCallGetPreSignedUrlMethodLastFile() {
        s3LastPreSignedURL = s3Service.getPreSignedUrl(s3LastFileName, Instant.now().plusSeconds(30));
    }

    @When("client use last preSigned URL - download and parse file, he receives {string}")
    public void clientUseLastPreSignedUrlDownloadAndParseFileHeReceives(String text) throws IOException {
        s3LastFileContent = CharStreams.toString(new InputStreamReader(s3LastPreSignedURL.openStream()));
        assertEquals(s3LastFileContent, text);
    }

    @When("client call deleteFiles method - lastFile")
    public void clientCallDeleteFilesMethodLastFile() {
        s3Service.deleteFiles(new String[]{s3LastFileName});
    }

}
