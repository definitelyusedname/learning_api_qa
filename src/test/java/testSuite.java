import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class testSuite {

    @Test
    public void getTextTest() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void getJsonHomeworkTest() {
        JsonPath response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        String answer = response.get("messages[1].message");
        System.out.println(answer);
    }

    @Test
    public void getLongRedirectEndTest() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(true)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();
        System.out.println(response.getHeader("X-Host"));
    }

    @Test
    public void getLongRedirectURLsTest() {

        int responseStatus = 0;
        int redirectCount = 0;
        String location = "https://playground.learnqa.ru/api/long_redirect";
        while (responseStatus != 200) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(location)
                    .andReturn();

            responseStatus = response.getStatusCode();
            if (responseStatus != 200) {
                location = response.getHeader("location");
                System.out.println("Redirected to: " + location);
                redirectCount++;
            }
        }
        System.out.println("Redirect count = " + redirectCount);
    }

    @Test
    public void getLongtimeJobTest() {

        JsonPath response1 = RestAssured
                .given()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        String token = response1.get("token");
        Integer seconds = response1.get("seconds");

        JsonPath response2 = RestAssured
                .given()
                .queryParam("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        assertEquals("Job is NOT ready", response2.get("status"));

        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonPath response3 = RestAssured
                .given()
                .queryParam("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        assertEquals("Job is ready", response3.get("status"));
        assertNotEquals((String) null, response3.get("result"));

    }
}
