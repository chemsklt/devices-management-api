package com.devices.devices_service.integration;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class DeviceIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15");

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void shouldCreateAndFetchDevice() throws Exception {
        String requestBody = """
                {
                "name": "Printer",
                "brand": "HP",
                "state": "AVAILABLE"
                }
                """;

        Long id = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/devices")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath().getLong("id");

        given()
                .when()
                .get("/devices/{id}", id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Printer"));
    }

    @Test
    void shouldUpdateDevice() {

        String create = """
        {
          "name": "Printer",
          "brand": "HP",
          "state": "AVAILABLE"
        }
        """;

        Long id =
                given()
                        .contentType("application/json")
                        .body(create)
                        .when()
                        .post("/devices")
                        .then()
                        .extract()
                        .jsonPath().getLong("id");
        var device =
                given()
                        .when()
                        .get("/devices/{id}", id)
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath();

        Long version = device.getLong("version");

        String update = """
        {
          "name": "Printer Updated",
          "brand": "HP",
          "state": "AVAILABLE",
          "version": %d
        }
        """.formatted(version);

        given()
                .contentType("application/json")
                .body(update)
                .when()
                .put("/devices/{id}", id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Printer Updated"));
    }
    @Test
    void shouldReturn404WhenDeviceNotFound() {

        given()
                .when()
                .get("/devices/{id}", 9999)
                .then()
                .statusCode(404)
                .body("message", equalTo("Device not found with id: 9999"));
    }

    @Test
    void shouldReturn400ForInvalidDeviceState() {

        given()
                .when()
                .get("/devices?state=INVALID_STATE")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldReturn400WhenRequestIsInvalid() {

        String requestBody = """
    {
        "name": "",
        "brand": "",
        "state": "AVAILABLE"
    }
    """;

        given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/devices")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldReturn404WhenDeletingUnknownDevice() {

        given()
                .when()
                .delete("/devices/{id}", 9999)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturnPagedDevices() {

        given()
                .when()
                .get("/devices?page=0&size=10")
                .then()
                .statusCode(200)
                .body("content", notNullValue())
                .body("page", equalTo(0));
    }

    @Test
    void shouldFailOnConcurrencyUpdate() {
        String create = """
                {
                  "name": "Printer",
                  "brand": "HP",
                  "state": "AVAILABLE"
                }
                """;

        Long id =
                given()
                        .contentType("application/json")
                        .body(create)
                        .when()
                        .post("/devices")
                        .then()
                        .extract()
                        .jsonPath()
                        .getLong("id");
        var device =
                given()
                        .when()
                        .get("/devices/{id}", id)
                        .then()
                        .extract()
                        .jsonPath();

        Long version = device.getLong("version");

        String update1 = """
                {
                  "name": "Printer A",
                  "brand": "HP",
                  "state": "AVAILABLE",
                  "version": %d
                }
                """.formatted(version);

        String update2 = """
                {
                  "name": "Printer B",
                  "brand": "HP",
                  "state": "AVAILABLE",
                  "version": %d
                }
                """.formatted(version);

        given()
                .contentType("application/json")
                .body(update1)
                .when()
                .put("/devices/{id}", id)
                .then()
                .statusCode(200);

        given()
                .contentType("application/json")
                .body(update2)
                .when()
                .put("/devices/{id}", id)
                .then()
                .statusCode(409);
    }

}
