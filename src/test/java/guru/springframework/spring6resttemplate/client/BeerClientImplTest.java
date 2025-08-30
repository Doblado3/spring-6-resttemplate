package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClientImpl beerClientImpl;

    @Test
    void testDeleteBeerById() {

        BeerDTO beerToDelete = beerClientImpl.listBeers().stream().findFirst().get();

        beerClientImpl.deleteBeerById(beerToDelete.getId());

        assertThrows(HttpClientErrorException.class, () -> {
            beerClientImpl.getBeerById(beerToDelete.getId());
        });
    }

    @Test
    void testUpdateBeerById() {

        BeerDTO beerToUpdate = beerClientImpl.listBeers().stream().findFirst().get();
        beerToUpdate.setBeerName("Beer Updated");
        beerClientImpl.updateBeerById(beerToUpdate);

        BeerDTO savedBeer = beerClientImpl.getBeerById(beerToUpdate.getId());

        assertEquals(beerToUpdate.getBeerName(), savedBeer.getBeerName());
    }

    @Test
    void testCreateBeer() {

        BeerDTO newDto = BeerDTO.builder()
                .price(new BigDecimal("10.98"))
                .beerName("Mango Bobs")
                .beerStyle(BeerStyle.PILSNER)
                .quantityOnHand(200)
                .upc("12324")
                .build();

        BeerDTO savedDto = beerClientImpl.createBeer(newDto);
        assertNotNull(savedDto);
    }

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