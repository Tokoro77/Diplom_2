package api;

import api.user.User;
import api.user.UserGenerator;
import api.user.UserMethod;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class UserCreateTests {

    private UserMethod client = new UserMethod();

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешного создания пользователя с уникальными данными")
    public void createUniqueUserSuccessfully() {
        User user = UserGenerator.random();

        ValidatableResponse response = client.create(user);
        response.assertThat()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Проверка, что нельзя создать пользователя с уже существующим email")
    public void createExistingUserShouldFail() {
        // Сначала создаем пользователя
        User user = UserGenerator.random();
        client.create(user).assertThat().statusCode(200);

        // Пытаемся создать пользователя с теми же данными
        client.create(user)
                .assertThat()
                .statusCode(403)
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
                .statusCode(403)
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
                .statusCode(403)
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
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}