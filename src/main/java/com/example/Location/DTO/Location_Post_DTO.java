package com.example.Location.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
public class Location_Post_DTO {

    public Location_Post_DTO() {
        final Logger log = LogManager.getLogger(Location_Post_DTO.class.getName());
        log.info("LOCATION_POST_DTO: Inside the LOCATION_POST_DTO");
    }


    private String restaurant_code;
    private String restaurant_name;
    private String restaurant_pin_code;
}
