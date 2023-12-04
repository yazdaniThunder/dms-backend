package com.sima.dms.service.impl;

import com.sima.dms.domain.dto.request.PersonnelRequestDto;
import com.sima.dms.domain.dto.response.PersonnelResponseDto;
import com.sima.dms.repository.BranchRepository;
import com.sima.dms.repository.DocumentRepository;
import com.sima.dms.repository.NodeBaseRepository;
import com.sima.dms.service.PersonnelService;
import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;
import com.sima.dms.constants.OpenKM;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;

@Service
@Component
//@Transactional
@AllArgsConstructor
public class PersonnelServiceImpl implements PersonnelService {

    private final BranchRepository branchRepository;
    private final NodeBaseRepository nodeBaseRepository;
    private final DocumentRepository documentRepository;

    private final Logger log = LoggerFactory.getLogger(PersonnelServiceImpl.class);
    private final OKMWebservices webservices = OKMWebservicesFactory.newInstance(OpenKM.host, OpenKM.username, OpenKM.password);

    @Override
    public PersonnelResponseDto getPersonnelInfo(PersonnelRequestDto request) {

        HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);

        RestTemplate restTemplate = new RestTemplate();
        PersonnelResponseDto response = new PersonnelResponseDto();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accept", "application/json");
        httpHeaders.add("Content-Type", "application/json");

        HttpEntity<PersonnelRequestDto> sendRequest = new HttpEntity<>(request, httpHeaders);

        try {
            log.info("message status : " + response);
            ResponseEntity<PersonnelResponseDto> exchange = restTemplate.exchange("https://10.0.2.14:8003/api/PersonelAllNewV/GetOverallPersonelInfo", HttpMethod.POST, sendRequest, PersonnelResponseDto.class);
            return exchange.getBody();

        } catch (Exception e) {
            log.error("error", e);
            return null;
        }
    }


}