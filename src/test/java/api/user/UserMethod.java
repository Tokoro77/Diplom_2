package api.user;

import api.base.BaseMethod;
import io.restassured.response.ValidatableResponse;

public class UserMethod extends BaseMethod {
    protected static final String AUTH_URI = API_PREFIX + "auth/";

    // Регистрация пользователя
    public ValidatableResponse create(User user) {
        return getSpec()
                .body(user)
                .when()
                .post(AUTH_URI + "register")
                .then().log().all();
    }

    // Логин пользователя
    public ValidatableResponse login(User user) {
        return getSpec()
                .body(user)
                .when()
                .post(AUTH_URI + "login")
                .then().log().all();
    }

    // Удаление пользователя
    public ValidatableResponse delete(String accessToken) {
        return getSpec()
                .header("Authorization", accessToken)
                .when()
                .delete(AUTH_URI + "user")
                .then().log().all();
    }

    public String extractAccessToken(ValidatableResponse response) {
        // Просто возвращаем токен как есть из API
        return response.extract().path("accessToken");
    }
}