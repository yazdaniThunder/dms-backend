package com.sima.dms.service;

import com.openkm.sdk4j.bean.QueryParams;
import com.openkm.sdk4j.bean.QueryResult;

import java.util.List;

public interface SearchService {

    List<QueryResult> find(String content ,String path);

    List<QueryResult> findByContent(String var1);
}
