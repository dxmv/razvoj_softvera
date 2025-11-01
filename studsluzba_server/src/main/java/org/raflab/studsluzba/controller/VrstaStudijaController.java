package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.VrstaStudija;
import org.raflab.studsluzba.service.VrstaStudijaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vrste-studija")
@RequiredArgsConstructor
public class VrstaStudijaController {

    private final VrstaStudijaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VrstaStudija create(@RequestBody VrstaStudija body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public VrstaStudija findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public VrstaStudija update(@PathVariable Long id, @RequestBody VrstaStudija body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
