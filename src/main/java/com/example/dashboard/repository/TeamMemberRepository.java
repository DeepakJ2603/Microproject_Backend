package com.example.dashboard.repository;

import com.example.dashboard.entity.Developer;
import com.example.dashboard.entity.Team;
import com.example.dashboard.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember>     findByTeam(Team team);
    List<TeamMember>     findByDeveloper(Developer developer);
    Optional<TeamMember> findByTeamAndDeveloper(Team team, Developer developer);
    boolean              existsByTeamAndDeveloper(Team team, Developer developer);
    void                 deleteByTeamAndDeveloper(Team team, Developer developer);
	void deleteAllByTeam(Team team);
	long countByTeam(Team team);
}