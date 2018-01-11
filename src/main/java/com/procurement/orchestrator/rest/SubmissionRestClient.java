package com.procurement.orchestrator.rest;

import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.domain.dto.cn.RequestSubmissionPeriodDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@FeignClient(name = "e-submission")
public interface SubmissionRestClient {
    @RequestMapping(path = "/period/check", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postCheckPeriod(@RequestParam("country") String country,
                                                @RequestParam("pmd") String pmd,
                                                @RequestParam("stage") String stage,
                                                @RequestParam("startDate") String startDate,
                                                @RequestParam("endDate") String endDate) throws Exception;

    @RequestMapping(path = "/period/save", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postSavePeriod(@RequestParam("cpid") String cpid,
                                               @RequestParam("stage") String stage,
                                               @RequestParam("startDate") String startDate,
                                               @RequestParam("endDate") String endDate) throws Exception;
}