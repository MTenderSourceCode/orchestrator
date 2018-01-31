package com.procurement.orchestrator.delegate.tender.qualification;

import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QualificationCheckAwarded implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(QualificationCheckAwarded.class);

    private final AccessRestClient accessRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public QualificationCheckAwarded(final AccessRestClient accessRestClient,
                                     final OperationService operationService,
                                     final ProcessService processService,
                                     final JsonUtil jsonUtil) {
        this.accessRestClient = accessRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
//        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
//        if (entityOptional.isPresent()) {
//            final OperationStepEntity entity = entityOptional.get();
//            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
//            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
//            final String processId = execution.getProcessInstanceId();
//            final String operationId = params.getOperationId();
//            try {
//                final JsonNode responseData = processService.processResponse(
//                        accessRestClient.updateCn(params.getOwner(), params.getCpid(), params.getToken(), jsonData),
//                        processId,
//                        operationId);
//                operationService.saveOperationStep(execution, entity, params, responseData);
//            } catch (Exception e) {
//                LOG.error(e.getMessage(), e);
//                processService.processException(e.getMessage(), processId);
//            }
//        }
    }
}
