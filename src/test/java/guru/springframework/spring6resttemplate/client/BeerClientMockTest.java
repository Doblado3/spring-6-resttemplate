package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6resttemplate.config.RestTemplateBuilderConfig;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import guru.springframework.spring6resttemplate.model.RestPageImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;


@RestClientTest
@Import(RestTemplateBuilderConfig.class)
public class BeerClientMockTest {

    static final String URL = "http://localhost:8080";

    BeerClient beerClient;
    MockRestServiceServer server;

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    @Autowired
    ObjectMapper objectMapper;

    @Mock
    RestTemplateBuilder mockRestTemplateBuilder = new RestTemplateBuilder(new MockServerRestTemplateCustomizer());

    BeerDTO beer;
    String dtoJson;

    @BeforeEach
    void setUp() throws JsonProcessingException {

        RestTemplate restTemplate = restTemplateBuilder.build();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);
        beerClient = new BeerClientImpl(mockRestTemplateBuilder);

        beer = getBeerDto();
        dtoJson = objectMapper.writeValueAsString(beer);
    }

    @Test
    void testListBeersWithParams() throws JsonProcessingException {

        String response = objectMapper.writeValueAsString(getPage());

        URI uri = UriComponentsBuilder.fromUriString(URL + BeerClientImpl.GET_BEER_PATH)
                .queryParam("beerName", "ALE")
                .build().toUri();

        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(uri))
                .andExpect(queryParam("beerName", "ALE"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        Page<BeerDTO> responsePage = beerClient
                .listBeers("ALE", null, null, null);

        assertThat(responsePage.getContent().size()).isEqualTo(1);
    }
    
    @Test
    void testUpdateBeerById() {

        server.expect(method(HttpMethod.PUT))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH,
                        beer.getId()))
                .andRespond(withNoContent());

        mockGetOperation();

        BeerDTO respondeDto = beerClient.updateBeerById(beer);
        assertThat(respondeDto.getId()).isEqualTo(beer.getId());
        
    }

    @Test
    void testCreateBeer()  {


        URI uri = UriComponentsBuilder.fromPath(BeerClientImpl.GET_BEER_BY_ID_PATH).build(beer.getId());

        server.expect(method(HttpMethod.POST))
                        .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
                                .andRespond(withAccepted().location(uri));

        mockGetOperation();

        BeerDTO responseDto = beerClient.createBeer(beer);
        assertThat(responseDto.getId()).isEqualTo(beer.getId());

    }

    @Test
    void testGetBeerById() {

        mockGetOperation();

        BeerDTO beerById = beerClient.getBeerById(beer.getId());
        assertThat(beerById.getId()).isEqualTo(beer.getId());
    }

    @Test
    void testListBeers() throws JsonProcessingException {

        String payload = objectMapper.writeValueAsString(getPage());

        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        Page<BeerDTO> dtos = beerClient.listBeers();
        assertThat(dtos.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testDeleteNotFound() {

        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH,
                        beer.getId()))
                .andRespond(withResourceNotFound());

        assertThrows(HttpClientErrorException.class, () -> {

            beerClient.deleteBeerById(beer.getId());

        });

        server.verify();
    }

    @Test
    void testDeleteBeerById() {

        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH,
                        beer.getId()))
                .andRespond(withNoContent());

        beerClient.deleteBeerById(beer.getId());

        server.verify();
    }

    BeerDTO getBeerDto() {
        return BeerDTO.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .price(new BigDecimal("10.98"))
                .beerName("Mango Bobs")
                .beerStyle(BeerStyle.PILSNER)
                .quantityOnHand(200)
                .upc("12324")
                .build();
    }

    RestPageImpl getPage() {
        return new RestPageImpl(Arrays.asList(getBeerDto()), 1, 25, 1);
    }

    private void mockGetOperation() {
        server.expect(method(HttpMethod.GET))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, beer.getId()))
                .andRespond(withSuccess(dtoJson, MediaType.APPLICATION_JSON));
    }
}
