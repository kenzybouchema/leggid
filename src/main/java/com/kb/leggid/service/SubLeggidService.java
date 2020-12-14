package com.kb.leggid.service;

import com.kb.leggid.dto.SubLeggidDto;
import com.kb.leggid.exceptions.SpringRedditException;
import com.kb.leggid.model.SubLeggid;
import com.kb.leggid.repository.SubLeggidRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class SubLeggidService {

    private final SubLeggidRepository SubleggidRepository;

    @Transactional
    public SubLeggidDto save(SubLeggidDto subLeggidDto) {
        SubLeggid save = SubleggidRepository.save(mapToSubleggid(subLeggidDto));
        subLeggidDto.setId(save.getId());
        return subLeggidDto;
    }

    private SubLeggid mapToSubleggid(SubLeggidDto subLeggidDto) {
        return SubLeggid.builder()
                .name(subLeggidDto.getName())
                .description(subLeggidDto.getDescription())
                .build();
    }


    @Transactional(readOnly = true)
    public List<SubLeggidDto> getAll() {
        return SubleggidRepository.findAll().stream().map(this::mapToDto).collect(toList());
    }

    private SubLeggidDto mapToDto(SubLeggid subLeggid) {
        return SubLeggidDto.builder()
                .id(subLeggid.getId())
                .name(subLeggid.getName())
                .description(subLeggid.getDescription())
                .build();
    }

    @Transactional(readOnly = true)
     public SubLeggidDto getSubleggid(Long id) {
         SubLeggid subLeggid = SubleggidRepository.findById(id)
                 .orElseThrow(() -> new SpringRedditException("No Subleggid found with ID - " + id));
         return mapToDto(subLeggid);
     }
}