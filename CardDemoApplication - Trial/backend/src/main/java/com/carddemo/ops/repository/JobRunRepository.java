package com.carddemo.ops.repository;

import com.carddemo.ops.entity.JobRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JobRunRepository extends JpaRepository<JobRunEntity, String> {
    Optional<JobRunEntity> findFirstByJobNameAndStatusOrderByCreatedAtDesc(String jobName, String status);
}
