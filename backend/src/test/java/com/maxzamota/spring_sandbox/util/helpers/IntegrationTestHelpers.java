package com.maxzamota.spring_sandbox.util.helpers;

import com.maxzamota.spring_sandbox.dto.CustomerDto;
import com.maxzamota.spring_sandbox.model.CustomerEntity;
import io.netty.handler.logging.LogLevel;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class IntegrationTestHelpers {
    private static final String APP_BASE_URI = "http://localhost";
    private static final String CUSTOMER_PUBLIC_API_URI = "/api/v1/customer";

    public static WebTestClient getWebTestClient(Integer port) {
        return WebTestClient
                .bindToServer()
                .baseUrl(Objects.nonNull(port) ? APP_BASE_URI + ":" + port : APP_BASE_URI)
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient
                                        .create()
                                        // This will enable info level logging to underlying HttpClient
                                        // to see requests and responses data
                                        .wiretap(
                                                "HTTP_CLIENT",
                                                LogLevel.INFO,
                                                AdvancedByteBufFormat.TEXTUAL
                                        )
                        )
                )
                .defaultHeader(HttpHeaders.USER_AGENT, "Spring Web Client")
                .build();
    }

    @Step("Create new Customer")
    public static CustomerEntity postCustomer(@NotNull WebTestClient webClient, CustomerDto customerDto) {
        return webClient.post()
                .uri(CUSTOMER_PUBLIC_API_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Request-Id", String.valueOf(UUID.randomUUID()))
                .body(Mono.just(customerDto), CustomerDto.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(CustomerEntity.class)
                .returnResult()
                .getResponseBody();
    }

    @Step("Get all customers")
    public static Collection<CustomerEntity> getAllCustomers(@NotNull WebTestClient webClient) {
        return webClient.get()
                .uri(CUSTOMER_PUBLIC_API_URI + "/all")
                .accept(MediaType.APPLICATION_JSON)
                .header("Request-Id", String.valueOf(UUID.randomUUID()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CustomerEntity.class)
                .returnResult()
                .getResponseBody();
    }

    @Step("Get customer by id")
    public static CustomerEntity getCustomerById(WebTestClient webClient, Integer id) {
        return webClient.get()
                .uri(CUSTOMER_PUBLIC_API_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header("Request-Id", String.valueOf(UUID.randomUUID()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerEntity.class)
                .returnResult()
                .getResponseBody();
    }

    @Step("Delete customer by id")
    public static String deleteCustomerById(WebTestClient webClient, Integer id) {
        return webClient.delete()
                .uri(CUSTOMER_PUBLIC_API_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header("Request-Id", String.valueOf(UUID.randomUUID()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
    }

    @Step("Get customer by id not found")
    public static Object getCustomerByIdNotFound(WebTestClient webClient, Integer id) {
        return webClient.get()
                .uri(CUSTOMER_PUBLIC_API_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header("Request-Id", String.valueOf(UUID.randomUUID()))
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(Object.class)
                .returnResult()
                .getResponseBody();
    }

    @Step("Update customer")
    public static CustomerEntity putCustomer(WebTestClient webClient, CustomerEntity customerEntity) {
        return webClient.put()
                .uri(CUSTOMER_PUBLIC_API_URI + "/{id}", customerEntity.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Request-Id", String.valueOf(UUID.randomUUID()))
                .body(Mono.just(customerEntity), CustomerEntity.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerEntity.class)
                .returnResult()
                .getResponseBody();
    }
}
