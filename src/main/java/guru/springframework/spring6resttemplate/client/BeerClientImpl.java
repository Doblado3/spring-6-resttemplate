package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.databind.JsonNode;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import guru.springframework.spring6resttemplate.model.RestPageImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;


    private static final String GET_BEER_PATH = "/api/v1/beer";
    private static final String GET_BEER_BY_ID_PATH = "/api/v1/beer/{beerId}";

    @Override
    public Page<BeerDTO> listBeers() {

        return listBeers(null, null, null, null);
    }



    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Integer pageNumber, Integer size) {

        RestTemplate restTemplate = restTemplateBuilder.build();

        UriComponentsBuilder uriComponentsBuilder = buildUriWithQueryParams(beerName,
                beerStyle, pageNumber, size);


        ResponseEntity<RestPageImpl> response =
                restTemplate.getForEntity(uriComponentsBuilder.toUriString(), RestPageImpl.class);

        return response.getBody();
    }

    @Override
    public BeerDTO getBeerById(UUID beerId) {

        RestTemplate restTemplate = restTemplateBuilder.build();

        return restTemplate.getForObject(GET_BEER_BY_ID_PATH, BeerDTO.class, beerId);
    }

    private UriComponentsBuilder buildUriWithQueryParams(String beerName, BeerStyle beerStyle, Integer pageNumber, Integer size) {

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

        if (beerName != null) {
            uriComponentsBuilder.queryParam("beerName", beerName);
        }

        if (beerStyle != null) {
            uriComponentsBuilder.queryParam("beerStyle", beerStyle);
        }

        if (pageNumber != null) {
            uriComponentsBuilder.queryParam("pageNumber", pageNumber);
        }

        if (size != null) {
            uriComponentsBuilder.queryParam("pageSize", size);
        }

        return uriComponentsBuilder;
    }
}
