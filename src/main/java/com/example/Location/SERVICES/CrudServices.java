package com.example.Location.SERVICES;


import com.example.Location.DTO.Location_Post_DTO;
import com.example.Location.DTO.Location_Put_DTO;
import com.example.Location.DTO.Remote_Put_Location_Menus_DTO;
import com.example.Location.DTO.User_Data_DTO;
import com.example.Location.Document.Location_MDB;
import com.example.Location.EXCEPTION.*;
import com.example.Location.Entity.Interceptor_Data_DB;
import com.example.Location.Entity.User_Data_DB;
import com.example.Location.REPOSITORY.Interceptor_Repository;
import com.example.Location.REPOSITORY.Location_Repository;
import com.example.Location.REPOSITORY.User_Data_Repository;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Component
@Data
public class CrudServices {


    public CrudServices(Location_Repository location_repository, Check_ConvertService cc_service
            , User_Data_Repository user_repository, RemoteRequest rr_service, Interceptor_Repository interceptor_repository) {
        this.location_repository = location_repository;
        this.cc_service = cc_service;
        this.user_repository = user_repository;
        this.interceptor_repository = interceptor_repository;
        this.rr_service = rr_service;
    }

    //logger
    private static final Logger log = LogManager.getLogger(CrudServices.class.getName());


    ///Repositories
    private Location_Repository location_repository;
    private User_Data_Repository user_repository;
    private Interceptor_Repository interceptor_repository;

    //Services
    private Check_ConvertService cc_service;
    private RemoteRequest rr_service;


    private User_Data_DB user_db;
    private Location_MDB location_mdb;
    private Interceptor_Data_DB interceptor_data_db;
    private Remote_Put_Location_Menus_DTO remote_put_location_menus_dto;


    ///SAVE SERVICE
    public boolean save_location(Location_Post_DTO location_post_dto) {

        log.info("CRUD_SERVICE: Entered the AddLocation to database");

        if (cc_service.check_location_post_dto(location_post_dto)) {
            location_mdb = cc_service.DtoDocument_convert(location_post_dto);

            try {

                log.info("The restaurant code is :" + location_mdb.getRestaurantcode());
                location_repository.save(location_mdb);
            } catch (DataIntegrityViolationException e) {
                throw new DuplicateLocationCodeFoundException("RestaurantCode Already In Use:  Please Use Different Code Adding Location");
            }

            log.info("CRUD_SERVICE: Successfully Exited the AddLocation to database");
            return true;
        } else {
            log.info("CRUD_SERVICE: UNSUCCESSFULLY Exited the AddLocation to database");
            return false;
        }
    }


