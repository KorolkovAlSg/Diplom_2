import constans.Constants;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import serialization.ProfileUser;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class OrderCreationTest {
    Faker faker;

    Order order;
    ProfileUser profile;
    User user;

    serialization.Ingredients jsonIngredientHashes;

    @Before
    public void setUp(){
        RestAssured.baseURI = Constants.URLFORTESTS;

        faker = new Faker();
        profile = new ProfileUser(faker.internet().emailAddress(),faker.internet().password(),faker.name().username());
        user = new User(profile);

        user.createUser();

        order = new Order("Краторная булка N-200i", "Соус с шипами Антарианского плоскоходца", "Мини-салат Экзо-Плантаго");
        jsonIngredientHashes = new serialization.Ingredients(order.getListIngredientHashes());
    }

    @Test
    @DisplayName("Create order with an authorized user and with ingredients of /api/orders and checking the response body")
    @Description("Returns code 200 and response body {\"success\": true, \"name\":\"\", \"order\": {\"number\": \"\"}")
    public void createOrderWithAuthorizedUserAndIngredients() {
        order.createOrder(user.getAToken(), jsonIngredientHashes)
                .then()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("name", not(emptyOrNullString()))
                .body("order", not(emptyOrNullString()))
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Create order with an authorized user and WITHOUT ingredients of /api/orders and checking the response body")
    @Description("Returns code 400 and response body {\"success\": false, \"message\": \"Ingredient ids must be provided\"}")
    public void createOrderWithAuthorizedUserAndWithoutIngredients() {
        order = new Order(null,null,null);
        jsonIngredientHashes = new serialization.Ingredients(order.getListIngredientHashes());

        order.createOrder(user.getAToken(), jsonIngredientHashes)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Create order with an authorized user and with INCORRECT HASH ingredients of /api/orders and checking the response body")
    @Description("Returns code 500")
    public void createOrderWithAuthorizedUserAndIncorrectHash() {
        jsonIngredientHashes.setIngredients(List.of("incorrectHash_:)"));

        order.createOrder(user.getAToken(), jsonIngredientHashes)
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Create order with an UNAUTHORIZED user and with ingredients of /api/orders and checking the response body")
    @Description("Returns code 401 and response body {\"success\": false, \"message\": \"email or password are incorrect\"}")
    public void createOrderWithUnauthorizedUserAndIngredients() {
        order.createOrder("", jsonIngredientHashes)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Create order with an UNAUTHORIZED user and WITHOUT ingredients of /api/orders and checking the response body")
    @Description("Returns code 401 and response body {\"success\": false, \"message\": \"email or password are incorrect\"}")
    public void createOrderWithUnauthorizedUserAndWithoutIngredients() {
        order = new Order(null,null,null);
        jsonIngredientHashes = new serialization.Ingredients(order.getListIngredientHashes());

        order.createOrder("", jsonIngredientHashes)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Create order with an UNAUTHORIZED user and with INCORRECT HASH ingredients of /api/orders and checking the response body")
    @Description("Returns code 500")
    public void createOrderWithUnauthorizedUserAndIncorrectHash() {
        jsonIngredientHashes.setIngredients(List.of("incorrectHash_:)"));

        order.createOrder("", jsonIngredientHashes)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void clearTestsData() {
        user.deleteUser();
    }
}
