package com.procurement.orchestrator.delegate.ein;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AccessCreateEin implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessCreateEin.class);

    private final AccessRestClient accessRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public AccessCreateEin(final AccessRestClient accessRestClient,
                           final OperationService operationService,
                           final ProcessService processService,
                           final JsonUtil jsonUtil) {
        this.accessRestClient = accessRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
        final String processId = execution.getProcessInstanceId();
        final String operationId = params.getOperationId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode responseData = processService.processResponse(
                accessRestClient.createEin(params.getOwner(), jsonData),
                processId,
                operationId,
                taskId);
        if (Objects.nonNull(responseData))
            operationService.saveOperationStep(
                    execution,
                    entity,
                    addDataToParams(params, responseData, processId, operationId),
                    responseData);
    }

    private Params addDataToParams(final Params params,
                                   final JsonNode responseData,
                                   final String processId,
                                   final String operationId) {
        params.setToken(processService.getValue("token", responseData, processId, operationId));
        params.setCpid(processService.getValue("ocid", responseData, processId, operationId));
        return params;
    }


}
