package com.swp391.edrive.controller;

import com.swp391.edrive.dto.request.ColorRequest;
import com.swp391.edrive.dto.response.ColorResponse;
import com.swp391.edrive.service.ColorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/colors")
@SecurityRequirement(name = "api")
public class ColorController {

    private final ColorService colorService;

    @PostMapping
    public ColorResponse create(@RequestBody @Valid ColorRequest req) {
        return colorService.create(req);
    }

    @PutMapping("/{id}")
    public ColorResponse update(@PathVariable Long id, @RequestBody @Valid ColorRequest req) {
        return colorService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean force) {
        colorService.delete(id, force);
    }

    @GetMapping("/{id}")
    public ColorResponse getById(@PathVariable Long id) {
        return colorService.getById(id);
    }

    @GetMapping
    public List<ColorResponse> list(@RequestParam(required = false) String q) {
        return (q == null || q.isBlank()) ? colorService.getAll() : colorService.search(q);
    }
}
