package tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

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

    @DisplayName("Test get data of different user")
    @Description("This test checks that user can see only username of other users")
    @Test
    public void testGetUserDataOfDifferentUser() {
        Map<String, String> userData = DataGenerator.getGenerationData();

        Response createUserResponse = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData);

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response authResponse = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);

        String cookie = getCookie(authResponse, "auth_sid");
        String header = getHeader(authResponse, "x-csrf-token");

        Response responseForDifferentUser = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/2",
                header,
                cookie);
        String[] unexpectedFields = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasField(responseForDifferentUser, "username");
        Assertions.assertJsonHasNotFields(responseForDifferentUser, unexpectedFields);
    }
}
