package com.huy.pdoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.huy.pdoc.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,String>{ 
    public Role findByAuthority(String authority);  
}
