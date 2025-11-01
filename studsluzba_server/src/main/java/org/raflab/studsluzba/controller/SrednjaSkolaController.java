package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.SrednjaSkola;
import org.raflab.studsluzba.service.SrednjaSkolaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/srednje-skole")
@RequiredArgsConstructor
public class SrednjaSkolaController {

    private final SrednjaSkolaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SrednjaSkola create(@RequestBody SrednjaSkola body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public SrednjaSkola findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public SrednjaSkola update(@PathVariable Long id, @RequestBody SrednjaSkola body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
