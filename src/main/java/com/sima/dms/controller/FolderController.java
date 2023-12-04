package com.sima.dms.controller;


import com.sima.dms.service.FolderService;
import com.openkm.sdk4j.bean.Folder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "Folder")
@RequestMapping("/dms/folder")
public class FolderController {


    private final FolderService folderService;
    private final Logger log = LoggerFactory.getLogger(FolderController.class);

//    @CrossOrigin
//    @GetMapping("/folders")
//    @SecurityRequirement(name = "token")
//    @PreAuthorize("hasAnyAuthority('ADMIN')")
//    @Operation(summary = "get folder children")
//    public ResponseEntity<List<Folder>> getFolderList(@RequestParam String fldId){
//        List<Folder> folders=folderService.getFolderChildren(fldId);
//        return ResponseEntity.ok().body(folders);
//    }


    @CrossOrigin
    @PutMapping("/copy")
    @Operation(summary = "copy folder")
    @SecurityRequirement(name = "token")
    public void copyFolder(@RequestParam String fldId ,@RequestParam String dstId) {
        log.debug("REST request to copy folder");
        folderService.folderCopy(fldId,dstId);
    }


    @CrossOrigin
    @PutMapping("/move")
    @Operation(summary = "move folder")
    @SecurityRequirement(name = "token")
    public void moveFolder(@RequestParam String fldId , @RequestParam String dstId) {
        log.debug("REST request to move folder");
        folderService.folderMove(fldId,dstId);
    }

    @CrossOrigin
    @PutMapping("/rename")
    @Operation(summary = "rename folder")
    @SecurityRequirement(name = "token")
    public void rename(@RequestParam String fldId , @RequestParam String newName) {
        log.debug("REST request to rename folder");
        folderService.rename(fldId,newName);
    }

    @CrossOrigin
    @DeleteMapping("/delete")
    @Operation(summary = "delete folder")
    @SecurityRequirement(name = "token")
    public void deleteFolder(@RequestParam String fldId ) {
        log.debug("REST request to delete folder");
        folderService.folderDelete(fldId);
    }


    @CrossOrigin
    @PostMapping("/createSimple")
    @Operation(summary = "create folder")
    @SecurityRequirement(name = "token")
    public ResponseEntity<Folder> createSimple(@RequestBody String path) {
        log.debug("REST request to create folder simple");
        Folder folder=folderService.createFolderSimple(path);
        return ResponseEntity.ok().body(folder);
    }

}
