package com.example.Location.SERVICES;


import com.example.Location.DTO.Remote_Put_Location_Menus_DTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RemoteRequest {

    private static final Logger log = LogManager.getLogger(RemoteRequest.class.getName());


    /////update function to location-menu api
    public Integer remote_update_location_menus(Remote_Put_Location_Menus_DTO remote_location_menus_dto) {

        log.info("REMOTE REQUEST: ENTERED INTO THE REMOTE REQUEST remote_Update_LOCATION_MENU");
        HttpHeaders headers = getHeaders();
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8082/menus/put/update_location_menus";
        System.out.println(url);
        HttpEntity<Remote_Put_Location_Menus_DTO> requestEntity = new HttpEntity<>(remote_location_menus_dto, headers);
        ResponseEntity<Integer> response_entity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Integer.class);

        log.info("REMOTE REQUEST: EXITED FROM THE REMOTE REQUEST remote_Update_LOCATION_MENU");
        return response_entity.getBody();
    }


    /////delete function  to location- menu api
    public Integer remote_delete_location_menus(String code) {

        log.info("REMOTE REQUEST: ENTERED INTO THE REMOTE REQUEST DELETE_LOCATION_MENU");
        HttpHeaders headers = getHeaders();
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8082/menus/delete/delete_location_menus/" + code;
        System.out.println(url);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Integer> response_entity = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Integer.class, 1);

        log.info("REMOTE REQUEST: Exited FROM THE REMOTE REQUEST DELETE_LOCATION_MENU");
        return response_entity.getBody();
    }


    private HttpHeaders getHeaders() {
        log.info("REMOTE REQUEST: Entered into the getHeaders");
        String credentials = "jose:jose@";
        System.out.println("credentiasl--->" + credentials);
        String encodeCredential = new String(Base64.encodeBase64(credentials.getBytes()));
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.add("Authorization", "Basic " + encodeCredential);
        log.debug("REMOTE REQUEST: EXITED FROM THE getHeaders");
        return header;
    }


}
