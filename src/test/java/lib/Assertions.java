package lib;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    @Step("Assert JSON has '{int}' in {str} field")
    public static void assertIntInJsonByName(Response response, String name, int expectedValue) {
        response.then().assertThat().body("$", hasKey(name));

        int value = response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "Value in JSON is different from expected");
    }
    public static void assertStringInJsonByName(Response response, String name, String expectedString) {
        response.then().assertThat().body("$", hasKey(name));

        String value = response.jsonPath().getString(name);
        assertEquals(expectedString, value, "Value of '"+ name +"' in JSON is different from expected");
    }
    public static void assertJsonHasField(Response response, String expectedField) {
        response.then().assertThat().body("$", hasKey(expectedField));
    }

    public static void assertJsonHasFields(Response response, String[] expectedFields) {
        for (String expectedField:
             expectedFields) {
            assertJsonHasField(response, expectedField);
        }
    }
    public static void assertJsonHasNotField(Response response, String unexpectedField) {
        response.then().assertThat().body("$", not(hasKey(unexpectedField)));
    }



    public static void assertResponseTextEquals(Response response, String expectedResponseText) {
        assertEquals(
                expectedResponseText,
                response.asString(),
                "Response text is different from expected"
        );
    }
    public static void assertResponseCodeEquals(Response response, int expectedStatusCode) {
        assertEquals(
                expectedStatusCode,
                response.statusCode(),
                "Response code is different from expected"
        );
    }
}
