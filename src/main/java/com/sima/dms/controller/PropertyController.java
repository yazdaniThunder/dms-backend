package com.sima.dms.controller;


import com.sima.dms.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "Property")
@RequestMapping("/dms/property")
public class PropertyController {

    private final PropertyService propertyService;
    private final Logger log = LoggerFactory.getLogger(PropertyController.class);


    @PostMapping("/addCategory")
    @Operation(summary = "add Category")
    @SecurityRequirement(name = "token")
    public void addCategory(@RequestParam String nodeId,@RequestParam String catId) {
        log.debug("REST request add Category");
         propertyService.addCategory(nodeId,catId);
    }


    @DeleteMapping("/deleteCategory")
    @Operation(summary = "delete Category")
    @SecurityRequirement(name = "token")
    public void deleteCategory(@RequestParam String nodeId,@RequestParam String catId) {
        log.debug("REST request delete Category");
        propertyService.deleteCategory(nodeId,catId);
    }

}
