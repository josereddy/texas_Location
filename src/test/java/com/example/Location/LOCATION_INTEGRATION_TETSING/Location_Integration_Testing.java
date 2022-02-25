package com.example.Location.LOCATION_INTEGRATION_TETSING;


import com.example.Location.DTO.Location_Post_DTO;
import com.example.Location.DTO.Location_Put_DTO;
import com.example.Location.DTO.Remote_Put_Location_Menus_DTO;
import com.example.Location.DTO.User_Data_DTO;
import com.example.Location.Document.Location_MDB;
import com.example.Location.Entity.Interceptor_Data_DB;
import com.example.Location.Entity.User_Data_DB;
import com.example.Location.REPOSITORY.Interceptor_Repository;
import com.example.Location.REPOSITORY.Location_Repository;
import com.example.Location.REPOSITORY.User_Data_Repository;
import com.example.Location.SERVICES.RemoteRequest;
import com.example.Location.SERVICES.SequenceGeneratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.net.URI;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Location_Integration_Testing {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;


    private Location_MDB location_mdb = new Location_MDB();
    private Location_Post_DTO location_post_dto = new Location_Post_DTO();
    private User_Data_DB user_data_db = new User_Data_DB();
    ///repositories
    @Autowired
    private Location_Repository location_repository;
    @Autowired
    private SequenceGeneratorService seq_service;
    @Autowired
    private Interceptor_Repository interceptor_repository;
    @Autowired
    private User_Data_Repository user_data_repository;

///////services

    @MockBean
    private RemoteRequest remoteRequest;

    @BeforeEach
    public void setup() {
        location_repository.deleteAll();
        user_data_repository.deleteAll();
        user_data_db.setUserpassword((new BCryptPasswordEncoder().encode("jose@")));
        user_data_db.setUsername("jose");
        user_data_db.setUserroll("ADMIN");
        user_data_repository.save(user_data_db);
        location_post_dto.setRestaurant_code("usa-test-1000");
        location_post_dto.setRestaurant_name("usa-test-chicken");
        location_post_dto.setRestaurant_pin_code("11231");

        ////////
        location_mdb.setId(seq_service.generateSequence(Location_MDB.SEQUENCE_NAME));
        location_mdb.setRestaurantcode("usa-test-1000");
        location_mdb.setRestaurantname("usa-test-chicken");
        location_mdb.setRestaurantpincode("44114");
    }


    /////////location_post

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("POST_LOCATION_TEST:1")
    @Test
    @Order(1)
    public void Location_Post_Positive() throws Exception {

        // given - precondition or setup
        String Expected = "Location Data Successfully Added to DataBase";

//
//        BDDMockito.given(cr_service.save_location(any(Location_Post_DTO.class))).willReturn(true);
//         when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post(URI.create("/locations/post/add_location"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(location_post_dto)));

        // then - verify the result or output using assert statements
        response.andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(Expected));
//        System.out.println("--------------->"+result.getResponse().toString());
        assertEquals(Expected, "Location Data Successfully Added to DataBase");
        assertThat("ss").isEqualTo("ss");
//       verify("ss",times(1)).getClass();


    }


    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("POST_LOCATION_TEST:2")
    @Test
    @Order(2)
    public void Location_Post_TEST2() throws Exception {

        // given - precondition or setup
        location_repository.save(location_mdb);
        String message = "RestaurantCode Already In Use:  Please Use Different Code Adding Location";

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post(URI.create("/locations/post/add_location"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(location_post_dto)));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", CoreMatchers.is((message))));
    }


