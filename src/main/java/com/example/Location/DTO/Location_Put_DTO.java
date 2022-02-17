package com.example.Location.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
public class Location_Put_DTO {

    public Location_Put_DTO() {
        final Logger log = LogManager.getLogger(Location_Put_DTO.class.getName());
        log.info("LOCATION_DTO_UPDATE: Inside the LOCATION_DTO_UPDATE");

    }


    private Long id;
    private String restaurant_code;
    private String restaurant_name;
    private String restaurant_pin_code;
}
