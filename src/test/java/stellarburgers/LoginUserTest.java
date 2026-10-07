package stellarburgers;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stellarburgers.api.AuthApi;
import stellarburgers.dto.User;

import static org.hamcrest.Matchers.*;

@Epic("Авторизация")
@Feature("Вход в систему")
@DisplayName("Тесты авторизации пользователя (Login)")
public class LoginUserTest {

    private AuthApi authApi;
    private User validUser;
    private String accessToken;
    private static final String VALID_PASSWORD = "12345678";

    @BeforeEach
    void setUp() {
        authApi = new AuthApi();
        String uniqueEmail = "test_user_" + System.currentTimeMillis() + "@mail.ru";
        validUser = new User(uniqueEmail, VALID_PASSWORD, "Test Name");

        Response registerResponse = authApi.register(validUser);
        // Сохраняем токен сразу при регистрации — он нужен для tearDown
        accessToken = registerResponse.jsonPath().getString("accessToken");
        registerResponse.then().statusCode(200);
    }

    @AfterEach
    void tearDown() {
        if (accessToken != null) {
            authApi.deleteUser(accessToken);
        }
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Успешная авторизация с корректными данными")
    void loginSuccessTest() {
        Response response = authApi.login(validUser);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue())
                .body("user.email", equalTo(validUser.getEmail()))
                .body("user.name", equalTo(validUser.getName()));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Ошибка авторизации: неверный пароль")
    void loginWithWrongPasswordTest() {
        User wrongPasswordUser = new User(validUser.getEmail(), "wrong_password_123", validUser.getName());

        Response response = authApi.login(wrongPasswordUser);

        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", containsString("password"));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Ошибка авторизации: несуществующий email")
    void loginWithNonExistingEmailTest() {
        User nonExistingUser = new User("not_registered_user@mail.ru", VALID_PASSWORD, "Fake Name");

        Response response = authApi.login(nonExistingUser);

        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", notNullValue());
    }
}
