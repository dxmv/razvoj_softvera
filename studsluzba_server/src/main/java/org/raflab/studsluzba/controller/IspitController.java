package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.Ispit;
import org.raflab.studsluzba.service.IspitService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ispiti")
@RequiredArgsConstructor
public class IspitController {

    private final IspitService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ispit create(@RequestBody Ispit body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public Ispit findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Ispit update(@PathVariable Long id, @RequestBody Ispit body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
