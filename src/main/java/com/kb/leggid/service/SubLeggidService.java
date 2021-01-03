package com.kb.leggid.service;

import com.kb.leggid.dto.SubLeggidDto;
import com.kb.leggid.exceptions.SpringRedditException;
import com.kb.leggid.mapper.SubLeggidMapper;
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

    private final SubLeggidMapper subLeggidMapper;

    @Transactional
    public SubLeggidDto save(SubLeggidDto subLeggidDto) {
        SubLeggid save = SubleggidRepository.save(subLeggidMapper.mapDtoToSubreddit(subLeggidDto));
        subLeggidDto.setId(save.getId());
        return subLeggidDto;
    }

    @Transactional(readOnly = true)
    public List<SubLeggidDto> getAll() {
        return SubleggidRepository.findAll().stream().map(subLeggidMapper::mapSubredditToDto).collect(toList());
    }

    @Transactional(readOnly = true)
     public SubLeggidDto getSubleggid(Long id) {
         SubLeggid subLeggid = SubleggidRepository.findById(id)
                 .orElseThrow(() -> new SpringRedditException("No Subleggid found with ID - " + id));
         return subLeggidMapper.mapSubredditToDto(subLeggid);
     }
}