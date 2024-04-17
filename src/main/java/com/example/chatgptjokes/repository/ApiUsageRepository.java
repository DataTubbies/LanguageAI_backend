package com.example.chatgptjokes.repository;

import com.example.chatgptjokes.entity.ApiUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiUsageRepository extends JpaRepository<ApiUsage, Integer> {

}
