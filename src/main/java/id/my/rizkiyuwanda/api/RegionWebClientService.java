package id.my.rizkiyuwanda.api;

import id.my.rizkiyuwanda.region.Region;
import id.my.rizkiyuwanda.utility.RWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RegionWebClientService {
    @Autowired
    private RWebClient rWebClient;

    public Region save(Region region) {
        Mono<Region> bankMono =  rWebClient.getWebClient().post()
                .uri("/region/save")
                .body(Mono.just(region), Region.class)
                .retrieve()
                .bodyToMono(Region.class);
        //EXECUTE
        return bankMono.block();
    }

    public String deleteById(Long id) {
        Mono<String> stringMono = rWebClient.getWebClient().delete()
                .uri("/region//deletebyid/{id}", id)
                .retrieve()
                .bodyToMono(String.class);
        //EXECUTE
        return stringMono.block();
    }

    public List<Region> findAll() {
        Flux<Region> regionFlux = rWebClient.getWebClient().get()
                .uri("/region/findall")
                .retrieve()
                .bodyToFlux(Region.class);
        //EXECUTE
        return regionFlux.collectList().block();
    }
}
