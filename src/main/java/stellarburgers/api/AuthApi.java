package stellarburgers.api;


import io.qameta.allure.Step;
import io.restassured.response.Response;
import stellarburgers.dto.User;


import static io.restassured.RestAssured.given;
import static stellarburgers.config.ApiConfig.BASE_URL;


public class AuthApi {


    @Step("Регистрация пользователя: {user.email}")
    public Response register(User user) {
        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(user)
                .when()
                .post("/api/auth/register");
    }


    @Step("Логин пользователя: {user.email}")
    public Response login(User user) {
        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(user)
                .when()
                .post("/api/auth/login");
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", accessToken)
                .when()
                .delete("/api/auth/user");
    }
}