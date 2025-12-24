import constans.Endpoints;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import serialization.ProfileUser;

import static io.restassured.RestAssured.given;

public class User {
    private final ProfileUser profile;
    Response response;

    public User(ProfileUser profile){
        this.profile=profile;
    }

    @Step("Send POST request to /api/auth/register")
    public Response createUser() {
        response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(profile)
                        .when()
                        .post(Endpoints.REGISTER);
        return response;
    }

    @Step("Send POST request to /api/auth/login")
    public Response logInUser(){
        response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(profile)
                        .when()
                        .post(Endpoints.LOGIN);
        return response;
    }

    public String getAToken(){
        return logInUser().body().path("accessToken").toString().split(" ")[1];
    }

    @Step("Send DELETE request to /api/auth/user")
    public void deleteUser() {
        given()
                .header("Content-type", "application/json")
                .auth().oauth2(getAToken())
                .when()
                .delete(Endpoints.USER);
    }
}
