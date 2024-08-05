package nl.hkstwk.reactivemongo.web.fn;

import lombok.RequiredArgsConstructor;
import nl.hkstwk.reactivemongo.model.BeerDTO;
import nl.hkstwk.reactivemongo.services.BeerService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BeerHandler {
    private final BeerService beerService;

    public Mono<ServerResponse> listBeers(ServerRequest request){
        return ServerResponse.ok()
                .body(beerService.listBeers(), BeerDTO.class);
    }
}
