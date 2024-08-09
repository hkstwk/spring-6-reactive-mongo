package nl.hkstwk.reactivemongo.web.fn;

import lombok.RequiredArgsConstructor;
import nl.hkstwk.reactivemongo.model.BeerDTO;
import nl.hkstwk.reactivemongo.services.BeerService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static nl.hkstwk.reactivemongo.web.fn.BeerRouterConfig.BEER_PATH_ID;

@Component
@RequiredArgsConstructor
public class BeerHandler {
    private final BeerService beerService;

    public Mono<ServerResponse> getBeerById(ServerRequest request){
        String beerId = request.pathVariable("beerId");
        return ServerResponse.ok()
                .body(beerService.getBeerById(beerId), BeerDTO.class);
    }

    public Mono<ServerResponse> listBeers(ServerRequest request){
        return ServerResponse.ok()
                .body(beerService.listBeers(), BeerDTO.class);
    }

    public Mono<ServerResponse> createNewBeer(ServerRequest request){
        return beerService.saveBeer(request.bodyToMono(BeerDTO.class))
                .flatMap(beerDTO -> ServerResponse
                        .created(UriComponentsBuilder
                                .fromPath(BEER_PATH_ID)
                                .build(beerDTO.getId()))
                        .build());
    }

    public Mono<ServerResponse> updateBeerById(ServerRequest request) {
        return request.bodyToMono(BeerDTO.class)
                .map(beerDTO -> beerService
                        .updateBeer(request.pathVariable("beerId"), beerDTO))
                .flatMap(savedDTO -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> patchBeerById(ServerRequest request) {
        return request.bodyToMono(BeerDTO.class)
                .map(beerDTO -> beerService
                        .patchBeer(request.pathVariable("beerId"), beerDTO))
                .flatMap(savedDTO -> ServerResponse.noContent().build());
    }
}
