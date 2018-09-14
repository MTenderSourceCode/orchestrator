package com.procurement.orchestrator.delegate.qualification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.QualificationRestClient;
import com.procurement.orchestrator.service.NotificationService;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class QualificationCreateAwards implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(QualificationCreateAwards.class);

    private final QualificationRestClient qualificationRestClient;
    private final NotificationService notificationService;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public QualificationCreateAwards(final QualificationRestClient qualificationRestClient,
                                     final NotificationService notificationService,
                                     final OperationService operationService,
                                     final ProcessService processService,
                                     final JsonUtil jsonUtil) {
        this.qualificationRestClient = qualificationRestClient;
        this.notificationService = notificationService;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final String processId = execution.getProcessInstanceId();
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode requestData = jsonUtil.toJsonNode(entity.getResponseData());
        final String taskId = execution.getCurrentActivityId();
        final JsonNode responseData = processService.processResponse(
                qualificationRestClient.createAwards(
                        context.getCpid(),
                        context.getStage(),
                        context.getOwner(),
                        context.getCountry(),
                        context.getPmd(),
                        context.getStartDate(),
                        requestData),
                context,
                processId,
                taskId,
                requestData);
        if (Objects.nonNull(responseData)) {
            operationService.saveOperationStep(
                    execution,
                    entity,
                    notificationService.addAwardOutcomeToContext(context, responseData, processId),
                    requestData,
                    processService.addAwardData(requestData, responseData, processId));
        }
    }
}


