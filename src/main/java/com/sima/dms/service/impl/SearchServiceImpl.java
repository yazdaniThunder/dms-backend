package com.sima.dms.service.impl;


import com.sima.dms.service.SearchService;
import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;
import com.openkm.sdk4j.bean.QueryParams;
import com.openkm.sdk4j.bean.QueryResult;
import com.sima.dms.constants.OpenKM;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final OKMWebservices webservices = OKMWebservicesFactory.newInstance(OpenKM.host, OpenKM.username, OpenKM.password);


    @Override
    public  List<QueryResult> find(String content ,String path) {
        try {
            QueryParams queryParams = new QueryParams();
            queryParams.setContent(content);
            queryParams.setPath(path);
            return webservices.find(queryParams);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<QueryResult> findByContent(String var1) {
        try {
            return webservices.findByContent(var1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
