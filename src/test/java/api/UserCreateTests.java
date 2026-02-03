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

public class UserCreateTests {

    private UserMethod client = new UserMethod();
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        // Создаем пользователя для теста на существующего пользователя
        user = UserGenerator.random();
        ValidatableResponse createResponse = client.create(user);
        createResponse.assertThat().statusCode(SC_OK);
        accessToken = client.extractAccessToken(createResponse);
    }

    @After
    public void tearDown() {

        if (accessToken != null) {
            client.delete(accessToken);
            // Убрали: .assertThat().statusCode(SC_ACCEPTED)
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешного создания пользователя с уникальными данными")
    public void createUniqueUserSuccessfully() {
        User newUser = UserGenerator.random();

        ValidatableResponse response = client.create(newUser);
        response.assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("user.email", equalTo(newUser.getEmail().toLowerCase()))
                .body("user.name", equalTo(newUser.getName()))
                .body("accessToken", notNullValue());

        String newUserToken = client.extractAccessToken(response);
        if (newUserToken != null) {
            client.delete(newUserToken);
            // Убрали: .assertThat().statusCode(SC_ACCEPTED)
        }
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Проверка, что нельзя создать пользователя с уже существующим email")
    public void createExistingUserShouldFail() {
        // Пытаемся создать пользователя с теми же данными
        client.create(user)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка, что нельзя создать пользователя без email")
    public void createUserWithoutEmailShouldFail() {
        User userWithoutEmail = UserGenerator.withoutEmail();

        client.create(userWithoutEmail)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка, что нельзя создать пользователя без пароля")
    public void createUserWithoutPasswordShouldFail() {
        User userWithoutPassword = UserGenerator.withoutPassword();

        client.create(userWithoutPassword)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Проверка, что нельзя создать пользователя без имени")
    public void createUserWithoutNameShouldFail() {
        User userWithoutName = UserGenerator.withoutName();

        client.create(userWithoutName)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}