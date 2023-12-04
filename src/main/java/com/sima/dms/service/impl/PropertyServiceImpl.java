package com.sima.dms.service.impl;


import com.sima.dms.service.PropertyService;
import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;
import com.sima.dms.constants.OpenKM;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final OKMWebservices webservices = OKMWebservicesFactory.newInstance(OpenKM.host, OpenKM.username, OpenKM.password);

    @Override
    public void addCategory(String var1, String var2){
        try {
            webservices.addCategory(var1, var2);
        }catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void deleteCategory(String var1, String var2){
        try {
            webservices.removeCategory(var1, var2);
        }catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

}
