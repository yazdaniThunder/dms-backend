package com.sima.dms.controller;

import com.sima.dms.domain.dto.MetadataTreeDto;
import com.sima.dms.service.MetadataTreeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "Metadata")
@RequestMapping("/dms/metadata")
public class MetadataTreeController {

    private final MetadataTreeService metadataTreeService;
    private final Logger log = LoggerFactory.getLogger(MetadataTreeController.class);

    @PostMapping
    @Operation(summary = "Create Metadata Tree")
    public ResponseEntity<List<MetadataTreeDto>> create(@RequestBody List<MetadataTreeDto> dto) {
        log.debug("REST request to create Metadata Tree : " + dto);
        List<MetadataTreeDto> metadataTreeDtos = metadataTreeService.save(dto);
        return ResponseEntity.ok().body(metadataTreeDtos);
    }

    @PutMapping
    @Operation(summary = "Update Metadata Tree")
    public ResponseEntity<List<MetadataTreeDto>> update(@RequestBody List<MetadataTreeDto> dto) {
        log.debug("REST request to update Metadata Tree : ", dto);
        List<MetadataTreeDto> metadataTreeDtos = metadataTreeService.save(dto);
        return ResponseEntity.ok().body(metadataTreeDtos);
    }

//    @PostMapping
//    @Operation(summary = "Create Metadata Tree")
//    public ResponseEntity<List<MetadataTreeDto>> create(@RequestBody List<String> names, @RequestParam(required = false) Long parentId) {
//        log.debug("REST request to create Metadata Tree : ", names, parentId);
//        List<MetadataTreeDto> metadataTrees = metadataTreeService.save(parentId, names);
//        return ResponseEntity.ok().body(metadataTrees);
//    }

    @GetMapping("/byParent/{parentId}")
    @Operation(summary = "Get Metadata Tree by parentId")
    public ResponseEntity<List<MetadataTreeDto>> getByParent(@PathVariable Long parentId) {
        log.debug("REST request to get Metadata Tree : ", parentId);
        List<MetadataTreeDto> dto = metadataTreeService.findByPatent(parentId);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping
    @Operation(summary = "Get Metadata Tree")
    public ResponseEntity<List<MetadataTreeDto>> getMetadataTree() {
        log.debug("REST request to get Metadata Tree : ");
        List<MetadataTreeDto> dto = metadataTreeService.getMetadataTree();
        return ResponseEntity.ok().body(dto);
    }


//    @GetMapping("/{id}")
//    @Operation(summary = "Get Metadata Tree by id")
//    public ResponseEntity<MetadataTreeDto> getById(@PathVariable Long id) {
//        log.debug("REST request to get Metadata Tree : ", id);
//        MetadataTreeDto dto = metadataTreeService.findById(id);
//        return ResponseEntity.ok().body(dto);
//    }
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Delete Metadata Tree by id")
//    public void delete(@PathVariable Long id) {
//        log.debug("REST request to delete Metadata Tree : ", id);
//        metadataTreeService.delete(id);
//    }
}
