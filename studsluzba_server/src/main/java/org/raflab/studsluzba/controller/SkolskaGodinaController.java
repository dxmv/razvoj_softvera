package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.SkolskaGodina;
import org.raflab.studsluzba.service.SkolskaGodinaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/skolske-godine")
@RequiredArgsConstructor
public class SkolskaGodinaController {

    private final SkolskaGodinaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkolskaGodina create(@RequestBody SkolskaGodina body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public SkolskaGodina findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public SkolskaGodina update(@PathVariable Long id, @RequestBody SkolskaGodina body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
