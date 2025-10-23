package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.ActivateColorRequest;
import com.swp391.edrive.dto.request.VersionColorPricePatchRequest;
import com.swp391.edrive.dto.request.VersionColorUpsertRequest;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.service.VersionColorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles/{versionId}/colors")
@RequiredArgsConstructor
@Tag(name = "Vehicle Colors", description = "Quản lý màu của phiên bản xe")
public class VersionColorController {
    private final VersionColorService colorService;

    @GetMapping
    public ResponseEntity<ResponseObject> list(@PathVariable Long versionId,
                                               @RequestParam(required = false) Boolean active) {
        var data = colorService.list(versionId, active);
        return ResponseEntity.ok(new ResponseObject(200, "Color list retrieved", data));
    }

    @PostMapping
    public ResponseEntity<ResponseObject> create(@PathVariable Long versionId,
                                                 @Valid @RequestBody VersionColorUpsertRequest req) {
        var data = colorService.create(versionId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(201, "Color created", data));
    }

    @PutMapping("/{colorId}")
    public ResponseEntity<ResponseObject> update(@PathVariable Long versionId,
                                                 @PathVariable Long colorId,
                                                 @Valid @RequestBody VersionColorUpsertRequest req) {
        var data = colorService.update(versionId, colorId, req);
        return ResponseEntity.ok(new ResponseObject(200, "Color updated", data));
    }

    @PatchMapping("/{colorId}/price")
    public ResponseEntity<ResponseObject> patchPrice(@PathVariable Long versionId,
                                                     @PathVariable Long colorId,
                                                     @RequestBody VersionColorPricePatchRequest req) {
        var data = colorService.patchPrice(versionId, colorId, req);
        return ResponseEntity.ok(new ResponseObject(200, "Color price updated", data));
    }

    @PatchMapping("/{colorId}/activate")
    public ResponseEntity<ResponseObject> activate(@PathVariable Long versionId,
                                                   @PathVariable Long colorId,
                                                   @RequestBody ActivateColorRequest req) {
        var data = colorService.activate(versionId, colorId, req.getActive());
        return ResponseEntity.ok(new ResponseObject(200, req.getActive() ? "Color activated" : "Color deactivated", data));
    }

    @DeleteMapping("/{colorId}")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long versionId,
                                                 @PathVariable Long colorId) {
        colorService.delete(versionId, colorId);
        return ResponseEntity.ok(new ResponseObject(200, "Color deleted", null));
    }
}
