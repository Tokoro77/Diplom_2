package api;

import api.user.User;
import api.user.UserGenerator;
import api.user.UserMethod;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class UserLoginTests {

    private UserMethod client = new UserMethod();
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        // Создаём пользователя для тестов логина
        user = UserGenerator.random();
        ValidatableResponse createResponse = client.create(user);
        createResponse.assertThat().statusCode(SC_OK);
        accessToken = client.extractAccessToken(createResponse);
    }

    @After
    public void tearDown() {

        if (accessToken != null) {
            client.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Вход под существующим пользователем")
    @Description("Проверка успешного входа с корректными учетными данными")
    public void loginWithExistingUserShouldSucceed() {
        ValidatableResponse response = client.login(user);
        response.assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Вход с неверным email")
    @Description("Проверка, что вход с неверным email возвращает ошибку")
    public void loginWithWrongEmailShouldFail() {
        User wrongUser = new User(
                "wrong_" + System.currentTimeMillis() + "@example.com",
                user.getPassword(),
                user.getName()
        );

        client.login(wrongUser)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    @Description("Проверка, что вход с неверным паролем возвращает ошибку")
    public void loginWithWrongPasswordShouldFail() {
        User wrongUser = new User(
                user.getEmail(),
                "wrongpassword",
                user.getName()
        );

        client.login(wrongUser)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}