package com.procurement.orchestrator.config.kafka;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Rules;
import com.procurement.orchestrator.domain.chronograph.ChronographResponse;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

public class MessageConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumer.class);

    private final ProcessService processService;
    private final RequestService requestService;
    private final OperationService operationService;
    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;

    public MessageConsumer(final ProcessService processService,
                           final RequestService requestService,
                           final OperationService operationService,
                           final JsonUtil jsonUtil,
                           final DateUtil dateUtil) {
        this.processService = processService;
        this.requestService = requestService;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @KafkaListener(topics = "chronograph-out")
    public void onReceiving(final String message,
                            @Header(KafkaHeaders.ACKNOWLEDGMENT) final Acknowledgment acknowledgment) {

        acknowledgment.acknowledge();
        try {
            LOG.info("Get task: " + message);
            final ChronographResponse response = jsonUtil.toObject(ChronographResponse.class, message);
            final ChronographResponse.ChronographResponseData data = response.getData();
            final Context contextChronograph = jsonUtil.toObject(Context.class, data.getMetaData());

            final Context prevContext = operationService.getContext(contextChronograph.getCpid());

            final Rules rules = operationService.checkAndGetRules(prevContext, contextChronograph.getProcessType());

            final Context context = new Context();
            context.setCountry(rules.getCountry());
            context.setPmd(rules.getPmd());
            context.setProcessType(rules.getProcessType());
            context.setStage(rules.getNewStage());
            context.setPhase(rules.getNewPhase());
            context.setOperationType(rules.getOperationType());

            context.setRequestId(contextChronograph.getRequestId());
            context.setOperationId(contextChronograph.getOperationId());
            context.setOwner(prevContext.getOwner());
            context.setCpid(prevContext.getCpid());
            context.setOcid(prevContext.getOcid());
            context.setToken(prevContext.getToken());
            context.setLanguage(prevContext.getLanguage());
            context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));

            requestService.saveRequest(
                    context.getRequestId(),
                    context.getOperationId(),
                    context,
                    jsonUtil.toJsonNode(data));
            final Map<String, Object> variables = new HashMap<>();
            variables.put("operationType", context.getOperationType());
            processService.startProcess(contextChronograph, variables);
        } catch (Exception e) {
        }
    }
}
