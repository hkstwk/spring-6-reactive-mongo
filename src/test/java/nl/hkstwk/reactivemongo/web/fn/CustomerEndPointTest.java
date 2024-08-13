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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class CustomerEndPointTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @Order(1)
    void testListCustomer() {
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody().jsonPath("$.size()").value(greaterThan(1));
    }

    @Test
    @Order(2)
    void testGetCustomerById() {
        CustomerDTO customerDTO = getSavedTestCustomer();

        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody(CustomerDTO.class);
    }

    public CustomerDTO getSavedTestCustomer(){
        FluxExchangeResult<CustomerDTO> customerDTOFluxExchangeResult = webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(getTestCustomer()), Customer.class)
                .header("Content-Type", "application/json")
                .exchange()
                .returnResult(CustomerDTO.class);

        List<String> location = customerDTOFluxExchangeResult.getResponseHeaders().get("Location");

        return webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange().returnResult(CustomerDTO.class).getResponseBody().blockFirst();
    }

    @Test
    @Order(3)
    void testListCustomersByName() {
        final String CUSTOMER_NAME = "De Rechter";
        CustomerDTO testDto = getSavedTestCustomer();
        testDto.setCustomerName(CUSTOMER_NAME);

        //create test data
        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(testDto), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange();

        webTestClient.get().uri(UriComponentsBuilder
                        .fromPath(CustomerRouterConfig.CUSTOMER_PATH)
                        .queryParam("customerName", CUSTOMER_NAME).build().toUri())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody().jsonPath("$.size()").value(equalTo(2));
    }

    @Test
    void testCreateCustomer() {
        CustomerDTO customerDTO = getSavedTestCustomer();

        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("location");
    }

    public static Customer getTestCustomer() {
        return Customer.builder()
                .customerName("Harmpie")
                .build();
    }
}
