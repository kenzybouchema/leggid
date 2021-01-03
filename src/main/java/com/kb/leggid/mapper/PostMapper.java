package com.kb.leggid.mapper;

import com.kb.leggid.dto.PostRequest;
import com.kb.leggid.dto.PostResponse;
import com.kb.leggid.model.Post;
import com.kb.leggid.model.SubLeggid;
import com.kb.leggid.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "subLeggid", source = "subLeggid")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "description", source = "postRequest.description")
    Post map(PostRequest postRequest, SubLeggid subLeggid, User user);

    @Mapping(target = "id", source = "postId")
    @Mapping(target = "postName", source = "postName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "url", source = "url")
    @Mapping(target = "subLeggidName", source = "subLeggid.name")
    @Mapping(target = "userName", source = "user.username")
    PostResponse mapToDto(Post post);
}