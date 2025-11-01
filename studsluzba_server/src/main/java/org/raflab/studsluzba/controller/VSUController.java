package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.VSU;
import org.raflab.studsluzba.service.VSUService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vsu")
@RequiredArgsConstructor
public class VSUController {

    private final VSUService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VSU create(@RequestBody VSU body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public VSU findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public VSU update(@PathVariable Long id, @RequestBody VSU body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
