package nl.hkstwk.reactivemongo.mappers;

import nl.hkstwk.reactivemongo.domain.Beer;
import nl.hkstwk.reactivemongo.model.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {
    BeerDTO beerToBeerDTO(Beer beer);
    Beer beerDTOToBeer(BeerDTO beerDTO);
}
