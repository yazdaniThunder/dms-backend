package com.sima.dms.service;

import com.openkm.sdk4j.bean.Bookmark;

import java.util.List;

public interface BookMarkService {

    Bookmark create(String nodeId,String name);

    List<Bookmark> getUserBookmarks();

    Bookmark getById(Integer id);

    Bookmark rename(Integer id , String name);

    void delete(Integer id);

}
