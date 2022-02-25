package com.example.Location.Service;


import com.example.Location.DTO.Location_Post_DTO;
import com.example.Location.Document.Location_MDB;
import com.example.Location.EXCEPTION.DuplicateLocationCodeFoundException;
import com.example.Location.REPOSITORY.Location_Repository;
import com.example.Location.SERVICES.Check_ConvertService;
import com.example.Location.SERVICES.CrudServices;
import com.example.Location.SERVICES.SequenceGeneratorService;
import org.assertj.core.api.Assert;
import org.assertj.core.api.AssertionInfo;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import  static org.mockito.BDDMockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CrudService_Test {


        @Mock
        private Location_Repository location_Repository;
        @Mock
        private Check_ConvertService cc_Service;

        @InjectMocks
        private CrudServices crudService;

        private  static  Location_Post_DTO location_post_dto= new Location_Post_DTO();
        private static Location_MDB location_mdb =new Location_MDB();



        @BeforeAll
        public static void setup_data()
    {
        ////////post_dto
        location_post_dto.setRestaurant_pin_code("44114");
        location_post_dto.setRestaurant_name("indian grills");
        location_post_dto.setRestaurant_code("ind-1001");


        /////original_db
        location_mdb.setId(1l);
        location_mdb.setRestaurantcode("ind-1001");
        location_mdb.setRestaurantname("indian grills");
        location_mdb.setRestaurantpincode("44114");


    }






//////////////////crud location save methods
        // JUnit test for saveEmployee method test:1
        @DisplayName("JUnit test for save_location method TEST:1")
        @Test
        public void location_post_test1(){

            // given - precondition or setup
            given(location_Repository.save(location_mdb)).willReturn(location_mdb);
            given(cc_Service.check_location_post_dto(location_post_dto)).willReturn(true);
            given(cc_Service.DtoDocument_convert(location_post_dto)).willReturn(location_mdb);
            // when -  action or the behaviour that we are going test
             Boolean result = crudService.save_location(location_post_dto);
             // then - verify the output
            assertThat(result).isTrue();
            verify(cc_Service,times(1)).check_location_post_dto(any(Location_Post_DTO.class));
        }

    // JUnit test for saveEmployee method test2:Handling exception
    @DisplayName("JUnit test for save_location method TEST:2 Handling Exception")
    @Test
    public void location_post_test2(){

        // given - precondition or setup
        given(location_Repository.save(location_mdb)).willThrow(DataIntegrityViolationException.class);
        given(cc_Service.check_location_post_dto(location_post_dto)).willReturn(true);
        given(cc_Service.DtoDocument_convert(location_post_dto)).willReturn(location_mdb);
        // when -  action or the behaviour that we are going test

        Assertions.assertThrows(DuplicateLocationCodeFoundException.class,
                ()->{crudService.save_location(location_post_dto);});
        // then - verify the output
//        assertThat(result).isTrue();
        verify(cc_Service,times(1)).check_location_post_dto(any(Location_Post_DTO.class));
        verify(location_Repository,times(1)).save(any(Location_MDB.class));
        }












}