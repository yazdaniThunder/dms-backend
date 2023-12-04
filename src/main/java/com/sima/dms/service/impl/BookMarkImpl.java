package com.sima.dms.service.impl;

import com.sima.dms.errors.exceptions.GenericException;
import com.sima.dms.service.BookMarkService;
import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;
import com.openkm.sdk4j.bean.Bookmark;
import com.sima.dms.constants.OpenKM;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BookMarkImpl implements BookMarkService {

    private final OKMWebservices webservices = OKMWebservicesFactory.newInstance(OpenKM.host, OpenKM.username, OpenKM.password);

    @Override
    public Bookmark create(String nodeId,String name)  {
        try {
            return webservices.createBookmark(nodeId,name);
        }catch (Exception e){
            throw new GenericException(e);
        }
    }

    @Override
    public List<Bookmark> getUserBookmarks() {

        try {
            return  webservices.getUserBookmarks();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Bookmark getById(Integer id) {
        try {
            return webservices.getBookmark(id);
        } catch (Exception e){
            throw new GenericException(e);
        }
    }

    @Override
    public Bookmark rename(Integer id , String name) {
        try {
            return webservices.renameBookmark(id, name);
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            webservices.deleteBookmark(id);
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }
}
