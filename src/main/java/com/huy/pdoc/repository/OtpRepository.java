package com.huy.pdoc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.huy.pdoc.entity.Otp;
import com.huy.pdoc.entity.User;

import jakarta.transaction.Transactional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, String> {
    @Transactional
    public void deleteByUser(User user);

    public Optional<Otp> findByUser(User user);
}
