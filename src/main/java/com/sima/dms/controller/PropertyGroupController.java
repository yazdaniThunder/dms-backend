package com.sima.dms.controller;


import com.sima.dms.service.PropertyGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@Tag(name = "PropertyGroup")
@RequestMapping("/dms/propertyGroup")
public class PropertyGroupController {

    private final PropertyGroupService propertyService;


    @CrossOrigin
    @GetMapping("/getPropertiesSimple")
    @Operation(summary = "get Properties Simple")
    @SecurityRequirement(name = "token")
    public ResponseEntity <Map<String, String>> getPropertiesSimple(@RequestParam  String nodeId,@RequestParam String grpName){
        Map<String, String> bookmark=propertyService.getPropertiesSimple(nodeId,grpName);
        return ResponseEntity.ok().body(bookmark);
    }


    @CrossOrigin
    @DeleteMapping("/removeGroup")
    @Operation(summary = "remove groups")
    @SecurityRequirement(name = "token")
    public void removeGroups(@RequestParam  String nodeId,@RequestParam String grpName){
        propertyService.removeGroups(nodeId,grpName);
    }
}
