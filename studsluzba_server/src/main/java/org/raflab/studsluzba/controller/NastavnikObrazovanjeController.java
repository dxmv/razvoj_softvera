package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.NastavnikObrazovanje;
import org.raflab.studsluzba.service.NastavnikObrazovanjeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nastavnik-obrazovanja")
@RequiredArgsConstructor
public class NastavnikObrazovanjeController {

    private final NastavnikObrazovanjeService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NastavnikObrazovanje create(@RequestBody NastavnikObrazovanje body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public NastavnikObrazovanje findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public NastavnikObrazovanje update(@PathVariable Long id, @RequestBody NastavnikObrazovanje body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
