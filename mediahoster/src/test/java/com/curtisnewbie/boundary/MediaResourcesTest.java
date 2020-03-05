package com.curtisnewbie.boundary;

import io.quarkus.test.junit.QuarkusTest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

@QuarkusTest
public class MediaResourcesTest {

    static final String MEDIA_DIR = "media_test";

    static final String MEDIA_ONE = "mediaOne.mp4";
    static final String MEDIA_TWO = "mediaTwo.mp4";
    static final String DEMO_DATA = "1234";

    static File mediaOne;
    static File mediaTwo;

    @BeforeAll
    public static void createDemoMediaFiles() throws IOException {
        File file = new File(MEDIA_DIR);
        if (!file.exists()) {
            file.mkdir();

            if (file.exists()) {
                // create demo files
                mediaOne = new File(file, MEDIA_ONE);
                mediaTwo = new File(file, MEDIA_TWO);
                mediaOne.createNewFile();
                mediaTwo.createNewFile();

                try (var fileOut = new FileOutputStream(mediaOne);) {
                    fileOut.write(DEMO_DATA.getBytes());
                }
            }
        }
    }

    @AfterAll
    public static void removeDemoMediaFiles() {
        File file = new File(MEDIA_DIR);
        if (mediaOne != null && mediaOne.exists())
            mediaOne.delete();
        if (mediaTwo != null && mediaTwo.exists())
            mediaTwo.delete();
        if (file.listFiles().length == 0)
            file.delete();
    }

    @Test
    @DisplayName("Should return a response of an array of two media files for \"/media/amount\".")
    public void shouldReturnTwoMediaFiles(TestInfo testInfo) {
        given().when().get("/media/amount").then().statusCode(200).body(is("2"));
    }

    @Test
    @DisplayName("Should return partial content 206 response with correct Content-Length and Content-Range.")
    public void shouldReturnPartialContentResp(TestInfo testInfo) {
        given().param("filename", MEDIA_DIR + "/mediaOne.mp4").header("Range", "Bytes=0-1").when().get("/media").then()
                .statusCode(206).header("Content-Length", "2").and()
                .header("Content-Range", String.format("bytes %d-%d/%d", 0, 1, DEMO_DATA.getBytes().length));

        given().param("filename", MEDIA_DIR + "/mediaOne.mp4").header("Range", "Bytes=1-3").when().get("/media").then()
                .statusCode(206).header("Content-Length", "3").and()
                .header("Content-Range", String.format("bytes %d-%d/%d", 1, 3, DEMO_DATA.getBytes().length));
    }

    @Test
    @DisplayName("Should return OK 200 response for request without range.")
    public void shouldReturnOkResp(TestInfo testInfo) {
        given().param("filename", MEDIA_DIR + "/" + MEDIA_ONE).when().get("/media").then().statusCode(200);
    }

    @Test
    @DisplayName("Should return 206 response for HEAD request, and which contains \"Accept-Range: bytes\" header.")
    public void shouldReturnCorrectHeadResp(TestInfo testInfo) {
        given().param("filename", MEDIA_DIR + "/" + MEDIA_ONE).when().head("/media").then().assertThat().statusCode(206)
                .header("Accept-Ranges", "bytes");
    }
}
