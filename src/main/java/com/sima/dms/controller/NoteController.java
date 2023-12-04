package com.sima.dms.controller;


import com.sima.dms.service.NoteService;
import com.openkm.sdk4j.bean.Note;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "Note")
@RequestMapping("/dms/note")
public class NoteController {

    private final NoteService noteService;
    private final Logger log = LoggerFactory.getLogger(NoteController.class);

    @CrossOrigin
    @PostMapping("/add")
    @Operation(summary = "add Note")
    @SecurityRequirement(name = "token")
    public ResponseEntity<Note> addNote(@RequestParam String nodeId, @RequestBody String text) {
        log.debug("RESt request to add note", nodeId);
        Note note = noteService.addNote(nodeId, text);
        return ResponseEntity.ok().body(note);
    }


    @CrossOrigin
    @PutMapping("/set")
    @Operation(summary = "Set Note")
    @SecurityRequirement(name = "token")
    public void setNote(@RequestParam(required = false) String nodeId, @RequestBody String text) {
        log.debug("RESt request to set note", nodeId);
        noteService.setNote(nodeId, text);
    }

    @CrossOrigin
    @GetMapping("/list")
    @Operation(summary = "List of  Notes")
    @SecurityRequirement(name = "token")
    public ResponseEntity<List<Note>> listNote(@RequestParam String nodeId) {
        log.debug("RESt request to get list note", nodeId);
        List<Note> notes = noteService.noteList(nodeId);
        return ResponseEntity.ok().body(notes);
    }

    @CrossOrigin
    @DeleteMapping("/delete")
    @Operation(summary = "delete Note")
    @SecurityRequirement(name = "token")
    public void deleteNote(@RequestParam String noteId) {
        log.debug("RESt request delete note", noteId);
        noteService.deleteNote(noteId);
    }

}
