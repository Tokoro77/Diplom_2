package api;

import api.order.Order;
import api.order.OrderMethod;
import api.user.User;
import api.user.UserGenerator;
import api.user.UserMethod;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.*;

public class OrderCreateTests {

    private UserMethod userClient = new UserMethod();
    private OrderMethod orderClient = new OrderMethod();
    private String accessToken;
    private List<String> validIngredients = new ArrayList<>();

    @Before
    public void setUp() {
        // Получаем реальные ингредиенты из API
        ValidatableResponse ingredientsResponse = orderClient.getIngredients();
        if (ingredientsResponse.extract().statusCode() == 200) {
            validIngredients = ingredientsResponse.extract().jsonPath().getList("data._id");
            if (validIngredients == null || validIngredients.isEmpty()) {
                validIngredients = ingredientsResponse.extract().jsonPath().getList("_id");
            }
        }

        if (validIngredients == null || validIngredients.isEmpty()) {
            validIngredients = Arrays.asList(
                    "60d3b41abdacab0026a733c6",
                    "609646e4dc916e00276b2870"
            );
        }

        User user = UserGenerator.random();
        ValidatableResponse createResponse = userClient.create(user);
        createResponse.assertThat().statusCode(200);
        accessToken = userClient.extractAccessToken(createResponse);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    @Description("Проверка успешного создания заказа авторизованным пользователем")
    public void createOrderWithAuthAndIngredientsShouldSucceed() {
        if (validIngredients.size() >= 2) {
            List<String> ingredients = Arrays.asList(
                    validIngredients.get(0),
                    validIngredients.get(1)
            );
            Order order = new Order(ingredients);

            orderClient.createWithAuth(order, accessToken)
                    .assertThat()
                    .statusCode(200)
                    .body("success", equalTo(true))
                    .body("order.number", greaterThan(0));
        }
    }

    @Test
    @DisplayName("Создание заказа без авторизации с ингредиентами")
    @Description("Проверка создания заказа неавторизованным пользователем")
    public void createOrderWithoutAuthWithIngredientsShouldSucceed() {
        if (!validIngredients.isEmpty()) {
            List<String> ingredients = Collections.singletonList(validIngredients.get(0));
            Order order = new Order(ingredients);

            orderClient.createWithoutAuth(order)
                    .assertThat()
                    .statusCode(200)
                    .body("success", equalTo(true))
                    .body("order.number", greaterThan(0));
        }
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов (пустой массив)")
    @Description("Проверка создания заказа с пустым массивом ингредиентов")
    public void createOrderWithoutIngredientsEmptyArrayShouldFail() {
        Order order = new Order(Collections.emptyList());

        orderClient.createWithAuth(order, accessToken)
                .assertThat()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверка создания заказа с невалидными хешами ингредиентов")
    public void createOrderWithInvalidIngredientHashShouldFail() {
        List<String> ingredients = Collections.singletonList("invalid_hash_12345");
        Order order = new Order(ingredients);

        orderClient.createWithAuth(order, accessToken)
                .assertThat()
                .statusCode(500); // ✅ Правильно! 500 Internal Server Error
    }

    // Дополнительные тесты для полного покрытия (опционально)
    @Test
    @DisplayName("Создание заказа без ингредиентов (null)")
    @Description("Проверка создания заказа с null вместо массива ингредиентов")
    public void createOrderWithNullIngredientsShouldFail() {
        Order order = new Order(null);

        orderClient.createWithAuth(order, accessToken)
                .assertThat()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }
}