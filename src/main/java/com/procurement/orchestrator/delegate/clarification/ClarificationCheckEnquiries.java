package com.procurement.orchestrator.delegate.clarification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.ClarificationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.ClarificationCommandType.CHECK_ENQUIRIES;

@Component
public class ClarificationCheckEnquiries implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ClarificationCheckEnquiries.class);

    private final ClarificationRestClient clarificationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public ClarificationCheckEnquiries(final ClarificationRestClient clarificationRestClient,
                                       final OperationService operationService,
                                       final ProcessService processService,
                                       final JsonUtil jsonUtil) {
        this.clarificationRestClient = clarificationRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode commandMessage = processService.getCommandMessage(CHECK_ENQUIRIES, context, jsonUtil.empty());
        JsonNode responseData = processService.processResponse(
                clarificationRestClient.execute(commandMessage),
                context,
                processId,
                taskId,
                commandMessage);
        if (Objects.nonNull(responseData)) {
            processResponse(execution, responseData, processId);
            operationService.saveOperationStep(execution, entity, context, commandMessage, responseData);
        }
    }

    private void processResponse(final DelegateExecution execution, final JsonNode responseData, final String processId) {
        final Boolean isEnquiryPeriodExpired = processService.getBoolean("isEnquiryPeriodExpired", responseData, processId);
        final Boolean allAnswered = processService.getBoolean("allAnswered", responseData, processId);
        execution.setVariable("isEnquiryPeriodExpired", isEnquiryPeriodExpired);
        execution.setVariable("allAnswered", allAnswered);
    }
}
