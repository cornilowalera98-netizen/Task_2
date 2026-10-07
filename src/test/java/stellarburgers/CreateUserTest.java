package stellarburgers;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stellarburgers.api.AuthApi;
import stellarburgers.dto.User;

import static org.hamcrest.Matchers.*;

@Epic("Регистрация пользователя")
@Feature("Создание пользователя")
@Story("Ручка POST /api/auth/register")
@DisplayName("Создание пользователя")
public class CreateUserTest {

    private AuthApi authApi;
    private String accessToken;

    @BeforeEach
    void setUp() {
        authApi = new AuthApi();
    }

    @AfterEach
    void tearDown() {
        if (accessToken != null) {
            authApi.deleteUser(accessToken);
        }
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание уникального пользователя")
    void createUniqueUserTest() {
        String uniqueEmail = "user" + System.currentTimeMillis() + "@mail.ru";
        User user = new User(uniqueEmail, "password123", "TestName");

        Response response = authApi.register(user);
        accessToken = response.jsonPath().getString("accessToken");

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", is(uniqueEmail))
                .body("user.name", is("TestName"));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание пользователя с уже зарегистрированным email")
    void createExistingUserTest() {
        String uniqueEmail = "user" + System.currentTimeMillis() + "@mail.ru";
        User user = new User(uniqueEmail, "password123", "TestName");

        // Первая регистрация — успешная, сохраняем токен для удаления
        Response firstResponse = authApi.register(user);
        accessToken = firstResponse.jsonPath().getString("accessToken");

        // Вторая регистрация — должна провалиться
        Response response = authApi.register(user);

        response.then()
                .statusCode(anyOf(is(400), is(403)))
                .body("success", is(false));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание пользователя без обязательного поля (email)")
    void createUserWithoutRequiredFieldTest() {
        User user = new User(null, "password123", "TestName");

        Response response = authApi.register(user);

        response.then()
                .statusCode(anyOf(is(400), is(403)))
                .body("success", is(false));
    }
}
