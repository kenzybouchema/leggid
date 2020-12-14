package com.kb.leggid.controller;

import com.kb.leggid.dto.SubLeggidDto;
import com.kb.leggid.service.SubLeggidService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subleggid")
@AllArgsConstructor
@Slf4j
public class SubLeggidController {

    private final SubLeggidService subleggidService;

    @PostMapping
    public ResponseEntity<SubLeggidDto> createSubleggid(@RequestBody SubLeggidDto subleggidDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subleggidService.save(subleggidDto));
    }

    @GetMapping
    public ResponseEntity<List<SubLeggidDto>> getAllSubleggids() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(subleggidService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubLeggidDto> getSubleggid(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(subleggidService.getSubleggid(id));
    }
}
