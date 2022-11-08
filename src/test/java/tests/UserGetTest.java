package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {

    @Test
    public void testGetUserDataWithoutAuth() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        Assertions.assertJsonHasField(response, "username");
        Assertions.assertJsonHasNotField(response, "firstName");
        Assertions.assertJsonHasNotField(response, "lastName");
        Assertions.assertJsonHasNotField(response, "email");
    }

    @Test
    public void testGetUserDataWithSameUser() {
        Map<String, String> data = new HashMap<>();
        data.put("email", "vinkotov@example.com");
        data.put("password", "1234");

        Response authResponse = RestAssured
                .given()
                .body(data)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        String cookie = getCookie(authResponse, "auth_sid");
        String header = getHeader(authResponse, "x-csrf-token");

        Response response = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookies("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        String[] expectedFields = {"username", "firstName", "lastName", "email"};

        Assertions.assertJsonHasFields(response, expectedFields);
    }

}
