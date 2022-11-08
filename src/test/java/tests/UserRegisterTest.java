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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    public void createUserWithExistingEmail() {
        String email = "vinkotov@example.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getGenerationData(userData);

        Response response = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Users with email '" + email + "' already exists");
        System.out.println(response.statusCode());
    }

    @Test
    public void createNewUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getGenerationData();

        Response response = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(response, 200);
        Assertions.assertJsonHasField(response, "id");
    }

    @DisplayName("Test user creation with invalid email")
    @Description("This test checks that user is not created if email is not valid")
    @Test
    public void failCreateUserWithInvalidEmail(){
        Map<String, String> userData = DataGenerator.getGenerationData();
        userData.put("email", DataGenerator.getRandomEmail().replace("@",""));
        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData);
        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Invalid email format");
    }

    @DisplayName("Test user creation with missing field")
    @Description("This test checks that user is not created if a field is not present")
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void failCreateUserWithMissingField(String missingFieldName){
        Map<String, String> userData = DataGenerator.getGenerationData();
        userData.remove(missingFieldName);
        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData);
        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The following required params are missed: "+missingFieldName+"");
    }

    @DisplayName("Test user creation with a short name ")
    @Description("This test checks that user is not created if username is too short")
    @Test
    public void failCreateUserWithShortName(){
        Map<String, String> userData = DataGenerator.getGenerationData();
        userData.put("username", "s");
        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData);
        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'username' field is too short");
    }

    @DisplayName("Test user creation with a long name ")
    @Description("This test checks that user is not created if username is too long")
    @Test
    public void failCreateUserWithLongName(){
        Map<String, String> userData = DataGenerator.getGenerationData();
        userData.put("username", Arrays.toString(new int[255]));
        System.out.println(userData);
        Response response = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData);
        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'username' field is too long");
    }
}