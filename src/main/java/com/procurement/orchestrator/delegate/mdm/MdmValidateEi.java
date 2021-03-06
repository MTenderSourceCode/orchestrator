package com.procurement.orchestrator.delegate.mdm;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.MdmRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.MdmCommandType.PROCESS_EI_DATA;

@Component
public class MdmValidateEi implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(MdmValidateEi.class);

    private final MdmRestClient mdmRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public MdmValidateEi(final MdmRestClient mdmRestClient,
                         final OperationService operationService,
                         final ProcessService processService,
                         final JsonUtil jsonUtil) {
        this.mdmRestClient = mdmRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final JsonNode prevData = jsonUtil.toJsonNode(entity.getResponseData());
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode rqData = processService.getEiData(prevData, processId);
        final JsonNode commandMessage = processService.getCommandMessage(PROCESS_EI_DATA, context, rqData);
        if (rqData != null) {
            JsonNode responseData = processService.processResponse(
                    mdmRestClient.execute(commandMessage),
                    context,
                    processId,
                    taskId,
                    commandMessage);
            if (Objects.nonNull(responseData)) {
                operationService.saveOperationStep(
                        execution,
                        entity,
                        commandMessage,
                        processService.setEiData(prevData, responseData, processId));
            }
        }
    }
}
