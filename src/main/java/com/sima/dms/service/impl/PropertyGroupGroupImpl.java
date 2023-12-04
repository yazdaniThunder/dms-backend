package com.sima.dms.service.impl;


import com.sima.dms.service.PropertyGroupService;
import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;
import com.sima.dms.constants.OpenKM;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class PropertyGroupGroupImpl implements PropertyGroupService {
    private final OKMWebservices webservices = OKMWebservicesFactory.newInstance(OpenKM.host, OpenKM.username, OpenKM.password);

    @Override
    public void removeGroups(String var1, String var2){
        try {
            webservices.removeGroup(var1, var2);
        }catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Map<String, String> getPropertiesSimple(String v1, String v2) {
        try {
            return webservices.getPropertyGroupPropertiesSimple(v1, v2);
        }catch (Exception exception){
            throw new RuntimeException(exception);
        }
    }
}
