package com.example.Location.LOCATION_CONTROLLER;


import com.example.Location.DTO.Location_Post_DTO;
import com.example.Location.DTO.Location_Put_DTO;
import com.example.Location.DTO.User_Data_DTO;
import com.example.Location.Document.Location_MDB;
import com.example.Location.EXCEPTION.UserDataIncorrectFormatException;
import com.example.Location.Entity.Interceptor_Data_DB;
import com.example.Location.Entity.User_Data_DB;
import com.example.Location.SERVICES.Check_ConvertService;
import com.example.Location.SERVICES.CrudServices;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/locations")


@Tag(name = "Locations", description = "Manages All location related data about restaurant")
@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Operation Success",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "400", description = "CHECK FOR SCHEMA BEFORE SENDING",
                content = @Content(schema = @Schema(implementation = Location_MDB.class),
                        mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "401", description = "Login credentials mismatch",
                content = @Content(schema = @Schema(implementation = User_Data_DB.class),
                        mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "403", description = "Login USER LEVEL mismatch",
                content = @Content(schema = @Schema(implementation = User_Data_DB.class),
                        mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "Mismatch with RULES While entering details",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
})


public class Location_Controller {


    private static final Logger log = LogManager.getLogger(Location_Controller.class.getName());
    @Autowired
    private CrudServices cr_service;
    @Autowired
    private Check_ConvertService cc_service;


    ///Add Location API
    @Operation(summary = "[POST LOCATION DATA TO MONGO DB]", description = "New record wil be Added into the database locations")
    @SecurityRequirement(name = "check")
    @PostMapping("/post/add_location")
    public String add_Location(@RequestBody Location_Post_DTO location_post_dto) {
        System.out.println("hello---------------------->in only controller ");
        log.info("REST CALL: ENTERED ADD LOCATION DATA ");
        if (cr_service.save_location(location_post_dto)) {
            log.info("REST CALL: ADD LOCATION DATA Successfully EXITED ");
            return "Location Data Successfully Added to DataBase";
        } else
            throw new UserDataIncorrectFormatException("Location_Data is incorrect");


    }


    ///Pagination and Sorting And FILTERING  API
    @Operation(summary = "[GET PAGINATED LOCATION DATA with sorted by and filtering]", description = "Location data from Location table in pagination with sorting,filtering is obtained ")
    @Parameter(name = "offset", example = "0", required = true, description = "PAGE OFFSET", in = ParameterIn.PATH)
    @Parameter(name = "pageSize", example = "5", required = true, description = "PAGE SIZE", in = ParameterIn.PATH)
    @Parameter(name = "sort_field", example = "id", required = false, description = "SORTING FIELD  /default=id  /ex:id or restaurantcode ....", in = ParameterIn.QUERY)
    @Parameter(name = "filter_fields", required = false, description = "Filtering FIELDS /default=all fields  /ex:id,restaurantname.... ", in = ParameterIn.QUERY)
    @SecurityRequirement(name = "check")
    @GetMapping("/get/pagination_sort_filtering_location/{offset}/{pageSize}")
    private MappingJacksonValue getProductsWithPagination_SortAndFiltering(@PathVariable("offset") int offset, @PathVariable("pageSize") int pageSize,
                                                                           @RequestParam("sort_field") Optional<String> sort_field, @RequestParam ("filter_fields") Optional<Set<String>> filter_fields) {
        log.info("REST CALL: Entered Pagination and Sorting and filtering");

        System.out.println("filter_fields--->"+filter_fields);
        MappingJacksonValue location_list = cr_service.findLocationsWithPaginationSorting_filtering_location(offset, pageSize, sort_field, filter_fields);
        log.debug("REST CALL:Exited Pagination AND Sorting AND Filtering");
        return location_list;

    }


    //////Search by field API
    @SecurityRequirement(name = "check")
    @Operation(summary = "[GET the record by field and value]", description = "Get the single record for given field and value ")
    @Parameter(name = "value", example = "1", required = true, description = "Field Value ", in = ParameterIn.PATH)
    @Parameter(name = "search_field", example = "id", required = false, description = "Searching Field /default=id   /ex:id or restaurantcode  only ", in = ParameterIn.QUERY)
    @Parameter(name = "filter_fields", required = false, description = "Filtering Fields  /default=id   /ex:id,restaurantcode.....", in = ParameterIn.QUERY)
    @GetMapping("/get/search_location/{value}")
    public MappingJacksonValue search_value_location(@PathVariable("value") String value, @RequestParam("search_field") Optional<String> search_field,
                                                     @RequestParam("filter_fields")Optional<Set<String>> filter_fields) {
        log.info("REST API: Entered SEARCH Service");
        System.out.println("value--->"+value);
        System.out.println("search_field--->"+search_field);
        System.out.println("filter_fields--->"+filter_fields);

        MappingJacksonValue location_list = cr_service.find_value_location(value, search_field, filter_fields);
        log.debug("REST API: Exited Search Service");
        return location_list;
    }


    /////////   Update location API
    @SecurityRequirement(name = "check")
    @Operation(summary = "[Update the record based on id]", description = "A new value for the particular value is appeared in Database ")
    @PutMapping("/put/update_location")
    public String update_location(@RequestBody Location_Put_DTO location_put_dto) {
        log.info("REST API: Entered  Update Service");
        cr_service.update_service_location(location_put_dto);
        return "Data updated Successfully";
    }


    ///////////   Delete Location Api
    @SecurityRequirement(name = "check")
    @Operation(summary = "[Delete the location record based on id]", description = "No more data available in the database with the chosen id")
    @Parameter(name = "id", example = "1", required = true, description = "Id VALUE", in = ParameterIn.PATH)
    @DeleteMapping("/delete/delete_location/{id}")
    public String delete_location(@PathVariable("id") Long id) {
        log.info("REST API: Entered Deleted Location ");
        if (cr_service.delete_service_location(id)) {
            log.debug("REST API: Exited Deleted Location");
            return "Record with id -->" + id + " deleted";
        } else
            return "Record with id--->" + id + " not deleted";

    }


    /////////////////////  REQUEST FOR API TIMINGS

    @GetMapping("/get/api_timing_location/{offset}/{pageSize}")
    @SecurityRequirement(name = "check")
    @Operation(summary = "[GET The All Location Api timings]", description = "Retrieve data in pagination format from db")
    @Parameter(name = "offset", example = "1", required = true, description = " PAGE OFFSET", in = ParameterIn.PATH)
    @Parameter(name = "pageSize", example = "1", required = true, description = "PAGE SIZE", in = ParameterIn.PATH)
    @Parameter(name = "Microservice", example = "LOCATION", required = true, description = "MICROSERVICE NAME", in = ParameterIn.QUERY)

    public Page<Interceptor_Data_DB> get_api_timing(@PathVariable("offset") int offset, @PathVariable("pageSize") int pageSize
            , @RequestParam("Microservice") String microservice) {
        log.info("REST API:Entered into Api TIME sender");
        System.out.println("Inside the api timing call in get API call");
        Page<Interceptor_Data_DB> data = cr_service.api_timing(offset, pageSize, microservice);
        log.debug("REST API:Exited FROM API TIME SENDER");
        return data;
    }


//////user sign up API

    @Operation(summary = "[POST The USER,PASSWORD,ROLE]", description = "post the new record in user database")
    @PostMapping("/post/add_user_signup")
    public String add_signup(@RequestBody User_Data_DTO user) {

        log.info("CRUD_SERVICE: Entered into the User Sign up API");
        if (cr_service.addUser(user)) {
            log.info("CRUD_SERVICE: Exited successfully from  the  sign up API");
            return "Data Successfully Added";
        } else {
            log.info("CRUD_SERVICE: Exited Un-successfully from  the  sign up API");
            return "Data cannot be added successfully";
        }
    }


///////////  Rest Template service API

    @Hidden
    @SecurityRequirement(name = "check")
    @GetMapping("/get/check_menus_location/{code}/{name}")
    public Integer check_location_code_name(@PathVariable("code") String code, @PathVariable("name") String name) {

        log.info("REST API:Inside the check Rest Template Service");
        Integer tested_value = cr_service.check_code_name(code, name);
        log.debug("REST API:Exited check Rest Template Service");
        return tested_value;
    }


}


