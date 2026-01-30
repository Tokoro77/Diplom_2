package api.order;

import api.base.BaseMethod;
import io.restassured.response.ValidatableResponse;

public class OrderMethod extends BaseMethod {
    protected static final String ORDERS_URI = API_PREFIX + "orders";
    protected static final String INGREDIENTS_URI = API_PREFIX + "ingredients";

    // Создание заказа с авторизацией
    public ValidatableResponse createWithAuth(Order order, String accessToken) {
        return getSpec()
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDERS_URI)
                .then().log().all();
    }

    // Создание заказа без авторизации
    public ValidatableResponse createWithoutAuth(Order order) {
        return getSpec()
                .body(order)
                .when()
                .post(ORDERS_URI)
                .then().log().all();
    }

    // Получение списка ингредиентов
    public ValidatableResponse getIngredients() {
        return getSpec()
                .when()
                .get(INGREDIENTS_URI)
                .then().log().all();
    }
}