package api.base;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BaseMethod {
    // Используем URL из документации API
    protected static final String BASE_URI = "https://stellarburgers.education-services.ru";
    protected static final String API_PREFIX = "/api/";

    // Метод для получения готового запроса
    protected RequestSpecification getSpec() {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URI);
    }
}