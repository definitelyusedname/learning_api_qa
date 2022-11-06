package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userId;
    @BeforeEach
    public void login() {
        Map<String, String> data = new HashMap<>();
        data.put("email", "vinkotov@example.com");
        data.put("password", "1234");

        Response authResponse = RestAssured
                .given()
                .body(data)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        this.cookie = getCookie(authResponse, "auth_sid");
        this.header = getHeader(authResponse, "x-csrf-token");
        this.userId = getIntFromJson(authResponse, "user_id");

    }
    @Test
    public void testUserAuth() {
        Response checkAuthResponse = RestAssured
                .given()
                .header("x-csrf-token", this.header)
                .cookies("auth_sid",this.cookie)
                .get("https://playground.learnqa.ru/api/user/auth")
                .andReturn();
        Assertions.assertIntInJsonByName(checkAuthResponse,"user_id", this.userId);
    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie", "header"})
    public void negativeTestUserAuth(String condition) {

        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        if (condition.equals("cookie"))
            spec.cookie("auth_sid", this.cookie);
        else if (condition.equals("header"))
            spec.header("x-csrf-token", this.header);
        else
            throw new IllegalArgumentException("Condition value is unknown: " + condition);

        Response checkAuthResponse = spec.get().andReturn();
        Assertions.assertIntInJsonByName(checkAuthResponse, "user_id", 0);
    }
}
