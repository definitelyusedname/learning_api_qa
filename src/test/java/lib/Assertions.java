package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    public static void assertIntInJsonByName(Response response, String name, int expectedValue) {
        response.then().assertThat().body("$", hasKey(name));

        int value = response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "Value in JSON is different from expected");
    }

    public static void assertStringInJsonByName(Response response, String name, String expectedValue) {
        response.then().assertThat().body("$", hasKey(name));

        String value = response.jsonPath().getString(name);
        assertEquals(expectedValue, value, "Value of '"+ name +"' in JSON is different from expected");
    }
}
