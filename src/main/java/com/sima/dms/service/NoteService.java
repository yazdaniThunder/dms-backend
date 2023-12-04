package com.sima.dms.service;

import com.openkm.sdk4j.bean.Note;

import java.util.List;

public interface NoteService {

    Note addNote(String var1,String text);

    void setNote(String var1,String text);

    List<Note> noteList(String var1);

    void deleteNote(String var1);
}