///////////// get

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_LOCATION TEST:1 default")
    @Test
    @Order(3)
    public void Location_get_BYPAGINATION_TEST1() throws Exception {

        // given - precondition or setup
        List<Location_MDB> list_location_mdb = new ArrayList();
        list_location_mdb.add(location_mdb);
        list_location_mdb.add(new Location_MDB(seq_service.generateSequence(Location_MDB.SEQUENCE_NAME), "usa-test-1001", "usa-test-chicken", "44114"));
        location_repository.saveAll(list_location_mdb);
        Integer size = 2, offset = 0, pageSize = 2;


        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/locations/get/pagination_sort_filtering_location/{offset}/{pageSize}", offset, pageSize));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.size", CoreMatchers.is(size)))
                .andExpect(jsonPath("$.content[0].restaurantcode", CoreMatchers.is("usa-test-1000")))
                .andExpect(jsonPath("$.content[1].restaurantcode", CoreMatchers.is("usa-test-1001")));
    }


    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_LOCATION TEST:2 filtering using Restaurantcode")
    @Test
    @Order(4)
    public void Location_get_BYPAGINATION_TEST2() throws Exception {


        // given - precondition or setup
        List<Location_MDB> list_location_mdb = new ArrayList();
        list_location_mdb.add(location_mdb);
        list_location_mdb.add(new Location_MDB(seq_service.generateSequence(Location_MDB.SEQUENCE_NAME), "usa-test-1001", "usa-test-chicken", "44114"));
        location_repository.saveAll(list_location_mdb);
        Integer size = 2, offset = 0, pageSize = 2;
        MultiValueMap<String, String> filter_fields = new LinkedMultiValueMap<>();
        filter_fields.add("filter_fields", "restaurantcode");


        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/locations/get/pagination_sort_filtering_location/{offset}/{pageSize}", offset, pageSize).params(filter_fields));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.size", CoreMatchers.is(size)))
                .andExpect(jsonPath("$.content[0].restaurantcode", CoreMatchers.is("usa-test-1000")))
                .andExpect(jsonPath("$.content[1].restaurantcode", CoreMatchers.is("usa-test-1001")));
    }


    /////get by id

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_LOCATION_BY_FIELD_ TEST:1 default")
    @Test
    @Order(5)
    public void Location_get_ByFIELD_TEST1() throws Exception {

        // given - precondition or setup
        String value = location_mdb.getId().toString();
        location_repository.save(location_mdb);
        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/locations/get/search_location/{value}", value));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantcode", CoreMatchers.is("usa-test-1000")))
                .andExpect(jsonPath("$.restaurantname", CoreMatchers.is("usa-test-chicken")));
    }

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_LOCATION_BY_FIELD_ TEST:2 Restaurantcode")
    @Test
    @Order(6)
    public void Location_get_BYFIELD_TEST2() throws Exception {

        // given - precondition or setup
        String value = location_mdb.getRestaurantcode();
        location_repository.save(location_mdb);
        MultiValueMap<String, String> filter_fields = new LinkedMultiValueMap<>();
        filter_fields.add("filter_fields", "restaurantcode");
        String search_field = "restaurantcode";
        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/locations/get/search_location/{value}", value).params(filter_fields).param("search_field", search_field));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantcode", CoreMatchers.is("usa-test-1000")));
    }

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_LOCATION_BY_FIELD_ TEST:3   EXCEPTION HANDLING VALUE NOT FOUND")
    @Test
    @Order(7)
    public void Location_get_FIELD_TEST3() throws Exception {

        // given - precondition or setup
        String value = "codenotpresent";
        location_repository.save(location_mdb);
        MultiValueMap<String, String> filter_fields = new LinkedMultiValueMap<>();
        filter_fields.add("filter_fields", "restaurantcode");
        String search_field = "restaurantcode";
        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/locations/get/search_location/{value}", value).params(filter_fields).param("search_field", search_field));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", CoreMatchers.is("Cannot find the requested data for the given value: " + value)));
    }


    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_LOCATION_BY_FIELD_ TEST:4   EXCEPTION HANDLING FIELD NOT FOUND")
    @Test
    @Order(8)
    public void Location_get_BYFIELD_TEST4() throws Exception {

        // given - precondition or setup
        String value = location_mdb.getId().toString();
        location_repository.save(location_mdb);
        MultiValueMap<String, String> filter_fields = new LinkedMultiValueMap<>();
        filter_fields.add("filter_fields", "restaurant");
        String search_field = "field not valid";
        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/locations/get/search_location/{value}", value).params(filter_fields).param("search_field", search_field));
        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", CoreMatchers.is("Field:  " + search_field + " Not present please Select valid Field ex:id or restaurant_code")));
    }


    ///////////////update

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("PUT _LOCATION_update_TEST:1 positive")
    @Test
    @Order(9)
    public void Location_update_test() throws Exception {

        // given - precondition or setup

        given(remoteRequest.remote_update_location_menus(any(Remote_Put_Location_Menus_DTO.class))).willReturn(0);

        location_repository.save(location_mdb);
        Location_Put_DTO location_put_dto = new Location_Put_DTO();
        location_put_dto.setId(location_mdb.getId());
        location_put_dto.setRestaurant_code("cl1001");
        location_put_dto.setRestaurant_name("nextnext");
        location_put_dto.setRestaurant_pin_code("444444");
        String expectedresult = "Data updated Successfully";

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/locations/put/update_location").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(location_put_dto)));
        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(expectedresult));
    }


    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("PUT_LOCATION_update_TEST:2 EXCEPTION IF Already code exist")
    @Test
   @Order(10)
    public void Location_update_test2() throws Exception {

        // given - precondition or setup

        given(remoteRequest.remote_update_location_menus(any(Remote_Put_Location_Menus_DTO.class))).willReturn(0);
        List<Location_MDB> list_location_mdb = new ArrayList();
        list_location_mdb.add(location_mdb);
        list_location_mdb.add(new Location_MDB(seq_service.generateSequence(Location_MDB.SEQUENCE_NAME), "usa-test-1001", "usa-test-chicken", "44114"));
        location_repository.saveAll(list_location_mdb);

        Location_Put_DTO location_put_dto = new Location_Put_DTO();
        location_put_dto.setId(location_mdb.getId());
        location_put_dto.setRestaurant_code("usa-test-1001");
        location_put_dto.setRestaurant_name("nextnext");
        location_put_dto.setRestaurant_pin_code("444444");
        String message = "Location code already existed please use another one";

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/locations/put/update_location").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(location_put_dto)));
        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", CoreMatchers.is(message)));
    }

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("PUT_LOCATION_update_TEST:3 EXCEPTION IF ID NOT PRESENT")
    @Test
    @Order(11)
    public void Location_update_test3() throws Exception {

        // given - precondition or setup

        given(remoteRequest.remote_update_location_menus(any(Remote_Put_Location_Menus_DTO.class))).willReturn(0);

        location_repository.save(location_mdb);
        Location_Put_DTO location_put_dto = new Location_Put_DTO();
        location_put_dto.setId(0l);
        location_put_dto.setRestaurant_code("usa-test-1001");
        location_put_dto.setRestaurant_name("nextnext");
        location_put_dto.setRestaurant_pin_code("444444");
        String message = "Location with ID " + location_put_dto.getId() + " not present";
        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/locations/put/update_location").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(location_put_dto)));


        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", CoreMatchers.is(message)));
    }


    //////////////delete

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("DELETE_LOCATION_TEST:1 default")
    @Test
    @Order(12)
    public void Location_delete_test() throws Exception {

        // given - precondition or setup

        given(remoteRequest.remote_delete_location_menus(any(String.class))).willReturn(0);

        location_repository.save(location_mdb);
        Long id = location_mdb.getId();
        String expectedresult = "Record with id -->" + id + " deleted";
        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(delete("/locations/delete/delete_location/{id}", id));


        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(expectedresult));
    }


    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("DELETE_LOCATION_TEST:2 EXCEPTION IF ID NOT PRESENT")
    @Test
    @Order(13)
    public void Location_delete_test2() throws Exception {

        // given - precondition or setup

        given(remoteRequest.remote_delete_location_menus(any(String.class))).willReturn(0);

        location_repository.save(location_mdb);
        Long id = 0l;
        String expectedresult = "Location with ID " + id + " not present";
        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(delete("/locations/delete/delete_location/{id}", id));


        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", CoreMatchers.is(expectedresult)));
    }


    /////////////////////////api timings get methods

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_PAGINATION API CALLS TEST1:default")
    @Test
    @Order(14)
    public void API_get_BY_PAGINATION_TEST1() throws Exception {

        // given - precondition or setup
        interceptor_repository.deleteAll();
        List<Interceptor_Data_DB> list_interceptor_data = new ArrayList<>();
        list_interceptor_data.add(new Interceptor_Data_DB(0, "LOCATION", "POST", "add_location", new Date(), 112l));
        list_interceptor_data.add(new Interceptor_Data_DB(0, "LOCATION", "POST", "add_location", new Date(), 112l));
        interceptor_repository.saveAll(list_interceptor_data);
        Long offset = 0l, pageSize = 2l;

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/locations/get/api_timing_location/{offset}/{pageSize}", offset, pageSize).param("Microservice", "LOCATION"));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.size", CoreMatchers.is(2)))
                .andExpect(jsonPath("$.content[0].servicename", CoreMatchers.is("POST")))
                .andExpect(jsonPath("$.content[1].url", CoreMatchers.is("add_location")));
    }


