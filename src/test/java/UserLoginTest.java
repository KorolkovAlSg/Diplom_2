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

public class UserLoginTest {

    Faker faker;

    ProfileUser profile;
    User user;

    private String oldPass;
    private String oldEmail;
    private String oldName;

    @Before
    public void setUp() {
        faker = new Faker();
        RestAssured.baseURI = Constants.URLFORTESTS;
        profile = new ProfileUser(faker.internet().emailAddress(), faker.internet().password(), faker.name().username());
        user = new User(profile);

        oldPass = profile.getPassword();
        oldEmail = profile.getEmail();
        oldName = profile.getName();

        user.createUser();
    }

    @Test
    @DisplayName("Log In user of /api/auth/login")
    @Description("Returns code 200 and response body {\"success\": true, \"user\": { \"email\":\"\", \"name\": \"\"}, \"accessToken\": \"Bearer ...\", \"refreshToken\": \"\"}")
    public void userLogInCode200RespBodySuccessTrueUserInfoAndTokens(){
        user.logInUser()
                .then().statusCode(SC_OK)
                .and()
                .assertThat()
                .body("success", equalTo(true))
                .body("accessToken", not(emptyOrNullString()))
                .body("refreshToken", not(emptyOrNullString()))
                .body("user.email", equalTo(profile.getEmail()))
                .body("user.name", equalTo(profile.getName()));
    }

    @Test
    @DisplayName("Log In user with incorrect/null email of /api/auth/login")
    @Description("Returns code 401 and response body {\"success\": false, \"message\": \"email or password are incorrect\"}")
    public void loginUserWithIncorrectEmailParam401ErrorResponseMessage(){
        profile.setEmail(faker.internet().emailAddress());

        user.logInUser()
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Log In user with incorrect/null password of /api/auth/login")
    @Description("Returns code 401 and response body {\"success\": false, \"message\": \"email or password are incorrect\"}")
    public void loginUserWithIncorrectPasswordParam401ErrorResponseMessage(){
        profile.setPassword(faker.internet().password());

        user.logInUser()
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void clearTestsData() {
        profile.setEmail(oldEmail);
        profile.setName(oldName);
        profile.setPassword(oldPass);

        user.deleteUser();
    }

}
