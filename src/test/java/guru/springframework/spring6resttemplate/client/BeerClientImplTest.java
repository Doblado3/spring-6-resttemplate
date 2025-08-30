package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClientImpl beerClientImpl;

    @Test
    void testGetBeerById() {

        Page<BeerDTO> beerDTOs = beerClientImpl.listBeers();

        BeerDTO dto = beerDTOs.getContent().getFirst();

        BeerDTO byId = beerClientImpl.getBeerById(dto.getId());

        assertNotNull(byId);
    }

    @Test
    void listBeersNoName() {

        beerClientImpl.listBeers(null, null, null, null);
    }

    @Test
    void listBeersParams() {

        beerClientImpl.listBeers("ALE", null, null, null);
    }

}