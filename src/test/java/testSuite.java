import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

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
            if(responseStatus != 200) {
                location = response.getHeader("location");
                System.out.println("Redirected to: " + location);
                redirectCount++;
            }
        }
        System.out.println("Redirect count = " + redirectCount);
    }

}
