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
@Feature("Получение истории заказов")
@Story("Ручка GET /api/orders")
@DisplayName("Получение списка заказов пользователя")
public class GetUserOrdersTest {

    private AuthApi authApi;
    private OrderApi orderApi;
    private String accessToken;

    private static final String VALID_INGREDIENT_1 = "61c0c5a71d1f82001bdaaa6d";
    private static final String VALID_INGREDIENT_2 = "61c0c5a71d1f82001bdaaa6f";

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
    @DisplayName("Получение списка заказов авторизованным пользователем")
    void getUserOrdersWithAuthTest() {
        List<String> ingredients = Arrays.asList(VALID_INGREDIENT_1, VALID_INGREDIENT_2);
        Order order = new Order(ingredients);
        orderApi.createOrderWithAuth(accessToken, order);

        Response response = orderApi.getUserOrders(accessToken);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("orders", notNullValue())
                .body("orders", hasSize(greaterThan(0)))
                .body("orders[0].number", notNullValue())
                .body("orders[0].ingredients", hasSize(greaterThan(0)));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Получение заказов без авторизации")
    void getUserOrdersWithoutAuthTest() {
        Response response = orderApi.getUserOrdersWithoutAuth();

        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Проверка, что пользователь видит только свои заказы")
    void getUserOrdersSeesOnlyOwnOrdersTest() {
        List<String> ingredients = Collections.singletonList(VALID_INGREDIENT_1);
        Order myOrder = new Order(ingredients);
        orderApi.createOrderWithAuth(accessToken, myOrder);

        // Регистрируем ВТОРОГО пользователя и создаем ему заказ
        String uniqueEmail2 = "user2" + System.currentTimeMillis() + "@mail.ru";
        User user2 = new User(uniqueEmail2, "12345678", "TestName2");
        Response registerResponse2 = authApi.register(user2);
        String token2 = registerResponse2.jsonPath().getString("accessToken");

        Order otherOrder = new Order(ingredients);
        orderApi.createOrderWithAuth(token2, otherOrder);

        // Удаляем второго пользователя
        authApi.deleteUser(token2);

        // Первый пользователь запрашивает свои заказы
        Response response = orderApi.getUserOrders(accessToken);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("orders", notNullValue())
                .body("orders", hasSize(greaterThan(0)));

        System.out.println("Список заказов пользователя: " + response.asString());
    }
}
