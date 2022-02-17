package com.example.Location.REPOSITORY;

import com.example.Location.Entity.Interceptor_Data_DB;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Interceptor_Repository extends JpaRepository<Interceptor_Data_DB, Integer> {


    Page<Interceptor_Data_DB> findByApiname(String microservice,Pageable page);
}
