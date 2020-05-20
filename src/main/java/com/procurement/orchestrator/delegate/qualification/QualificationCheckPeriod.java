package com.procurement.orchestrator.delegate.qualification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.delegate.clarification.ClarificationCheckPeriod;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.QualificationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.QualificationCommandType.CHECK_PERIOD;

@Component
public class QualificationCheckPeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ClarificationCheckPeriod.class);
    private static final String ATTRIBUTE_NAME = "isPreQualificationPeriodChanged";
    private static final String VARIABLE_NAME = "isPreQualificationPeriodChanged";

    private final QualificationRestClient qualificationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public QualificationCheckPeriod(
        final QualificationRestClient qualificationRestClient,
        final OperationService operationService,
        final ProcessService processService,
        final JsonUtil jsonUtil
    ) {
        this.qualificationRestClient = qualificationRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();

        final JsonNode period = processService.getPreQualificationPeriod(jsonData, processId);
        final JsonNode commandMessage = processService.getCommandMessage(CHECK_PERIOD, context, period);
        if (LOG.isDebugEnabled()) {
            LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));
        }

        final ResponseEntity<ResponseDto> response = qualificationRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled()) {
            LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));
        }

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled()) {
            LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));
        }

        if (responseData != null) {
            final Boolean isPreQualificationPeriodChanged = processService.getBoolean(ATTRIBUTE_NAME, responseData, processId);
            execution.setVariable(VARIABLE_NAME, isPreQualificationPeriodChanged);
            if (LOG.isDebugEnabled())
                LOG.debug("COMMAND ({}) IN CONTEXT PUT THE VARIABLE '{}' WITH THE VALUE '{}'.", context.getOperationId(), VARIABLE_NAME, isPreQualificationPeriodChanged);

            final JsonNode step = processService.setPreQualificationPeriod(jsonData, responseData, processId);
            if (LOG.isDebugEnabled())
                LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

            operationService.saveOperationStep(execution, entity, commandMessage, step);
        }
    }
}
