package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.PredispitnaObaveza;
import org.raflab.studsluzba.service.PredispitnaObavezaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/predispitne-obaveze")
@RequiredArgsConstructor
public class PredispitnaObavezaController {

    private final PredispitnaObavezaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PredispitnaObaveza create(@RequestBody PredispitnaObaveza body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public PredispitnaObaveza findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public PredispitnaObaveza update(@PathVariable Long id, @RequestBody PredispitnaObaveza body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