//    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("POST_USER SERVICE TEST:1 default")
    @Test
    @Order(15)
    public void post_user_service_TEST() throws Exception {


        // given - precondition or setup

        User_Data_DTO user_data_dto = new User_Data_DTO();
        user_data_dto.setUser_password("vijay@");
        user_data_dto.setUser_name("vijay");
        user_data_dto.setUser_roll("ADMIN");
        String expectedresult = "Data Successfully Added";


        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/locations/post/add_user_signup")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user_data_dto)));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(expectedresult));

    }




//    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("POST_USER SERVICE TEST:2 Exception Duplicate user")
    @Test
    @Order(16)
    public void post_user_service_TEST2() throws Exception {


        // given - precondition or setup

        User_Data_DB user_data_db1 = new User_Data_DB();
        user_data_db1.setUserpassword((new BCryptPasswordEncoder().encode("vijay@")));
        user_data_db1.setUsername("vijay");
        user_data_db1.setUserroll("ADMIN");
        user_data_repository.save(user_data_db1);

        User_Data_DTO user_data_dto = new User_Data_DTO();
        user_data_dto.setUser_password("vijay@");
        user_data_dto.setUser_name("vijay");
        user_data_dto.setUser_roll("ADMIN");
        String expectedresult = "USER NAME IN USE: Choose Different USERNAME ";


        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/locations/post/add_user_signup")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user_data_dto)));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", CoreMatchers.is(expectedresult)));

    }
}
