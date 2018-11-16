package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ProcessService {

    void startProcess(Context context, Map<String, Object> variables);

    void terminateProcess(String processId, String message);

    JsonNode getCommandMessage(Enum command, Context context, JsonNode data);

    JsonNode processResponse(ResponseEntity<ResponseDto> responseEntity,
                             Context context,
                             String processId,
                             String taskId,
                             JsonNode request);

    String getText(String fieldName, JsonNode jsonData, String processId);

    Boolean getBoolean(String fieldName, JsonNode jsonData, String processId);

    void setEnquiryPeriodStartDate(JsonNode jsonData, String startDate, String processId);

    void setTenderPeriodStartDate(JsonNode jsonData, String startDate, String processId);

    JsonNode setCheckEnquiryPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode getCheckTenderPeriod(JsonNode jsonData, String processId);

    JsonNode setCheckTenderPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    String getTenderPeriodEndDate(JsonNode jsonData, String processId);

    String getEnquiryPeriodEndDate(JsonNode jsonData, String processId);

    JsonNode getTenderPeriod(JsonNode jsonData, String processId);

    JsonNode getEnquiryPeriod(JsonNode jsonData, String processId);

    JsonNode addTenderTenderPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addTenderEnquiryPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addEnquiryWithAnswer(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addTenderStatus(JsonNode jsonData, JsonNode statusData, String processId);

    JsonNode addTenderUnsuspendData(JsonNode jsonData, JsonNode statusData, String processId);

    JsonNode getLots(JsonNode jsonData, String processId);

    JsonNode addLots(JsonNode jsonData, JsonNode lotsData, String processId);

    JsonNode addLotsUnsuccessful(JsonNode jsonData, JsonNode lotsData, String processId);

    JsonNode addLotsAndAwardCriteria(JsonNode jsonData, JsonNode lotsData, String processId);

    JsonNode addItems(JsonNode data, String processId);

    JsonNode addAwardData(JsonNode jsonData, JsonNode awardData, String processId);

    JsonNode addAwards(JsonNode jsonData, JsonNode awardsData, String processId);

    JsonNode addCans(JsonNode jsonData, JsonNode cansData, String processId);

    JsonNode addContracts(JsonNode jsonData, JsonNode data, String processId);

    JsonNode getUnsuccessfulLots(JsonNode jsonData, String processId);

    JsonNode getTenderLots(JsonNode jsonData, String processId);

    JsonNode addUpdateBidsStatusData(JsonNode jsonData, JsonNode bidsData, String processId);

    JsonNode addBidsAndTenderPeriod(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode addBids(JsonNode jsonData, JsonNode responseData, String processId);

    Boolean isBidsEmpty(JsonNode responseData, String processId);

    JsonNode addUpdatedBid(JsonNode jsonData, JsonNode bidData, String processId);

    JsonNode addUpdatedLot(JsonNode jsonData, JsonNode lotData, String processId);

    JsonNode getDocumentsOfTender(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfTender(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode getDocumentsOfAward(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfAward(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode getDocumentsOfAwards(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfAwards(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode getDocumentsOfBids(JsonNode jsonData, String processId);

    JsonNode getDocumentsOfBid(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfBids(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode setDocumentsOfBid(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode getDocumentsOfContractUpdate(JsonNode jsonData, String processId);

    JsonNode getDocumentsOfContractAwards(JsonNode jsonData, String processId);

    JsonNode getDocumentsOfContractPersones(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfContractAwards(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode setDocumentsOfContractPersones(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode addStandstillPeriod(JsonNode jsonData, String startDate, String endDate, String processId);

    JsonNode setAccessData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getAccessData(JsonNode jsonData, String processId);

    JsonNode setTender(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getEiData(JsonNode jsonData, String processId);

    JsonNode setEiData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getFsData(JsonNode jsonData, String processId);

    JsonNode setFsData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getTenderData(Boolean itemsAdd, JsonNode jsonData, String processId);

    JsonNode setTenderData(Boolean itemsAdd, JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getContractData(JsonNode jsonData, String processId);

    JsonNode setContractData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getPlanning(JsonNode jsonData, String processId);

    JsonNode getCheckItems(JsonNode jsonData, String processId);

    JsonNode setCheckItems(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getCheckFs(JsonNode jsonData, String startDate, String processId);

    JsonNode setCheckFs(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getBidTenderersData(JsonNode jsonData, String processId);

    JsonNode setBidTenderersData(JsonNode jsonData, JsonNode responseData, String processId);

    String getEnquiryId(JsonNode jsonData, String processId);

    JsonNode getEnquiryAuthor(JsonNode jsonData, String processId);

    JsonNode setEnquiryAuthor(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getEnquiryRelatedLot(JsonNode jsonData, String processId);

    JsonNode getAuctionData(JsonNode prevData, String processId);

    JsonNode setAuctionData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getAuctionStartData(JsonNode prevData, String processId);

    JsonNode setAuctionStartData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getAuctionLaunchData(JsonNode jsonData, String processId);

    JsonNode setAuctionEndData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode addContractTerms(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode addActualBudgetSource(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getAwardsValue(JsonNode jsonData, String processId);

    JsonNode getDataForGetTerms(JsonNode jsonData, String processId);

    JsonNode getAgreedMetrics(JsonNode jsonData, String processId);

    JsonNode setAgreedMetrics(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode setContractUpdateData(JsonNode jsonData, JsonNode responseData, String processId);
}

