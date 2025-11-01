package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.Predmet;
import org.raflab.studsluzba.service.PredmetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/predmeti")
@RequiredArgsConstructor
public class PredmetController {

    private final PredmetService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Predmet create(@RequestBody Predmet body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public Predmet findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Predmet update(@PathVariable Long id, @RequestBody Predmet body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
