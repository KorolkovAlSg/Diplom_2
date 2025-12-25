import constans.Constants;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import serialization.ProfileUser;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class UserCreationTest {

    Faker faker;
    ProfileUser profile;
    User user;


    @Before
    public void setUp(){
        faker = new Faker();
        RestAssured.baseURI = Constants.URLFORTESTS;
        profile = new ProfileUser(faker.internet().emailAddress(),faker.internet().password(),faker.name().username());
        user = new User(profile);
    }

    @Test
    @DisplayName("Create unique user of /api/auth/register and checking the response body")
    @Description("Returns code 200 and response body {\"success\": true, \"user\": { \"email\":\"\", \"name\": \"\"}, \"accessToken\": \"Bearer ...\", \"refreshToken\": \"\"}")
    public void createNewUserReturnCode200RespBodySuccessTrueUserInfoAndTokens() {
        user.createUser()
                .then()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(profile.getEmail()))
                .body("user.name", equalTo(profile.getName()))
                .body("accessToken", not(emptyOrNullString()))
                .body("user.name", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("Create similar user of /api/auth/register and checking the response body")
    @Description("Returns code 403 and response body {\"success\": false, \"message\": \"User already exists\"}")
    public void createSimilarUserCode403RespBodySuccessFalseAndErrMessage() {
        user.createUser();
        user.createUser()
                .then()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Creating a user without a email of /api/auth/register")
    @Description("Returns code 403 and response body {\"success\": false, \"message\": \"Email, password and name are required fields\"}")
    public void createNewUserWithoutEmailParam403ErrorResponseMessage(){
        profile.setEmail(null);

        user.createUser()
                .then()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Creating a user without a password of /api/auth/register")
    @Description("Returns code 403 and response body {\"success\": false, \"message\": \"Email, password and name are required fields\"}")
    public void createNewUserWithoutPassParam403ErrorResponseMessage(){
        profile.setPassword(null);

        user.createUser()
                .then()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Creating a user without a name of /api/auth/register")
    @Description("Returns code 403 and response body {\"success\": false, \"message\": \"Email, password and name are required fields\"}")
    public void createNewUserWithoutNameParam403ErrorResponseMessage(){
        profile.setName(null);

        user.createUser()
                .then()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void clearTestsData() {
        if (profile.getEmail() != null
                && profile.getPassword() != null
                && profile.getName() != null) {
            user.deleteUser();
        }
    }
}
