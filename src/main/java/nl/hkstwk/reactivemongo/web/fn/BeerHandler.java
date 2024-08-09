package nl.hkstwk.reactivemongo.web.fn;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.hkstwk.reactivemongo.model.BeerDTO;
import nl.hkstwk.reactivemongo.services.BeerService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static nl.hkstwk.reactivemongo.web.fn.BeerRouterConfig.BEER_PATH_ID;

@Component
@RequiredArgsConstructor
public class BeerHandler {
    private final BeerService beerService;
    private final Validator validator;

    private void validate(BeerDTO beerDTO) {
        Errors errors = new BeanPropertyBindingResult(beerDTO, "beerDto");
        validator.validate(beerDTO, errors);

        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString());
        }
    }

    public Mono<ServerResponse> getBeerById(ServerRequest request) {
        return ServerResponse.ok()
                .body(beerService.getBeerById(request.pathVariable("beerId"))
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))), BeerDTO.class);
    }

    public Mono<ServerResponse> listBeers(ServerRequest request) {
        return ServerResponse.ok()
                .body(beerService.listBeers(), BeerDTO.class);
    }

    public Mono<ServerResponse> createNewBeer(ServerRequest request) {
        return beerService.saveBeer(request.bodyToMono(BeerDTO.class)
                        .doOnNext(this::validate))
                .flatMap(beerDTO -> ServerResponse
                        .created(UriComponentsBuilder
                                .fromPath(BEER_PATH_ID)
                                .build(beerDTO.getId()))
                        .build());
    }

    public Mono<ServerResponse> updateBeerById(ServerRequest request) {
        return request.bodyToMono(BeerDTO.class)
                .doOnNext(this::validate)
                .flatMap(beerDTO -> beerService
                        .updateBeer(request.pathVariable("beerId"), beerDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(savedDTO -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> patchBeerById(ServerRequest request) {
        return request.bodyToMono(BeerDTO.class)
                .doOnNext(this::validate)
                .flatMap(beerDTO -> beerService
                        .patchBeer(request.pathVariable("beerId"), beerDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(savedDTO -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> deleteBeerById(ServerRequest request) {
        return beerService.getBeerById(request.pathVariable("beerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(beerDTO -> beerService.deleteBeerById(beerDTO.getId()))
                .then(ServerResponse.noContent().build());
    }
}
