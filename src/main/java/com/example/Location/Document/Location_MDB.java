package com.example.Location.Document;


import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Transient;

@Document(collection = "LOCATIONS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter("ModelLocation")
public class Location_MDB {

    @Transient
    public static final String SEQUENCE_NAME = "Location_sequence";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Indexed(unique = true)
    private String restaurantcode;

    private String restaurantname;

    private String restaurantpincode;


}
