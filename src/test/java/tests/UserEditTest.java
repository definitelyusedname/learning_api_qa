package tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testCreateUserAndEditData() {
        Map<String, String> userData = DataGenerator.getGenerationData();
        //Create
        JsonPath responseCreateUser = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateUser.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT
        String newName = "newName";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUserData = RestAssured
                .given()
                .header("x-csrf-token", getHeader(responseAuth, "x-csrf-token"))
                .cookies("auth_sid",this.getCookie(responseAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET DATA
        Response responseGetUserData = RestAssured
                .given()
                .header("x-csrf-token", getHeader(responseAuth, "x-csrf-token"))
                .cookies("auth_sid",this.getCookie(responseAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
        Assertions.assertStringInJsonByName(responseGetUserData, "firstName", newName);
    }

    @DisplayName("Test edit data without auth")
    @Description("This test checks that user data can not be changed without auth")
    @Test
    public void testEditUserDataWithoutAuth() {

        String newName = "newName";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseForEdit = apiCoreRequests.makePutRequestWithoutAuth(
                "https://playground.learnqa.ru/api/user/2",
                editData);
        Assertions.assertResponseStatusCodeEquals(responseForEdit, 400);
        Assertions.assertResponseTextEquals(responseForEdit,"Auth token not supplied");
    }

    @DisplayName("Test edit data of another user")
    @Description("This test checks that user can not change data of other users")
    @Test
    public void testEditAnotherUserData() {
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

        //Edit another user
        String newName = "newName";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", newName);
        Response responseForEdit = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/2",
                header,
                cookie,
                editData);

        //Check username of another user
        Response responseGetDataOfDifferentUser = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/2",
                header,
                cookie);
        responseGetDataOfDifferentUser.print();
        Assertions.assertStringInJsonByName(responseGetDataOfDifferentUser,"username", "Vitaliy");
    }

    @DisplayName("Test edit invalid email")
    @Description("This test checks that user can not set email without '@' symbol")
    @Test
    public void testEditUserEmailInvalid() {
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

        //Edit email user
        Map<String,String> editData = new HashMap<String, String>();
        editData.put("email", authData.get("email").replace("@", ""));
        Response responseForEdit = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/"+userId+"",
                header,
                cookie,
                editData);
        Assertions.assertResponseStatusCodeEquals(responseForEdit, 400);
        Assertions.assertResponseTextEquals(responseForEdit, "Invalid email format");
    }

    @DisplayName("Test edit invalid firstname")
    @Description("This test checks that user can not set email without '@' symbol")
    @Test
    public void testEditFirstNameInvalid() {
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

        //Edit email user
        Map<String,String> editData = new HashMap<String, String>();
        editData.put("firstName", "a");
        Response responseForEdit = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/"+userId+"",
                header,
                cookie,
                editData);
        Assertions.assertResponseStatusCodeEquals(responseForEdit, 400);
        Assertions.assertStringInJsonByName(responseForEdit, "error","Too short value for field firstName");
    }
}
