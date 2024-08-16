package nl.hkstwk.reactivemongo.web.fn;

import nl.hkstwk.reactivemongo.domain.Customer;
import nl.hkstwk.reactivemongo.model.CustomerDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class CustomerEndPointTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @Order(1)
    void testListCustomer() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody().jsonPath("$.size()").value(greaterThan(1));
    }

    @Test
    @Order(2)
    void testGetCustomerById() {
        CustomerDTO customerDTO = getSavedTestCustomer();

        webTestClient
                .mutateWith(mockOAuth2Login())
                .get().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody(CustomerDTO.class);
    }

    @Test
    @Order(3)
    void testListCustomersByName() {
        final String CUSTOMER_NAME = "De eetkamer van Giethoorn";
        CustomerDTO testDto = getSavedTestCustomer();
        testDto.setCustomerName(CUSTOMER_NAME);

        //create test data
        webTestClient
                .mutateWith(mockOAuth2Login())
                .post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(testDto), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange();

        webTestClient
                .mutateWith(mockOAuth2Login())
                .get().uri(UriComponentsBuilder
                        .fromPath(CustomerRouterConfig.CUSTOMER_PATH)
                        .queryParam("customerName", CUSTOMER_NAME).build().toUri())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody().jsonPath("$.size()").value(equalTo(1));
    }

    @Test
    void testCreateCustomer() {
        CustomerDTO customerDTO = getSavedTestCustomer();

        webTestClient
                .mutateWith(mockOAuth2Login())
                .post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("location");
    }

    @Test
    void testCreateCustomerInvalidData() {
        Customer testCustomer = getTestCustomer();
        testCustomer.setCustomerName("");

        webTestClient
                .mutateWith(mockOAuth2Login())
                .post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(testCustomer), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(4)
    void testUpdateCustomer() {
        CustomerDTO customerDTO = getSavedTestCustomer();
        customerDTO.setCustomerName("updated customer name");

        webTestClient
                .mutateWith(mockOAuth2Login())
                .put().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testUpdateCustomerNotFound() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .put().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
                .body(Mono.just(getTestCustomer()), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateCustomerInvalidData() {
        Customer testCustomer = getTestCustomer();
        testCustomer.setCustomerName("");

        webTestClient
                .mutateWith(mockOAuth2Login())
                .put().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 1)
                .body(Mono.just(testCustomer), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testDeleteCustomer() {
        CustomerDTO customerDTO = getSavedTestCustomer();
        webTestClient
                .mutateWith(mockOAuth2Login())
                .delete().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteCustomerNotFound() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .delete().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 1234)
                .exchange()
                .expectStatus().isNotFound();
    }

    public CustomerDTO getSavedTestCustomer() {
        FluxExchangeResult<CustomerDTO> customerDTOFluxExchangeResult = webTestClient
                .mutateWith(mockOAuth2Login())
                .post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(getTestCustomer()), Customer.class)
                .header("Content-Type", "application/json")
                .exchange()
                .returnResult(CustomerDTO.class);

        List<String> location = customerDTOFluxExchangeResult.getResponseHeaders().get("Location");

        return webTestClient
                .mutateWith(mockOAuth2Login())
                .get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange().returnResult(CustomerDTO.class).getResponseBody().blockFirst();
    }

    public static Customer getTestCustomer() {
        return Customer.builder()
                .customerName("Harmpie")
                .build();
    }
}
