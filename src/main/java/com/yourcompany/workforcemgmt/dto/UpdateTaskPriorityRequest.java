package com.yourcompany.workforcemgmt.dto;

import lombok.Data;
import com.yourcompany.workforcemgmt.model.enums.Priority;

@Data
public class UpdateTaskPriorityRequest {

    private Long taskId;
    private Priority newPriority;
}
