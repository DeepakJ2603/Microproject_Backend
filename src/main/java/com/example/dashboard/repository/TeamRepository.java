package com.example.dashboard.repository;

import com.example.dashboard.entity.Manager;
import com.example.dashboard.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

	List<Team> findByManager(Manager manager);

	List<Team> findByManagerId(Long managerId);

	boolean existsByNameAndManager(String name, Manager manager);

	@Query("SELECT t FROM Team t WHERE t.manager = :manager " + "ORDER BY t.createdAt DESC")
	List<Team> findByManagerOrderByCreatedAtDesc(@Param("manager") Manager manager);
}