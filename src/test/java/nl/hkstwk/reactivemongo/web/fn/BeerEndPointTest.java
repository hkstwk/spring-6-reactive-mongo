package nl.hkstwk.reactivemongo.web.fn;

import nl.hkstwk.reactivemongo.domain.Beer;
import nl.hkstwk.reactivemongo.model.BeerDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.internal.matchers.GreaterThan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
class BeerEndPointTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @Order(1)
    void testListBeers() {
        webTestClient.get().uri(BeerRouterConfig.BEER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBodyList(BeerDTO.class).hasSize(3);
    }

    @Test
    @Order(2)
    void testGetBeerById() {
        webTestClient.get().uri(BeerRouterConfig.BEER_PATH_ID, 1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody(BeerDTO.class);
    }

    @Test
    void testGetBeerByIdNotFound() {
        webTestClient.get().uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateBeer() {
        webTestClient.post().uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(getTestBeer()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost:8080/api/v2/beer/4");
    }

    @Test
    void testCreateBeerInvalidData(){
        Beer testBeer = getTestBeer();
        testBeer.setBeerName("");

        webTestClient.post().uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(testBeer), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(3)
    void testUpdateBeer() {
        webTestClient.put().uri(BeerRouterConfig.BEER_PATH_ID, 1)
                .body(Mono.just(getTestBeer()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNoContent();

    }

    @Test
    void testUpdateBeerNotFound() {
        webTestClient.put().uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .body(Mono.just(getTestBeer()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    void testUpdateBeerInvalidData() {
        Beer testBeer = getTestBeer();
        testBeer.setBeerStyle("");

        webTestClient.put().uri(BeerRouterConfig.BEER_PATH_ID, 1)
                .body(Mono.just(testBeer), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPatchBeer() {
        webTestClient.patch().uri(BeerRouterConfig.BEER_PATH_ID, 1)
                .body(Mono.just(getTestBeer()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testPatchBeerNotFound() {
        webTestClient.patch().uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .body(Mono.just(getTestBeer()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testaDeleteBeer() {
        webTestClient.delete().uri(BeerRouterConfig.BEER_PATH_ID, 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testaDeleteBeerNotFound() {
        webTestClient.delete().uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    public static Beer getTestBeer(){
        return Beer.builder()
                .beerName("Karmeliet")
                .beerStyle("TRIPLE")
                .upc("abcde")
                .price(BigDecimal.valueOf(13.75))
                .quantityOnHand(10)
                .build();
    }
}