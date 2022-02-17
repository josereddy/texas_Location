package com.example.Location.Entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Interceptor_Data_DB {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer Id;


    private String apiname;
    private String servicename;
    private String url;
    private Date date;
    private Long timemillisec;
}
