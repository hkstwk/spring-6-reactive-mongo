package nl.hkstwk.reactivemongo.services;

import nl.hkstwk.reactivemongo.domain.Beer;
import nl.hkstwk.reactivemongo.model.BeerDTO;
import reactor.core.publisher.Mono;

public interface BeerService {
    Mono<BeerDTO> saveBeer(Mono<BeerDTO> beerDTO);
    Mono<BeerDTO> getBeerById(String beerId);
}
