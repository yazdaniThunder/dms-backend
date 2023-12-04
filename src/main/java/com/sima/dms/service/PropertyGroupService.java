package com.sima.dms.service;

import java.util.Map;

public interface PropertyGroupService {

    void removeGroups(String var1, String var2);


    Map<String, String> getPropertiesSimple(String v1, String v2);
}
