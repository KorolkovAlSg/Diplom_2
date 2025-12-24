import constans.Constants;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import serialization.ProfileUser;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderCreationWithParamTest {
    Faker faker;

    Order order;
    ProfileUser profile;
    User user;
    serialization.Ingredients jsonIngredientHashes;

    private final String bun;
    private final String sauce;
    private final String main;

    public OrderCreationWithParamTest(String bun, String sauce, String main) {
        this.bun = bun;
        this.sauce = sauce;
        this.main = main;
    }
    @Parameterized.Parameters(name = "bun: {0} | sauce: {1} | main: {2}")
    public static Object[][] getInfo(){
        return new Object[][]{
                {"Флюоресцентная булка R2-D3", "Соус Spicy-X", "Мясо бессмертных моллюсков Protostomia"},
                {"Флюоресцентная булка R2-D3", "Соус Spicy-X", "Говяжий метеорит (отбивная)"},
                {"Краторная булка N-200i", "Соус фирменный Space Sauce", "Биокотлета из марсианской Магнолии"},
                {"Краторная булка N-200i", "Соус фирменный Space Sauce", "Филе Люминесцентного тетраодонтимформа"},
                {"Флюоресцентная булка R2-D3", "Соус традиционный галактический", "Хрустящие минеральные кольца"},
                {"Флюоресцентная булка R2-D3", "Соус традиционный галактический", "Плоды Фалленианского дерева"},
                {"Флюоресцентная булка R2-D3", "Соус с шипами Антарианского плоскоходца", "Кристаллы марсианских альфа-сахаридов"},
                {"Краторная булка N-200i", "Соус с шипами Антарианского плоскоходца", "Мини-салат Экзо-Плантаго"},
                {"Краторная булка N-200i", "Соус с шипами Антарианского плоскоходца", "Сыр с астероидной плесенью"},
        };
    }

    @Before
    public void setUp(){
        RestAssured.baseURI = Constants.URLFORTESTS;

        faker = new Faker();
        profile = new ProfileUser(faker.internet().emailAddress(),faker.internet().password(),faker.name().username());
        user = new User(profile);

        user.createUser();

        order = new Order(bun,sauce,main);

        jsonIngredientHashes = new serialization.Ingredients(order.getListIngredientHashes());
    }

    @Test
    @DisplayName("Create order with ingredients of /api/orders and checking the response body")
    @Description("Returns code 200 and response body {\"success\": true, \"name\":\"\", \"order\": {\"number\": \"\"}")
    public void createOrderReturnCode200RespBodySuccessTrueNameBurgerOrderNumber() {
        order.createOrder(user.getAToken(), jsonIngredientHashes)
                .then()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("name", not(emptyOrNullString()))
                .body("order.number", not(emptyOrNullString()))
                .body("success", equalTo(true));
    }

    @After
    public void clearTestsData() {
        user.deleteUser();
    }



}
