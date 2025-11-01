package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.Indeks;
import org.raflab.studsluzba.service.IndeksService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/indeksi")
@RequiredArgsConstructor
public class IndeksController {

    private final IndeksService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Indeks create(@RequestBody Indeks body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public Indeks findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Indeks update(@PathVariable Long id, @RequestBody Indeks body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
