package com.procurement.orchestrator.delegate.contracting;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Outcome;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.ContractingRestClient;
import com.procurement.orchestrator.service.NotificationService;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static com.procurement.orchestrator.domain.commands.ContractingCommandType.CREATE_CAN;

@Component
public class ContractingCreateCan implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ContractingCreateCan.class);

    private final ContractingRestClient contractingRestClient;
    private final NotificationService notificationService;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public ContractingCreateCan(final ContractingRestClient contractingRestClient,
                                final NotificationService notificationService,
                                final OperationService operationService,
                                final ProcessService processService,
                                final JsonUtil jsonUtil) {
        this.contractingRestClient = contractingRestClient;
        this.notificationService = notificationService;
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

        final JsonNode commandMessage = processService.getCommandMessage(CREATE_CAN, context, jsonData);
        if (LOG.isDebugEnabled())
            LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = contractingRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

        if (responseData != null) {
            final JsonNode step = processService.addCan(jsonData, responseData, processId);
            if (LOG.isDebugEnabled())
                LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

            final Context modifiedContext = addCanOutcomeToContext(context, responseData);
            if (LOG.isDebugEnabled())
                LOG.debug("CONTEXT FOR SAVE (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(modifiedContext) + "'.");

            operationService.saveOperationStep(execution, entity, modifiedContext, commandMessage, step);
        }
    }

    private Context addCanOutcomeToContext(final Context context, final JsonNode responseData) {
        final String canId = responseData.get("can").get("id").asText();
        final String token = responseData.get("token").asText();
        final Outcome outcome = new Outcome(canId, token, "cans");

        final Set<Outcome> outcomes = new HashSet<>();
        outcomes.add(outcome);
        context.setOutcomes(outcomes);
        return context;
    }
}
