package com.sima.dms.controller;


import com.sima.dms.service.BookMarkService;
import com.openkm.sdk4j.bean.Bookmark;
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
@Tag(name = "BookMark")
@RequestMapping("/dms/bookmark")
public class BookMarkController {

    private final BookMarkService bookMarkService;
    private final Logger log = LoggerFactory.getLogger(BookMarkController.class);

    @CrossOrigin
    @PostMapping("/create")
    @Operation(summary = "Create bookmark")
    @SecurityRequirement(name = "token")
    public ResponseEntity<Bookmark>  create(@RequestParam  String nodeId , @RequestBody String name) {
        log.debug("REST request to create bookmark");
        Bookmark bookmark = bookMarkService.create(nodeId, name);
        return ResponseEntity.ok().body(bookmark);
    }

    @CrossOrigin
    @GetMapping("/get")
    @Operation(summary = "get bookMark by id")
    @SecurityRequirement(name = "token")
    public ResponseEntity<Bookmark> getBookMarkById(@RequestParam Integer bookmarkId){
        log.debug("REST request to get bookmark by id");
        Bookmark bookmark=bookMarkService.getById(bookmarkId);
        return ResponseEntity.ok().body(bookmark);
    }

    @CrossOrigin
    @GetMapping("/getAll")
    @Operation(summary = "get all bookMark by id")
    @SecurityRequirement(name = "token")
    public ResponseEntity<List<Bookmark>> getAll(){
        log.debug("REST request to get all bookmark");
        List<Bookmark> bookmark=bookMarkService.getUserBookmarks();
        return ResponseEntity.ok().body(bookmark);
    }

    @CrossOrigin
    @PutMapping("/rename")
    @Operation(summary = "rename bookMark")
    @SecurityRequirement(name = "token")
    public ResponseEntity <Bookmark>  rename(@RequestParam Integer bookmarkId, @RequestParam(required = false) String name) {
        log.debug("REST request to rename bookmark");
        Bookmark bookmark=bookMarkService.rename(bookmarkId,name);
        return ResponseEntity.ok().body(bookmark);
    }

    @CrossOrigin
    @DeleteMapping("delete")
    @Operation(summary = "rename bookMark")
    @SecurityRequirement(name = "token")
    public void delete(@RequestParam Integer bookmarkId) {
        log.debug("REST request to delete bookmark");
        bookMarkService.delete(bookmarkId);
    }

}
