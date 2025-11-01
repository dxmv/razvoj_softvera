package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.IspitniRok;
import org.raflab.studsluzba.service.IspitniRokService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ispitni-rokovi")
@RequiredArgsConstructor
public class IspitniRokController {

    private final IspitniRokService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IspitniRok create(@RequestBody IspitniRok body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public IspitniRok findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public IspitniRok update(@PathVariable Long id, @RequestBody IspitniRok body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
