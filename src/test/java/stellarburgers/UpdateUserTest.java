package stellarburgers;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stellarburgers.api.AuthApi;
import stellarburgers.api.UserApi;
import stellarburgers.dto.User;

import static org.hamcrest.Matchers.*;

@Epic("Пользователи")
@Feature("Управление профилем пользователя")
@DisplayName("Тесты обновления данных пользователя (Update)")
public class UpdateUserTest {

    private UserApi userApi;
    private AuthApi authApi;
    private String accessToken;
    private User registeredUser;
    private static final String VALID_PASSWORD = "12345678";

    @BeforeEach
    void setUp() {
        userApi = new UserApi();
        authApi = new AuthApi();

        String uniqueEmail = "test_user_" + System.currentTimeMillis() + "@mail.ru";
        registeredUser = new User(uniqueEmail, VALID_PASSWORD, "Test Name");

        Response registerResponse = authApi.register(registeredUser);
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
    @Story("Изменение имени пользователя")
    @DisplayName("Успешное обновление имени пользователя")
    void updateUserNameSuccessTest() {
        String newName = "Updated Name " + System.currentTimeMillis();

        User updatedUser = new User(registeredUser.getEmail(), VALID_PASSWORD, newName);

        userApi.updateUserWithAuth(accessToken, updatedUser)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.name", equalTo(newName))
                .body("user.email", equalTo(registeredUser.getEmail()));
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Изменение email пользователя")
    @DisplayName("Успешное обновление email пользователя")
    void updateUserEmailSuccessTest() {
        String newEmail = "new_email_" + System.currentTimeMillis() + "@mail.ru";

        User updatedUser = new User(newEmail, VALID_PASSWORD, registeredUser.getName());

        userApi.updateUserWithAuth(accessToken, updatedUser)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(registeredUser.getName()));
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Изменение пароля пользователя")
    @DisplayName("Успешное обновление пароля пользователя")
    void updateUserPasswordSuccessTest() {
        String newPassword = "new_password_456";

        User updatedUser = new User(registeredUser.getEmail(), newPassword, registeredUser.getName());

        userApi.updateUserWithAuth(accessToken, updatedUser)
                .then()
                .statusCode(200)
                .body("success", is(true));

        // Проверяем, что с новым паролем можно залогиниться
        User loginWithNewPassword = new User(registeredUser.getEmail(), newPassword, registeredUser.getName());
        authApi.login(loginWithNewPassword)
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Безопасность API")
    @DisplayName("Ошибка обновления без токена авторизации: изменение имени")
    void updateUserWithoutTokenNameTest() {
        String newName = "Hacker Name";
        User updatedUser = new User(registeredUser.getEmail(), VALID_PASSWORD, newName);

        userApi.updateUserWithoutAuth(updatedUser)
                .then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", containsString("authorised"));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Безопасность API")
    @DisplayName("Ошибка обновления без токена авторизации: изменение email")
    void updateUserWithoutTokenEmailTest() {
        String newEmail = "hacker_" + System.currentTimeMillis() + "@mail.ru";
        User updatedUser = new User(newEmail, VALID_PASSWORD, registeredUser.getName());

        userApi.updateUserWithoutAuth(updatedUser)
                .then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", containsString("authorised"));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Безопасность API")
    @DisplayName("Ошибка обновления без токена авторизации: изменение пароля")
    void updateUserWithoutTokenPasswordTest() {
        User updatedUser = new User(registeredUser.getEmail(), "hacker_password", registeredUser.getName());

        userApi.updateUserWithoutAuth(updatedUser)
                .then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", containsString("authorised"));
    }
}
