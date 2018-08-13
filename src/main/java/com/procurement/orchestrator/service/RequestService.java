package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Rule;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import org.springframework.stereotype.Service;

@Service
public interface RequestService {

    void saveRequest(String requestId, String operationId, Context context, JsonNode jsonData);

    RequestEntity getRequestById(String requestId, String processId);

    String getOwner(String authorization);

    void saveRequestAndCheckOperation(Context context, JsonNode jsonData);

    Context getContext(String cpId);

    Rule checkAndGetRule(Context prevContext, String processType);

    Rule getRule(String country, String pmd, String processType);

    Context getContextForCreate(String authorization,
                                String operationId,
                                String country,
                                String pmd,
                                String processType);

    Context getContextForUpdate(String authorization,
                                String operationId,
                                String cpid,
                                String ocid,
                                String token,
                                String processType);

}
