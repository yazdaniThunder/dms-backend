package com.sima.dms.controller;


import com.sima.dms.service.SearchService;
import com.openkm.sdk4j.bean.QueryResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "Search")
@RequestMapping("/dms/search")
public class SearchController {

    private final SearchService  searchService;

    @CrossOrigin
    @GetMapping("/find")
    @Operation(summary = "search")
    @SecurityRequirement(name = "token")
    public ResponseEntity<List<QueryResult>> find(@RequestParam(required = false) String content,@RequestParam(required = false) String path) {
        List<QueryResult> bookmark = searchService.find(content,path);
        return ResponseEntity.ok().body(bookmark);
    }

}
