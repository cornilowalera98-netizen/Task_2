package stellarburgers.api;

import io.restassured.response.Response;
import stellarburgers.dto.IngredientsResponse;

import static io.restassured.RestAssured.given;

public class IngredientsApi {

    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    public IngredientsResponse getIngredients() {
        Response response = given()
                .get(BASE_URL + "/ingredients")
                .then()
                .statusCode(200)
                .extract().response();

        return response.as(IngredientsResponse.class);
    }

    public String getValidIngredientId() {
        IngredientsResponse response = getIngredients();
        return response.getData().get(0).get_id();
    }
}
