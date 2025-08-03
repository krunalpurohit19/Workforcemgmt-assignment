package com.yourcompany.workforcemgmt.service;

import com.yourcompany.workforcemgmt.dto.*;
import com.yourcompany.workforcemgmt.model.enums.Priority;

import java.util.List;


public interface TaskManagementService {

    List<TaskManagementDto> createTasks(TaskCreateRequest request);
    List<TaskManagementDto> updateTasks(UpdateTaskRequest request);
    String assignByReference(AssignByReferenceRequest request);
    List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request);
    TaskManagementDto findTaskById(Long id);
    void updateTaskPriority(UpdateTaskPriorityRequest request);
    List<TaskManagementDto> fetchTasksByPriority(Priority priority);
}
