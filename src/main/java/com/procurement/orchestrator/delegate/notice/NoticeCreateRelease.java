package com.procurement.orchestrator.delegate.notice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Outcome;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.NoticeRestClient;
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

import static com.procurement.orchestrator.domain.commands.NoticeCommandType.CREATE_RELEASE;

@Component
public class NoticeCreateRelease implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(NoticeCreateRelease.class);

    private final NoticeRestClient noticeRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public NoticeCreateRelease(
        final NoticeRestClient noticeRestClient,
        final OperationService operationService,
        final ProcessService processService,
        final JsonUtil jsonUtil
    ) {
        this.noticeRestClient = noticeRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode requestData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();

        if (requestData != null) {
            final JsonNode commandMessage = processService.getCommandMessage(CREATE_RELEASE, context, requestData);
            if (LOG.isDebugEnabled())
                LOG.debug("COMMAND (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(commandMessage) + "'.");

            final ResponseEntity<ResponseDto> response = noticeRestClient.execute(commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE FROM SERVICE (" + context.getOperationId() + "): '" + jsonUtil.toJson(response.getBody()) + "'.");

            final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE AFTER PROCESSING (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(responseData) + "'.");

            if (responseData != null) {
                final Context modifiedContext = addDataToContext(context, responseData, processId);
                if (LOG.isDebugEnabled())
                    LOG.debug("CONTEXT FOR SAVE (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(modifiedContext) + "'.");

                if (LOG.isDebugEnabled())
                    LOG.debug("STEP FOR SAVE (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(responseData) + "'.");
                operationService.saveOperationStep(execution, entity, modifiedContext, commandMessage, responseData);
            }
        } else {
            if (LOG.isDebugEnabled())
                LOG.debug("Request data is missing. The Notice service for creating release was not called.");
        }
    }

    private Context addDataToContext(final Context context, final JsonNode responseData, final String processId) {
        context.setOcid(processService.getText("ocid", responseData, processId));
        final Context contextWithAmendmentsIds = addAmendmentsIds(context, responseData);
        return addAwardsIds(contextWithAmendmentsIds, responseData);
    }

    private Context addAmendmentsIds(final Context context, final JsonNode responseData) {
        final ArrayNode amendments = (ArrayNode) responseData.get("amendmentsIds");
        if (amendments != null) {
            Set<Outcome> outcomes;
            if (context.getOutcomes() != null) {
                outcomes = context.getOutcomes();
            } else {
                outcomes = new HashSet<>();
            }
            for (final JsonNode amendment : amendments) {
                outcomes.add(new Outcome(amendment.asText(), null, "amendments"));
            }
            context.setOutcomes(outcomes);
        }
        return context;
    }

    private Context addAwardsIds(final Context context, final JsonNode responseData) {
        final ArrayNode awards = (ArrayNode) responseData.get("awardsIds");
        if (awards != null) {
            Set<Outcome> outcomes;
            if (context.getOutcomes() != null) {
                outcomes = context.getOutcomes();
            } else {
                outcomes = new HashSet<>();
            }
            for (final JsonNode award : awards) {
                outcomes.add(new Outcome(award.asText(), null, "awards"));
            }
            context.setOutcomes(outcomes);
        }
        return context;
    }
}
