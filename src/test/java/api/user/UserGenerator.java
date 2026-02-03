package api.user;

import org.apache.commons.lang3.RandomStringUtils;

public class UserGenerator {

    // Генерация случайного пользователя со всеми полями
    public static User random() {
        String randomString = RandomStringUtils.randomAlphanumeric(10);
        return new User(
                "test_" + randomString + "@example.com",
                "password_" + randomString,
                "User_" + RandomStringUtils.randomAlphabetic(8)
        );
    }

    // Пользователь без email
    public static User withoutEmail() {
        String randomString = RandomStringUtils.randomAlphanumeric(10);
        return new User(
                null,
                "password_" + randomString,
                "User_" + RandomStringUtils.randomAlphabetic(8)
        );
    }

    // Пользователь без пароля
    public static User withoutPassword() {
        String randomString = RandomStringUtils.randomAlphanumeric(10);
        return new User(
                "test_" + randomString + "@example.com",
                null,
                "User_" + RandomStringUtils.randomAlphabetic(8)
        );
    }

    // Пользователь без имени
    public static User withoutName() {
        String randomString = RandomStringUtils.randomAlphanumeric(10);
        return new User(
                "test_" + randomString + "@example.com",
                "password_" + randomString,
                null
        );
    }

    // Пользователь с неверными данными для логина
    public static User randomWithWrongCredentials() {
        String randomString = RandomStringUtils.randomAlphanumeric(10);
        return new User(
                "wrong_" + randomString + "@example.com",
                "wrong_password_" + randomString
        );
    }
}