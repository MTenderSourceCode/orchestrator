package com.procurement.orchestrator.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OperationStepEntity {

    private String processId;

    private String taskId;

    private String operationId;

    private Date date;

    private String context;

    private String requestData;

    private String responseData;

    private String cpId;

}
