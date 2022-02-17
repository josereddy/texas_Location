package com.example.Location.REPOSITORY;

import com.example.Location.Document.Location_MDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface Location_Repository extends MongoRepository<Location_MDB, Long> {


    Location_MDB findByRestaurantcode(String value);
}