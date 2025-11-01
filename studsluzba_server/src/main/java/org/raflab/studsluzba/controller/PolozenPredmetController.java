package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.PolozenPredmet;
import org.raflab.studsluzba.service.PolozenPredmetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/polozeni-predmeti")
@RequiredArgsConstructor
public class PolozenPredmetController {

    private final PolozenPredmetService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PolozenPredmet create(@RequestBody PolozenPredmet body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public PolozenPredmet findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public PolozenPredmet update(@PathVariable Long id, @RequestBody PolozenPredmet body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
