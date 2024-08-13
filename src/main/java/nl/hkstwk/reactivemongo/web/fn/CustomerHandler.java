package nl.hkstwk.reactivemongo.web.fn;

import lombok.RequiredArgsConstructor;
import nl.hkstwk.reactivemongo.model.CustomerDTO;
import nl.hkstwk.reactivemongo.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerHandler {
    private final CustomerService customerService;
    private final Validator validator;

    private void validate(CustomerDTO customerDTO) {
        Errors errors = new BeanPropertyBindingResult(customerDTO, "customerDTO");
        validator.validate(customerDTO, errors);

        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString());
        }
    }

    public Mono<ServerResponse> getCustomerById(ServerRequest request) {
        return ServerResponse.ok()
                .body(customerService.getCustomerById(request.pathVariable("customerId"))
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))), CustomerDTO.class);
    }

    public Mono<ServerResponse> listCustomers(ServerRequest request) {
        Flux<CustomerDTO> flux;

        if (request.queryParam("customerName").isPresent()) {
            flux = customerService.findByCustomerName(request.queryParam("customerName").get());
        } else {
            flux = customerService.listCustomers();
        }
        return ServerResponse.ok()
                .body(flux, CustomerDTO.class);
    }

}
