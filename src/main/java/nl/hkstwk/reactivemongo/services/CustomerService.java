package nl.hkstwk.reactivemongo.services;

import nl.hkstwk.reactivemongo.model.CustomerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Flux<CustomerDTO> listCustomers();
    Mono<CustomerDTO> getCustomerById(String customerId);
    Mono<CustomerDTO> saveCustomer(Mono<CustomerDTO> customerDTO);
    Mono<CustomerDTO> updateCustomer(String customerId, CustomerDTO customerDTO);
    Mono<Void> deleteCustomer(String customerId);
    Flux<CustomerDTO> findByCustomerName(String customerName);
}
