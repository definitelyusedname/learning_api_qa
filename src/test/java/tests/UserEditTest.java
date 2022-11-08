package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {

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
}
