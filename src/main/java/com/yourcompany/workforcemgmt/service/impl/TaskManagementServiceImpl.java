package com.yourcompany.workforcemgmt.service.impl;

import com.yourcompany.workforcemgmt.common.exception.ResourceNotFoundException;
import com.yourcompany.workforcemgmt.dto.*;
import com.yourcompany.workforcemgmt.mapper.ITaskManagementMapper;
import com.yourcompany.workforcemgmt.model.TaskManagement;
import com.yourcompany.workforcemgmt.model.enums.Priority;
import com.yourcompany.workforcemgmt.model.enums.Task;
import com.yourcompany.workforcemgmt.model.enums.TaskStatus;
import com.yourcompany.workforcemgmt.repository.TaskRepository;
import com.yourcompany.workforcemgmt.service.TaskManagementService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskManagementServiceImpl implements TaskManagementService {
    private final TaskRepository taskRepository;
    private final ITaskManagementMapper taskMapper;

    public TaskManagementServiceImpl(TaskRepository taskRepository, ITaskManagementMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskManagementDto findTaskById(Long id) {
        TaskManagement task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return taskMapper.modelToDto(task);
    }

    @Override
    public List<TaskManagementDto> createTasks(TaskCreateRequest createRequest) {
        List<TaskManagement> createdTasks = new ArrayList<>();
        for (TaskCreateRequest.RequestItem item : createRequest.getRequests()) {
            TaskManagement newTask = new TaskManagement();
            newTask.setReferenceId(item.getReferenceId());
            newTask.setReferenceType(item.getReferenceType());
            newTask.setTask(item.getTask());
            newTask.setAssigneeId(item.getAssigneeId());
            newTask.setPriority(item.getPriority());
            newTask.setTaskDeadlineTime(item.getTaskDeadlineTime());
            newTask.setStatus(TaskStatus.ASSIGNED);
            newTask.setDescription("New task created.");
            createdTasks.add(taskRepository.save(newTask));
        }
        return taskMapper.modelListToDtoList(createdTasks);
    }

    @Override
    public List<TaskManagementDto> updateTasks(UpdateTaskRequest updateRequest) {
        List<TaskManagement> updatedTasks = new ArrayList<>();
        for (UpdateTaskRequest.RequestItem item : updateRequest.getRequests()) {
            TaskManagement task = taskRepository.findById(item.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));

            if (item.getTaskStatus() != null) {
                task.setStatus(item.getTaskStatus());
            }
            if (item.getDescription() != null) {
                task.setDescription(item.getDescription());
            }
            updatedTasks.add(taskRepository.save(task));
        }
        return taskMapper.modelListToDtoList(updatedTasks);
    }

    @Override
    public String assignByReference(AssignByReferenceRequest request) {
        List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
        List<TaskManagement> existingTasks = taskRepository
                .findByReferenceIdAndReferenceType(request.getReferenceId(), request.getReferenceType());

        for (Task taskType : applicableTasks) {
            List<TaskManagement> tasksOfType = existingTasks.stream()
                    .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
                    .collect(Collectors.toList());

            if (!tasksOfType.isEmpty()) {
                for (TaskManagement taskToUpdate : tasksOfType) {
                    taskToUpdate.setStatus(TaskStatus.CANCELLED); //Old Task for the previous employee is marked as CANCEL
                    taskRepository.save(taskToUpdate);
                }
            }

            TaskManagement newTask = new TaskManagement();
            newTask.setReferenceId(request.getReferenceId());
            newTask.setReferenceType(request.getReferenceType());
            newTask.setTask(taskType);
            newTask.setAssigneeId(request.getAssigneeId());
            newTask.setStatus(TaskStatus.ASSIGNED);
            newTask.setDescription("Reassigned by manager.");
            newTask.setTaskDeadlineTime(System.currentTimeMillis() + 86400000);
            taskRepository.save(newTask);

        }

        return "Tasks reassigned successfully for reference " + request.getReferenceId();
    }

    @Override
    public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request) {
        List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());

        long start = request.getStartDate();
        long end = request.getEndDate();

        List<TaskManagement> filteredTasks = tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.CANCELLED && task.getStatus() != TaskStatus.COMPLETED)
                .filter(task -> {
                    Long deadline = task.getTaskDeadlineTime();
                    if (deadline == null) return false;

                    return (deadline >= start && deadline <= end) || (deadline < start);
                })
                .collect(Collectors.toList());

        return taskMapper.modelListToDtoList(filteredTasks);
    }

    @Override
    public void updateTaskPriority(UpdateTaskPriorityRequest request) {
        TaskManagement task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + request.getTaskId()));

        task.setPriority(request.getNewPriority());
        taskRepository.save(task);
    }

    @Override
    public List<TaskManagementDto> fetchTasksByPriority(Priority priority) {
        List<TaskManagement> tasks = taskRepository.findAll();
        List<TaskManagement> filtered = tasks.stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());

        return taskMapper.modelListToDtoList(filtered);
    }

}
