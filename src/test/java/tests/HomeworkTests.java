package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class HomeworkTests extends BaseTestCase {

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

    @Test
    public void findPasswordTest() {
        List<String> passList = Stream.of("password", "password", "123456", "123456", "123456", "123456", "123456", "123456", "123456",
                "123456", "123456", "password", "password", "password", "password", "password", "password", "123456789",
                "12345678", "12345678", "12345678", "12345", "12345678", "12345", "12345678", "123456789", "qwerty",
                "qwerty", "abc123", "qwerty", "12345678", "qwerty", "12345678", "qwerty", "12345678", "password",
                "abc123", "qwerty", "abc123", "qwerty", "12345", "football", "12345", "12345", "1234567",
                "monkey", "monkey", "123456789", "123456789", "123456789", "qwerty", "123456789", "111111", "12345678",
                "1234567", "letmein", "111111", "1234", "football", "1234567890", "letmein", "1234567", "12345",
                "letmein", "dragon", "1234567", "baseball", "1234", "1234567", "1234567", "sunshine", "iloveyou",
                "trustno1", "111111", "iloveyou", "dragon", "1234567", "princess", "football", "qwerty", "111111",
                "dragon", "baseball", "adobe123", "football", "baseball", "1234", "iloveyou", "iloveyou", "123123",
                "baseball", "iloveyou", "123123", "1234567", "welcome", "login", "admin", "princess", "abc123",
                "111111", "trustno1", "admin", "monkey", "1234567890", "welcome", "welcome", "admin", "qwerty123",
                "iloveyou", "1234567", "1234567890", "letmein", "abc123", "solo", "monkey", "welcome", "1q2w3e4r",
                "master", "sunshine", "letmein", "abc123", "111111", "abc123", "login", "666666", "admin",
                "sunshine", "master", "photoshop", "111111", "1qaz2wsx", "admin", "abc123", "abc123", "qwertyuiop",
                "ashley", "123123", "1234", "mustang", "dragon", "121212", "starwars", "football", "654321",
                "bailey", "welcome", "monkey", "access", "master", "flower", "123123", "123123", "555555",
                "passw0rd", "shadow", "shadow", "shadow", "monkey", "passw0rd", "dragon", "monkey", "lovely",
                "shadow", "ashley", "sunshine", "master", "letmein", "dragon", "passw0rd", "654321", "7777777",
                "123123", "football", "12345", "michael", "login", "sunshine", "master", "!@#$%^&*", "welcome",
                "654321", "jesus", "password1", "superman", "princess", "master", "hello", "charlie", "888888",
                "superman", "michael", "princess", "696969", "qwertyuiop", "hottie", "freedom", "aa123456", "princess",
                "qazwsx", "ninja", "azerty", "123123", "solo", "loveme", "whatever", "donald", "dragon",
                "michael", "mustang", "trustno1", "batman", "passw0rd", "zaq1zaq1", "qazwsx", "password1", "password1",
                "Football", "password1", "000000", "trustno1", "starwars", "password1", "trustno1", "qwerty123", "123qwe").distinct().collect(Collectors.toList());


        Map<String, String> body = new HashMap<>();
        body.put("login", "super_admin");
        for (String pass :
                passList) {
            body.put("password", pass);
            Response authResponse = RestAssured
                    .given()
                    .body(body)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            Map<String, String> cookies = authResponse.getCookies();

            Response checkResponse = RestAssured
                    .given()
                    .cookies(cookies)
                    .when()
                    .get("https://playground.learnqa.ru/api/check_auth_cookie")
                    .andReturn();

            if (checkResponse.asString().contains("You are authorized")) {
                checkResponse.print();
                System.out.println("Correct password is: \"" + pass + "\"");
                break;
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"this is a long string", "this is short", "this is 15 char"})
    public void checkStringLengthTest(String check) {
        if (check.isEmpty())
            System.out.println("Input string is empty");
        else
            assertTrue(check.length() > 15, "Input string is less than 15 characters");
    }

    @Test
    public void homeworkCookieTest() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        String cookie = getCookie(response, "HomeWork");
        assertEquals("hw_value", cookie, "Unexpected value in cookie");
    }

    @Test
    public void homeworkHeaderTest() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        String header = getHeader(response, "x-secret-homework-header");
        assertEquals("Some secret value", header, "Header value is different");
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWe`bKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30|Mobile|No|Android",
            "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1|Mobile|Chrome|iOS",
            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)|Googlebot|Unknown|Unknown",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0|Web|Chrome|No",
            "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1|Mobile|No|iPhone"}, delimiter = '|')
    public void userAgentCheckTest(String userAgentValue, String platformExpected, String browserExpected, String deviceExpected) {
        Response response = RestAssured
                .given()
                .header("user-agent", userAgentValue)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .andReturn();
        Assertions.assertStringInJsonByName(response, "platform", platformExpected);
        Assertions.assertStringInJsonByName(response, "browser", browserExpected);
        Assertions.assertStringInJsonByName(response, "device", deviceExpected);
    }
}
