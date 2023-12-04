package com.sima.dms.service.impl;


import com.sima.dms.errors.exceptions.GenericException;
import com.sima.dms.service.NoteService;
import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;
import com.openkm.sdk4j.bean.Note;
import com.sima.dms.constants.OpenKM;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final OKMWebservices webservices = OKMWebservicesFactory.newInstance(OpenKM.host, OpenKM.username, OpenKM.password);
    @Override
    public Note addNote(String var1,String text) {
        try {
            return webservices.addNote(var1,text);
        }catch (Exception e){
            throw new GenericException(e);

        }
    }

    @Override
    public void setNote(String var1,String text) {
        try {
             webservices.setNote(var1,text);
        }catch (Exception e){
            throw new GenericException(e);
        }
    }

    @Override
    public List<Note> noteList(String var1) {
        try {
            return webservices.listNotes(var1);
        }catch (Exception e){
            throw new GenericException(e);
        }
    }

    @Override
    public void deleteNote(String var1) {
        try {
            webservices.deleteNote(var1);
        }catch (Exception e){
            throw new GenericException(e);
        }
    }



}
