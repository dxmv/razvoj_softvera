package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.PrijavaIspita;
import org.raflab.studsluzba.service.PrijavaIspitaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prijave-ispita")
@RequiredArgsConstructor
public class PrijavaIspitaController {

    private final PrijavaIspitaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrijavaIspita create(@RequestBody PrijavaIspita body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public PrijavaIspita findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public PrijavaIspita update(@PathVariable Long id, @RequestBody PrijavaIspita body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
