package nl.hkstwk.reactivemongo.repositories;

import nl.hkstwk.reactivemongo.domain.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
    Flux<Customer> findByCustomerName(String customerName);
}
