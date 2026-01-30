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

    // Извлечение accessToken из ответа
    public String extractAccessToken(ValidatableResponse response) {
        String token = response.extract().path("accessToken");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}