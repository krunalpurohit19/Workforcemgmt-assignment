package com.yourcompany.workforcemgmt.repository;

import com.yourcompany.workforcemgmt.model.TaskManagement;
import com.yourcompany.workforcemgmt.common.model.enums.ReferenceType;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Optional<TaskManagement> findById(Long id);
    TaskManagement save(TaskManagement task);
    List<TaskManagement> findAll();
    List<TaskManagement> findByReferenceIdAndReferenceType(Long referenceId, ReferenceType referenceType);
    List<TaskManagement> findByAssigneeIdIn(List<Long> assigneeIds);
}
