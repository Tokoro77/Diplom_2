package api;

import api.user.User;
import api.user.UserGenerator;
import api.user.UserMethod;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class UserLoginTests {

    private UserMethod client = new UserMethod();

    @Test
    @DisplayName("Вход под существующим пользователем")
    @Description("Проверка успешного входа с корректными учетными данными")
    public void loginWithExistingUserShouldSucceed() {
        // Создаём пользователя
        User user = UserGenerator.random();
        client.create(user).assertThat().statusCode(200);

        // Логинимся под ним
        ValidatableResponse response = client.login(user);
        response.assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Вход с неверным логином и паролем")
    @Description("Проверка, что вход с неверными данными возвращает ошибку")
    public void loginWithWrongEmailAndPasswordShouldFail() {
        User wrongUser = UserGenerator.randomWithWrongCredentials();

        client.login(wrongUser)
                .assertThat()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}