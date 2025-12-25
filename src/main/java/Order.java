import constans.Endpoints;
import deserialization.Data;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;

public class Order {
    Response response;

    private final String bun;
    private final String sauce;
    private final String main;

    public Order(String bun, String sauce, String main) {
        this.bun = bun;
        this.sauce = sauce;
        this.main = main;
    }

    public List<String> getListIngredientHashes(){
        List<String> ingredients = new ArrayList<>();

        ingredients.add(getHashMapBuns().get(bun));
        ingredients.add(getHashMapSauce().get(sauce));
        ingredients.add(getHashMapMain().get(main));

        return ingredients;
    }


    @Step("Send GET request to /api/ingredients")
    public deserialization.Ingredients getIngredients(){

        return given()
        .when()
        .get(Endpoints.INGREDIENTS)
                .body().as(deserialization.Ingredients.class);
    }

    private HashMap<String, String> getHashMapBuns(){
        List<Data> data = getIngredients().getData();
        HashMap<String, String> hashBuns = new HashMap<>();

        for (Data element: data){
            if (element.getType().equals("bun")){
                hashBuns.put(element.getName(), element.getId());
            }
        }
        return  hashBuns;
    }

    private HashMap<String, String> getHashMapMain(){
        List<Data> data = getIngredients().getData();
        HashMap<String, String> hashMains = new HashMap<>();

        for (Data element: data){
            if (element.getType().equals("main")){
                hashMains.put(element.getName(), element.getId());
            }
        }
        return  hashMains;
    }

    private HashMap<String, String> getHashMapSauce(){
        List<Data> data = getIngredients().getData();
        HashMap<String, String> hashSauces = new HashMap<>();

        for (Data element: data){
            if (element.getType().equals("sauce")){
                hashSauces.put(element.getName(),element.getId());
            }
        }
        return hashSauces;
    }

    @Step("Send POST request to /api/orders")
    public Response createOrder(String aToken, serialization.Ingredients jsonIngredientHashes) {
        response = given()
                .header("Content-type", "application/json")
                .auth().oauth2(aToken)
                .and()
                .body(jsonIngredientHashes)
                .when()
                .post(Endpoints.ORDERS);
        return response;
    }


}
