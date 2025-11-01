package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.NastavnikPredmet;
import org.raflab.studsluzba.service.NastavnikPredmetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nastavnik-predmeti")
@RequiredArgsConstructor
public class NastavnikPredmetController {

    private final NastavnikPredmetService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NastavnikPredmet create(@RequestBody NastavnikPredmet body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public NastavnikPredmet findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public NastavnikPredmet update(@PathVariable Long id, @RequestBody NastavnikPredmet body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
