package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Authorization cases")
@Feature("Authorization")
public class UserAuthTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    String cookie;
    String header;
    int userId;
    @Step("Login")
    @BeforeEach
    public void login() {
        Map<String, String> data = new HashMap<>();
        data.put("email", "vinkotov@example.com");
        data.put("password", "1234");

        Response authResponse = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login", data);
        this.cookie = this.getCookie(authResponse, "auth_sid");
        this.header = this.getHeader(authResponse, "x-csrf-token");
        this.userId = this.getIntFromJson(authResponse, "user_id");
    }

    @DisplayName("Test positive auth")
    @Description("This tests checks auth status after sending cookie and token")
    @Test
    public void testUserAuth() {
        Response checkAuthResponse = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/auth",
                this.header,
                this.cookie);
        Assertions.assertIntInJsonByName(checkAuthResponse, "user_id", this.userId);
    }

    @DisplayName("Test negative auth")
    @Description("This tests checks auth status without sending cookie or token")
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "header"})
    public void negativeTestUserAuth(String condition) {

        if (condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(
                    "https://playground.learnqa.ru/api/user/auth",
                    this.cookie);
            Assertions.assertIntInJsonByName(responseForCheck, "user_id", 0);
        } else if (condition.equals("header")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(
                    "https://playground.learnqa.ru/api/user/auth",
                    this.header);
            Assertions.assertIntInJsonByName(responseForCheck, "user_id", 0);
        } else {
            throw new IllegalArgumentException("Condition value is unknown: " + condition);
        }
    }
}
