package net.omisoft.rest.cucumber;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features", snippets = SnippetType.CAMELCASE, strict = true, plugin = {"pretty", "html:cucumber/report"})
public class CucumberTest {

    public final static String S3_ENDPOINT = "http://127.0.0.1:9000";
    public final static String S3_ACCESS_KEY = "admin";
    public final static String S3_SECRET_KEY = "admin123";
    public final static String S3_REGION = "eu-central-1";
    public final static String S3_BUCKET = "test";

    @ClassRule
    public final static TemporaryFolder TEMPORARY_FOLDER = new TemporaryFolder();

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
                    Thread.sleep(1000);
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

}