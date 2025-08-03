package com.yourcompany.workforcemgmt.controller;


import com.yourcompany.workforcemgmt.common.model.response.Response;
import com.yourcompany.workforcemgmt.dto.*;
import com.yourcompany.workforcemgmt.model.enums.Priority;
import com.yourcompany.workforcemgmt.service.TaskManagementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task-mgmt")
public class TaskManagementController {
    private final TaskManagementService taskManagementService;

    public TaskManagementController(TaskManagementService taskManagementService) {
        this.taskManagementService = taskManagementService;
    }

    @GetMapping("abc")
    public String sayHello(){
        return "Hello";
    }

    @GetMapping("/{id}")
    public Response<TaskManagementDto> getTaskById(@PathVariable Long id) {
        return new Response<>(taskManagementService.findTaskById(id));
    }

    @PostMapping("/create")
    public Response<List<TaskManagementDto>> createTasks(@RequestBody TaskCreateRequest request) {
        return new Response<>(taskManagementService.createTasks(request));
    }

    @PostMapping("/update")
    public Response<List<TaskManagementDto>> updateTasks(@RequestBody UpdateTaskRequest request) {
        return new Response<>(taskManagementService.updateTasks(request));
    }

    @PostMapping("/assign-by-ref")
    public Response<String> assignByReference(@RequestBody AssignByReferenceRequest request) {
        return new Response<>(taskManagementService.assignByReference(request));
    }

    
    @PostMapping("/fetch-by-date/v2")
    public Response<List<TaskManagementDto>> fetchByDate(@RequestBody TaskFetchByDateRequest request) {
        return new Response<>(taskManagementService.fetchTasksByDate(request));
    }


//    Added for Priority
    @PatchMapping("/update-priority")
    public Response<String> updateTaskPriority(@RequestBody UpdateTaskPriorityRequest request) {
        taskManagementService.updateTaskPriority(request);
        return new Response<>("Priority updated successfully.");
    }

    @GetMapping("/tasks/priority/{priority}")
    public Response<List<TaskManagementDto>> getTasksByPriority(@PathVariable Priority priority) {
        return new Response<>(taskManagementService.fetchTasksByPriority(priority));
    }

}
