package com.banulp.toy.zktest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    public List<String> getServiceUrl(String serviceName) {
        // /services/zkclienttest 일 경우, zkclienttest
        List<ServiceInstance> list = discoveryClient.getInstances(serviceName);
        List<String> uriList = new ArrayList<>();
        for ( ServiceInstance si : list ) {
            uriList.add(si.getUri().toString());
        }
        return uriList;
    }

    @RequestMapping("serviceUrl")
    public List<String> serviceUrl(@RequestParam String serviceName) {
        return getServiceUrl(serviceName);
    }

    @RequestMapping("remoteUrlCall")
    public List<String> remoteUrlCall(@RequestParam String serviceName) {
        List<String> list = getServiceUrl(serviceName);

        List<String> responseList = new ArrayList<>();
        for ( String url : list ) {
            String callUrl = url + "/initCache";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(null, headers);
            ResponseEntity<String> exchange = restTemplate.exchange(callUrl, HttpMethod.GET, entity, String.class);
            responseList.add(exchange.getBody());
        }
        return responseList;
    }

    @RequestMapping("message")
    public String message() {
        return "Hello world. zookeepertest message.";
    }

    @RequestMapping("initCache")
    public String initCache() {
        return "Hello world. initCache.";
    }

}
