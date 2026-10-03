package stellarburgers;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stellarburgers.api.AuthApi;
import stellarburgers.api.OrderApi;
import stellarburgers.dto.Order;
import stellarburgers.dto.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;

@Epic("Заказы")
@Feature("Создание заказа")
@Story("Ручка POST /api/orders")
@DisplayName("Создание заказа")
public class CreateOrderTest {

    private AuthApi authApi;
    private OrderApi orderApi;
    private String accessToken;

    private static final String VALID_INGREDIENT_1 = "61c0c5a71d1f82001bdaaa6d";
    private static final String VALID_INGREDIENT_2 = "61c0c5a71d1f82001bdaaa6f";
    private static final String INVALID_HASH = "000000000000000000000000";

    @BeforeEach
    void setUp() {
        authApi = new AuthApi();
        orderApi = new OrderApi();

        String uniqueEmail = "user" + System.currentTimeMillis() + "@mail.ru";
        User user = new User(uniqueEmail, "12345678", "TestName");

        Response registerResponse = authApi.register(user);
        accessToken = registerResponse.jsonPath().getString("accessToken");
    }

    @AfterEach
    void tearDown() {
        if (accessToken != null) {
            authApi.deleteUser(accessToken);
        }
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами")
    void createOrderWithAuthTest() {
        List<String> ingredients = Arrays.asList(VALID_INGREDIENT_1, VALID_INGREDIENT_2);
        Order order = new Order(ingredients);

        Response response = orderApi.createOrderWithAuth(accessToken, order);

        // Проверяем строго по документации
        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("name", not(emptyOrNullString()))
                .body("order.number", notNullValue());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание заказа без авторизации (анонимный заказ)")
    void createOrderWithoutAuthTest() {
        List<String> ingredients = Collections.singletonList(VALID_INGREDIENT_1);
        Order order = new Order(ingredients);

        Response response = orderApi.createOrderWithoutAuth(order);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("name", not(emptyOrNullString()))
                .body("order.number", notNullValue());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание заказа без ингредиентов")
    void createOrderWithoutIngredientsTest() {
        Order order = new Order(Collections.emptyList());

        Response response = orderApi.createOrderWithAuth(accessToken, order);

        response.then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", containsString("Ingredient ids must be provided"));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание заказа с неверным хешем ингредиента")
    void createOrderWithWrongHashTest() {
        List<String> ingredients = Collections.singletonList(INVALID_HASH);
        Order order = new Order(ingredients);

        Response response = orderApi.createOrderWithAuth(accessToken, order);

        response.then()
                .statusCode(anyOf(is(400), is(500)));
    }
}
