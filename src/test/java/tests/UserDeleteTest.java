package tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @DisplayName("Test delete default user")
    @Description("This test checks that default user can not be deleted")
    @Test
    public void testDeleteDefaultUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response authResponse = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);

        String cookie = getCookie(authResponse, "auth_sid");
        String header = getHeader(authResponse, "x-csrf-token");

        Response responseForDelete = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/2",
                header,
                cookie);
        Assertions.assertResponseStatusCodeEquals(responseForDelete, 400);
        Assertions.assertResponseTextEquals(responseForDelete, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @DisplayName("Test create and delete user")
    @Description("This test checks that user can delete himself")
    @Test
    public void testCreateAndDeleteNewUser() {
        //Create new user
        Map<String, String> userData = DataGenerator.getGenerationData();
        Response createUserResponse = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData);

        //Login as new user
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response authResponse = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);

        String cookie = getCookie(authResponse, "auth_sid");
        String header = getHeader(authResponse, "x-csrf-token");
        int userId = getIntFromJson(authResponse, "user_id");

        //Delete user
        Response responseForDelete = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie);

        //Check username of another user
        Response responseForUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie);
        Assertions.assertResponseStatusCodeEquals(responseForUserData, 404);
        Assertions.assertResponseTextEquals(responseForUserData, "User not found");
    }

    @DisplayName("Test create and delete user by other user")
    @Description("This test checks that user can not delete others")
    @Test
    public void testCreateAndDeleteNewUserByAnotherUser() {
        //Create new user
        Map<String, String> userData = DataGenerator.getGenerationData();
        Response createUserResponse = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData);
        int userId = getIntFromJson(createUserResponse, "id");
        createUserResponse.print();


        //Login as new user
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response authResponse = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);

        String cookie = getCookie(authResponse, "auth_sid");
        String header = getHeader(authResponse, "x-csrf-token");

        //Delete user
        Response responseForDelete = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie);

        Assertions.assertResponseStatusCodeEquals(responseForDelete, 400);
        Assertions.assertResponseTextEquals(responseForDelete, "Invalid auth");
    }
}
