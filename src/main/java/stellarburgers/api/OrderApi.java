package stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import stellarburgers.dto.Order;

import static io.restassured.RestAssured.given;
import static stellarburgers.config.ApiConfig.BASE_URL;

public class OrderApi {

    @Step("Создание заказа с авторизацией")
    public Response createOrderWithAuth(String accessToken, Order order) {
        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Создание заказа без авторизации")
    public Response createOrderWithoutAuth(Order order) {
        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Получение заказов авторизованного пользователя")
    public Response getUserOrders(String accessToken) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", accessToken)
                .when()
                .get("/api/orders");
    }

    @Step("Получение заказов неавторизованного пользователя")
    public Response getUserOrdersWithoutAuth() {
        return given()
                .baseUri(BASE_URL)
                .when()
                .get("/api/orders");
    }
}