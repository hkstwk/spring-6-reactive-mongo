package nl.hkstwk.reactivemongo.mappers;

import nl.hkstwk.reactivemongo.domain.Customer;
import nl.hkstwk.reactivemongo.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
    CustomerDTO customerToCustomerDTO(Customer customer);
    Customer customerDTOToCustomer(CustomerDTO customerDTO);
}