    //PAGINATION SERVICE
    public MappingJacksonValue findLocationsWithPaginationSorting_filtering_location(int offset, int pageSize, Optional<String> sort_field, Optional<Set<String>> filter_field) {
        log.info("CRUD_SERVICE: Entered into the Pagination And Sorting and filtering");
        Page<Location_MDB> locations_page = location_repository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(sort_field.orElse("id"))));
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(filter_field.orElse(cc_service.getAllLocationFields()));
        FilterProvider filters = new SimpleFilterProvider().addFilter("ModelLocation", filter);
        MappingJacksonValue mapping = new MappingJacksonValue(locations_page);
        mapping.setFilters(filters);
        log.info("CRUD_SERVICE: Exited into the Pagination And  Sorting and filtering");
        return mapping;

    }


    ///////////Get by field

    public MappingJacksonValue find_value_location(String value, Optional<String> search_field, Optional<Set<String>> filter_fields) {
        log.info("CRUD_SERVICE: Entered into the GET BY ID SERVICE");
        if (search_field.orElse("id").equals("restaurantcode")) {
            location_mdb = location_repository.findByRestaurantcode(value);
        } else if (search_field.orElse("id").equals("id")) {
            Long id = Long.parseLong(value);
            Optional<Location_MDB> location_optional = location_repository.findById(id);
            if (!(location_optional.isPresent())) {
                throw new UserNotFoundException("Cannot find the requested data for the given value: " + value);
            }
            location_mdb = location_optional.get();

        } else {
            throw new NoFieldPresentException("Field:  " + search_field.get() + " Not present please Select valid Field ex:id or restaurant_code");
        }
        if (location_mdb == null) {
            throw new UserNotFoundException("Cannot find the requested data for the given value: " + value);
        } else {
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(filter_fields.orElse(cc_service.getAllLocationFields()));
            FilterProvider filters = new SimpleFilterProvider().addFilter("ModelLocation", filter);
            MappingJacksonValue mapping = new MappingJacksonValue(location_mdb);
            mapping.setFilters(filters);
            log.info("CRUD_SERVICE: SUCCESSFULLY  EXITED FROM GET BY ID SERVICE");
            return mapping;
        }
    }


    ////update Service
    public void update_service_location(Location_Put_DTO location_put_dto) {
        log.info("CRUD_SERVICE: Entered into UPDATE SERVICE");
        int check_val = 0;


        if (!(location_repository.findById(location_put_dto.getId())).isPresent())
            throw new UserNotFoundException("Location with ID " + location_put_dto.getId() + " not present");

        remote_put_location_menus_dto = cc_service.create_remote_put_location_menus_dto(location_repository.findById(location_put_dto.getId()), location_put_dto);

        try {
            location_repository.findById(location_put_dto.getId()).map(location_record -> {
                location_record.setRestaurantcode(location_put_dto.getRestaurant_code());
                location_record.setRestaurantname(location_put_dto.getRestaurant_name());
                location_record.setRestaurantpincode(location_put_dto.getRestaurant_pin_code());
                return location_repository.save(location_record);
            });
        } catch (DataIntegrityViolationException e) {
            check_val = 1;
            throw new DuplicateLocationCodeFoundException("Location code already existed please use another one");
        } finally {
            if (check_val == 1) {
                check_val = 0;
            } else {
                Integer response = rr_service.remote_update_location_menus(remote_put_location_menus_dto);
                if (response != 0) {
                    log.info("Cannot return proper response from MENU service  transaction failed");
                    throw new RemoteServerException("Sorry transaction failed due to remote response");
                }
            }

        }
        log.info("CRUD_SERVICE: EXITED FROM  UPDATE SERVICE");

    }


    /////////////Delete service
    public Boolean delete_service_location(Long id) {
        log.info("CRUD_SERVICE: Entered into DELETED SERVICE");
        if (!(location_repository.findById(id).isPresent()))
            throw new UserNotFoundException("Location with ID " + id + " not present");
        if (rr_service.remote_delete_location_menus(location_repository.findById(id).get().getRestaurantcode()) == 0) {
            location_repository.deleteById(id);
            log.info("CRUD_SERVICE: Exited into DELETED SERVICE");
            return true;
        }
        log.info("CRUD_SERVICE: Exited unsuccessfully from  DELETED SERVICE");
        return false;


    }


///////////////Rest api timing service


    ////////adding interceptor data
    public void add_interceptor_data(List data) {
        log.info("CRUD SERVICES:Entered into the add_interceptor data ");
        interceptor_data_db = new Interceptor_Data_DB();
        interceptor_data_db.setTimemillisec((Long) data.get(0));
        interceptor_data_db.setUrl((String) data.get(2));
        interceptor_data_db.setId(0);
        interceptor_data_db.setDate(new Date());
        interceptor_data_db.setApiname("LOCATION");
        interceptor_data_db.setServicename((String) data.get(1));
        interceptor_repository.save(interceptor_data_db);
        log.info("CRUD SERVICES:Exited from the add_interceptor data");

    }

    //////////////getting interceptor data
    public Page<Interceptor_Data_DB> api_timing(int offset, int pageSize, String microservice) {
        log.info("CRUD_SERVICE: Entered into the api timing sender");

        System.out.println("Inside the  crud service api timing call in get API call");
        Page<Interceptor_Data_DB> data = interceptor_repository.findByApiname(microservice, PageRequest.of(offset, pageSize));
        log.info("CRUD_SERVICE: Exited from the api timing sender");
        return data;
    }


/////////user sing up service

    public boolean addUser(User_Data_DTO user_data_dto) {


        log.info("CRUD SERVICE: Inside the user signup service");
        user_db = cc_service.convertUser(user_data_dto);
        user_db.setUserpassword((new BCryptPasswordEncoder().encode(user_data_dto.getUser_password())));
        try {
            user_repository.save(user_db);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateUserFoundException("USER NAME IN USE: Choose Different USERNAME ");
        }

        log.info("CRUD SERVICE: Exited from the user signup service");
        return true;
    }


    ///Remote request service from MENUS TO LOCATION checking valid location_code and
    public Integer check_code_name(String code, String name) {

        log.info("CRUD_SERVICE :Entered check rest template service");
        location_mdb = location_repository.findByRestaurantcode(code);

        if (location_mdb == null) {
            log.debug("CRUD_SERVICE :Exited Successfully check rest template service");
            return 1;
        } else {
            log.info("CRUD_SERVICE :Exited Successfully check rest template service");
            if (!(location_mdb.getRestaurantname().equals(name)))
                return 2;
            return 0;
        }
    }


}



