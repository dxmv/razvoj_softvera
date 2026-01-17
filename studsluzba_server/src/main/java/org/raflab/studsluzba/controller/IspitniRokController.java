package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.IspitniRok;
import org.raflab.studsluzba.model.dto.IspitPrikazDto;
import org.raflab.studsluzba.model.dto.IspitniRokDto;
import org.raflab.studsluzba.service.IspitniRokService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public IspitniRokDto create2(@RequestBody IspitniRokDto dto) {
        return service.create2(dto);
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
    @GetMapping
    public List<IspitniRokDto> findAll() {
        return service.findAll();
    }
    @GetMapping("/{id}/ispiti")
    public List<IspitPrikazDto> findIspitiByRok(@PathVariable Long id) {
        return service.findIspitiByRok(id);
    }
}
