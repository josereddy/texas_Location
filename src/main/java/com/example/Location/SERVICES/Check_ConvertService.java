package com.example.Location.SERVICES;


import com.example.Location.DTO.Location_Post_DTO;
import com.example.Location.DTO.Location_Put_DTO;
import com.example.Location.DTO.Remote_Put_Location_Menus_DTO;
import com.example.Location.DTO.User_Data_DTO;
import com.example.Location.Document.Location_MDB;
import com.example.Location.Entity.User_Data_DB;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Check_ConvertService {


    private static final Logger log = LogManager.getLogger(Check_ConvertService.class.getName());


    @Autowired
    private SequenceGeneratorService seq_service;

    private Location_MDB location_mdb;
    private User_Data_DB user_data_db;
    private Remote_Put_Location_Menus_DTO remote_location_menus_dto;


    /////check location_post_Dto
    public boolean check_location_post_dto(Location_Post_DTO location_post_dto) {
        log.info("CHECK_CONVERT_SERVICE: Entered Location Data check Service");


        if (location_post_dto.getRestaurant_code() == null || location_post_dto.getRestaurant_name() == null ||
                location_post_dto.getRestaurant_pin_code() == null)
            return false;
        log.info("CHECK_CONVERT_SERVICE: Successfully Exited Location Data check Service");
        return true;
    }


    //dto document converter
    public Location_MDB DtoDocument_convert(Location_Post_DTO location_post_dto) {
        log.info("CHECK_CONVERT_SERVICE: Entered INTO DATA TO Document Conversion SERVICE");

        location_mdb = new Location_MDB();
        location_mdb.setId(seq_service.generateSequence(Location_MDB.SEQUENCE_NAME));
        location_mdb.setRestaurantcode(location_post_dto.getRestaurant_code());
        location_mdb.setRestaurantname(location_post_dto.getRestaurant_name());
        location_mdb.setRestaurantpincode(location_post_dto.getRestaurant_pin_code());

        log.info("CHECK_CONVERT_SERVICE: EXITED FROM Location DATA to Documents Conversion Service");
        return location_mdb;

    }


    ///d to entity converter
    public User_Data_DB convertUser(User_Data_DTO user_data_dto) {
        log.info("CHECK_CONVERT_SERVICE: Entered into the USER PASSWORD CONVERSION DTOE SERVICE ");
        user_data_db = new User_Data_DB();
        user_data_db.setUsername(user_data_dto.getUser_name());
        user_data_db.setUserroll(user_data_dto.getUser_roll());
        user_data_db.setId(0);
        log.info("CHECK_CONVERT_SERVICE: EXITED FROM  the USER PASSWORD CONVERSION DTOE SERVICE ");
        return user_data_db;

    }


    ///get all fields of location
    public Set<String> getAllLocationFields() {
        log.info("CHECK_CONVERT_SERVICE: Entered into get All fields Service");

        Set<String> fields = new HashSet<>();
        fields.add("id");
        fields.add("restaurantcode");
        fields.add("restaurantname");
        fields.add("restaurantpincode");
        log.info("CHECK_CONVERT_SERVICE: Exited from get All fields Service");
        return fields;
    }


    //create remote dto
    public Remote_Put_Location_Menus_DTO create_remote_put_location_menus_dto(Optional<Location_MDB> location_db, Location_Put_DTO location_put_dto) {
        remote_location_menus_dto = new Remote_Put_Location_Menus_DTO();
        remote_location_menus_dto.setUpdated_restaurant_code(location_put_dto.getRestaurant_code());
        remote_location_menus_dto.setUpdated_restaurant_name(location_put_dto.getRestaurant_name());
        remote_location_menus_dto.setOld_restaurant_code(location_db.get().getRestaurantcode());
        return remote_location_menus_dto;
    }
}