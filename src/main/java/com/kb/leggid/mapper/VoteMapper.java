package com.kb.leggid.mapper;

import com.kb.leggid.dto.VoteDto;
import com.kb.leggid.model.Vote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VoteMapper {

    @Mapping(target = "postId", source = "post.postId")
    VoteDto mapToDto(Vote vote);
}
