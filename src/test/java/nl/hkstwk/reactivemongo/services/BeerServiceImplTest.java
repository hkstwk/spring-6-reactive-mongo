package nl.hkstwk.reactivemongo.services;

import lombok.extern.slf4j.Slf4j;
import nl.hkstwk.reactivemongo.domain.Beer;
import nl.hkstwk.reactivemongo.mappers.BeerMapper;
import nl.hkstwk.reactivemongo.model.BeerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class BeerServiceImplTest {

    @Autowired
    BeerService beerService;

    @Autowired
    BeerMapper beerMapper;

    BeerDTO beerDTO;

    @BeforeEach
    void setUp() {
        beerDTO = beerMapper.beerToBeerDTO(getTestBeer());
    }

    @Test
    void saveBeer() throws InterruptedException {
        Mono<BeerDTO> savedMono = beerService.saveBeer(Mono.just(beerDTO));

        savedMono.subscribe(savedDto -> log.info(savedDto.getId()));

        Thread.sleep(1000);
    }

    @Test
    void getBeerById() {
    }

    public static Beer getTestBeer(){
        return Beer.builder()
                .beerName("Karmeliet")
                .beerStyle("Triple")
                .upc("12345")
                .price(BigDecimal.TEN)
                .quantityOnHand(24)
                .build();
    }
}