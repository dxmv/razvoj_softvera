package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.NastavnikZvanje;
import org.raflab.studsluzba.service.NastavnikZvanjeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nastavnik-zvanja")
@RequiredArgsConstructor
public class NastavnikZvanjeController {

    private final NastavnikZvanjeService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NastavnikZvanje create(@RequestBody NastavnikZvanje body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public NastavnikZvanje findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public NastavnikZvanje update(@PathVariable Long id, @RequestBody NastavnikZvanje body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
