package com.procurement.orchestrator.delegate.chronograph;

import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.cassandra.model.Params;
import com.procurement.orchestrator.kafka.MessageProducer;
import com.procurement.orchestrator.kafka.dto.ChronographTask;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChronographRescheduleEndTenderPeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ChronographRescheduleEndTenderPeriod.class);

    private final MessageProducer messageProducer;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public ChronographRescheduleEndTenderPeriod(final MessageProducer messageProducer,
                                                final OperationService operationService,
                                                final ProcessService processService,
                                                final JsonUtil jsonUtil,
                                                final DateUtil dateUtil) {
        this.messageProducer = messageProducer;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
//        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
//        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
//        final String processId = execution.getProcessInstanceId();
//        final String operationId = params.getOperationId();
//        ChronographTask.TaskMetaData taskMetaData = new ChronographTask.TaskMetaData(
//                params.getProcessType(),
//                operationId);
//        ChronographTask task = new ChronographTask(
//                ChronographTask.ActionType.REPLACE,
//                params.getCpid(),
//                "tender",
//                dateUtil.stringToLocal(params.getEndDate()),
//                jsonUtil.toJson(taskMetaData));
//        messageProducer.sendToChronograph(task);
    }
}