package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.dao.CassandraDao;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Rules;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.domain.entity.*;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import java.util.Optional;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OperationServiceImpl implements OperationService {

    private static final Logger LOG = LoggerFactory.getLogger(OperationServiceImpl.class);

    private static final String LAST_TASK = "lastExecutedTask";

    private final CassandraDao cassandraDao;

    private final RuntimeService runtimeService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public OperationServiceImpl(final CassandraDao cassandraDao,
                                final RuntimeService runtimeService,
                                final JsonUtil jsonUtil,
                                final DateUtil dateUtil) {
        this.cassandraDao = cassandraDao;
        this.runtimeService = runtimeService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public Boolean isRulesExist(final Rules rules) {
        return cassandraDao.isRulesExist(rules);
    }

    @Override
    public void checkOperationById(final String operationId) {
        if (cassandraDao.isOperationExist(operationId)) {
            throw new OperationException("Operation with current id already exist.");
        }
    }

    @Override
    public Boolean saveIfNotExist(final String operationId,
                                  final String processId) {
        final OperationEntity operationEntity = new OperationEntity();
        operationEntity.setOperationId(operationId);
        operationEntity.setProcessId(processId);
        return cassandraDao.saveOperationIfNotExist(operationEntity);
    }

    @Override
    public void saveFirstOperationStep(final DelegateExecution execution, final RequestEntity requestEntity) {
        execution.setVariable(LAST_TASK, execution.getCurrentActivityId());
        final OperationStepEntity operationStepEntity = new OperationStepEntity();
        operationStepEntity.setProcessId(execution.getProcessInstanceId());
        operationStepEntity.setTaskId(execution.getCurrentActivityId());
        operationStepEntity.setOperationId(requestEntity.getOperationId());
        operationStepEntity.setDate(dateUtil.dateNowUTC());
        operationStepEntity.setJsonParams(requestEntity.getJsonParams());
        operationStepEntity.setRequestData(requestEntity.getJsonData());
        operationStepEntity.setResponseData(requestEntity.getJsonData());
        cassandraDao.saveOperationStep(operationStepEntity);
    }

    @Override
    public OperationStepEntity getOperationStep(final String processId, final String taskId) {
        final Optional<OperationStepEntity> entityOptional = cassandraDao.getOperationStep(processId, taskId);
        if (entityOptional.isPresent()) {
            return entityOptional.get();
        } else {
            processException("No data found for taskId :" + taskId, processId);
            return null;
        }
    }

    @Override
    public OperationStepEntity getPreviousOperationStep(final DelegateExecution execution) {
        final String processId = execution.getProcessInstanceId();
        final String taskId = (String) execution.getVariable(LAST_TASK);
        return getOperationStep(processId, taskId);
    }

    @Override
    public void saveOperationStep(final DelegateExecution execution, final OperationStepEntity entity) {
        execution.setVariable(LAST_TASK, execution.getCurrentActivityId());
        entity.setTaskId(execution.getCurrentActivityId());
        entity.setDate(dateUtil.dateNowUTC());
        cassandraDao.saveOperationStep(entity);
    }

    @Override
    public void saveOperationStep(final DelegateExecution execution, final OperationStepEntity entity, final JsonNode request) {
        execution.setVariable(LAST_TASK, execution.getCurrentActivityId());
        entity.setTaskId(execution.getCurrentActivityId());
        entity.setRequestData(jsonUtil.toJson(request));
        entity.setDate(dateUtil.dateNowUTC());
        cassandraDao.saveOperationStep(entity);
    }

    @Override
    public void saveOperationStep(final DelegateExecution execution, final OperationStepEntity entity, final Context params, final JsonNode request) {
        execution.setVariable(LAST_TASK, execution.getCurrentActivityId());
        entity.setTaskId(execution.getCurrentActivityId());
        entity.setJsonParams(jsonUtil.toJson(params));
        entity.setRequestData(jsonUtil.toJson(request));
        entity.setDate(dateUtil.dateNowUTC());
        if (Objects.nonNull(params.getCpid()))
            entity.setCpId(params.getCpid());
        cassandraDao.saveOperationStep(entity);
    }

    @Override
    public void saveOperationStep(final DelegateExecution execution, final OperationStepEntity entity, final JsonNode request, final JsonNode response) {
        execution.setVariable(LAST_TASK, execution.getCurrentActivityId());
        entity.setTaskId(execution.getCurrentActivityId());
        entity.setRequestData(jsonUtil.toJson(request));
        entity.setResponseData(jsonUtil.toJson(response));
        entity.setDate(dateUtil.dateNowUTC());
        cassandraDao.saveOperationStep(entity);
    }

    @Override
    public void saveOperationStep(final DelegateExecution execution, final OperationStepEntity entity, final Context params, final JsonNode request, final JsonNode response) {
        execution.setVariable(LAST_TASK, execution.getCurrentActivityId());
        entity.setTaskId(execution.getCurrentActivityId());
        entity.setJsonParams(jsonUtil.toJson(params));
        entity.setRequestData(jsonUtil.toJson(request));
        entity.setResponseData(jsonUtil.toJson(response));
        entity.setDate(dateUtil.dateNowUTC());
        if (Objects.nonNull(params.getCpid()))
            entity.setCpId(params.getCpid());
        cassandraDao.saveOperationStep(entity);
    }

    @Override
    public void saveOperationException(final String processId, final String taskId, final Context params, final JsonNode request, final JsonNode response) {
        final OperationStepEntity operationStepEntity = new OperationStepEntity();
        operationStepEntity.setProcessId(processId);
        operationStepEntity.setTaskId(taskId);
        operationStepEntity.setOperationId(params.getOperationId());
        operationStepEntity.setCpId(params.getCpid());
        operationStepEntity.setDate(dateUtil.dateNowUTC());
        operationStepEntity.setRequestData(jsonUtil.toJson(request));
        operationStepEntity.setResponseData(jsonUtil.toJson(response));
        cassandraDao.saveOperationStep(operationStepEntity);
    }

    @Override
    public void saveStageParams(final Context params) {
        final StageEntity stageEntity = new StageEntity();
        stageEntity.setCpId(params.getCpid());
        stageEntity.setStage(params.getNewStage());
        stageEntity.setCountry(params.getCountry());
        stageEntity.setPmd(params.getPmd());
        stageEntity.setPhase(params.getPhase());
        cassandraDao.saveStage(stageEntity);
    }

    @Override
    public StageEntity getStageParams(final String cpId, final String processId) {
        final Optional<StageEntity> entityOptional = cassandraDao.getStageByCpId(cpId);
        if (entityOptional.isPresent()) {
            return entityOptional.get();
        } else {
            processException("No stage params found!", processId);
            return null;
        }
    }

    @Override
    public Stage getStageFromRules(final String country, final String pmd, final String operationType) {
        final Optional<StageRulesEntity> entityOptional = cassandraDao.getStageFromRules(country, pmd, operationType);
        if (entityOptional.isPresent()) {
            return Stage.fromValue(entityOptional.get().getStage());
        } else {
            throw new OperationException("No stage found!");
        }
    }


    public void processException(final String error,
                                 final String processId) {
        try {
            LOG.info("Exception in process Id: " + processId + "; message: " + error);
            runtimeService.suspendProcessInstanceById(processId);
        } catch (Exception ignored) {
        }
    }
}

