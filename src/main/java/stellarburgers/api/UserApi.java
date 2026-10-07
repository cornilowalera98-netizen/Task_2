package stellarburgers.api;


import io.qameta.allure.Step;
import io.restassured.response.Response;


import static io.restassured.RestAssured.given;
import static stellarburgers.config.ApiConfig.BASE_URL;


public class UserApi {


    @Step("Обновление данных пользователя с авторизацией")
    public Response updateUserWithAuth(String accessToken, Object body) {
        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .header("Authorization", accessToken)
                .body(body)
                .when()
                .patch("/api/auth/user");
    }


    @Step("Обновление данных пользователя без авторизации")
    public Response updateUserWithoutAuth(Object body) {
        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(body)
                .when()
                .patch("/api/auth/user");
    }
}
