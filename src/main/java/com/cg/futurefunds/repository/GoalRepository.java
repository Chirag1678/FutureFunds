package com.cg.futurefunds.repository;

import com.cg.futurefunds.model.Goal;
import com.cg.futurefunds.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUser(User user);
}
