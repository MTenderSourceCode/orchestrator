package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-clarification")
public interface ClarificationRestClient {

    @RequestMapping(path = "/enquiry", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createEnquiry(@RequestParam("cpid") String cpId,
                                              @RequestParam("stage") String stage,
                                              @RequestParam("date") String date,
                                              @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/enquiry", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> createAnswer(@RequestParam("cpid") String cpId,
                                             @RequestParam("stage") String stage,
                                             @RequestParam("token") String token,
                                             @RequestParam("enquiryId") String enquiryId,
                                             @RequestParam("owner") String owner,
                                             @RequestParam("date") String date,
                                             @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/enquiry", method = RequestMethod.GET)
    ResponseEntity<ResponseDto> checkEnquiries(@RequestParam("cpid") String cpId,
                                               @RequestParam("stage") String stage,
                                               @RequestParam("date") String date) throws Exception;


    @RequestMapping(path = "/period/save", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> savePeriod(@RequestParam("cpid") String cpId,
                                           @RequestParam("stage") String stage,
                                           @RequestParam("owner") String owner,
                                           @RequestParam("country") String country,
                                           @RequestParam("pmd") String pmd,
                                           @RequestParam("startDate") String startDate,
                                           @RequestParam("endDate") String endDate,
                                           @RequestParam("setExtendedPeriod") Boolean setExtendedPeriod) throws Exception;

    @RequestMapping(path = "/period", method = RequestMethod.GET)
    ResponseEntity<ResponseDto> getPeriod(@RequestParam("cpid") String cpId,
                                          @RequestParam("stage") String stage) throws Exception;
}
