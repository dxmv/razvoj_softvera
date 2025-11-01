package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.Nastavnik;
import org.raflab.studsluzba.service.NastavnikService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nastavnici")
@RequiredArgsConstructor
public class NastavnikController {

    private final NastavnikService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Nastavnik create(@RequestBody Nastavnik body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public Nastavnik findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Nastavnik update(@PathVariable Long id, @RequestBody Nastavnik body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
