package com.huy.pdoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.huy.pdoc.entity.ApiLog;

@Repository
public interface ApiLogRepository extends JpaRepository<ApiLog,String>{
    
}
