package com.example.Location.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
public class User_Data_DTO {

    public User_Data_DTO() {
        final Logger log = LogManager.getLogger(User_Data_DTO.class.getName());
        log.info("USER_Data_DTO: Inside the User_Data_DTO");
    }


    private String user_name;
    private String user_password;
    private String user_roll;
}
