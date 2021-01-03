package com.kb.leggid.service;

import com.kb.leggid.dto.PostRequest;
import com.kb.leggid.dto.PostResponse;
import com.kb.leggid.exceptions.PostNotFoundException;
import com.kb.leggid.exceptions.SubLeggidNotFoundException;
import com.kb.leggid.mapper.PostMapper;
import com.kb.leggid.model.Post;
import com.kb.leggid.model.SubLeggid;
import com.kb.leggid.model.User;
import com.kb.leggid.repository.PostRepository;
import com.kb.leggid.repository.SubLeggidRepository;
import com.kb.leggid.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final SubLeggidRepository subLeggidRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PostMapper postMapper;

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id.toString()));
        return postMapper.mapToDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::mapToDto)
                .collect(toList());
    }

    @Transactional
    public void save(PostRequest postRequest) {
        SubLeggid subLeggid = subLeggidRepository.findByName(postRequest.getSubLeggidName())
                .orElseThrow(() -> new SubLeggidNotFoundException(postRequest.getSubLeggidName()));
        postRepository.save(postMapper.map(postRequest, subLeggid, authService.getCurrentUser()));
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBysubLeggid(Long subLeggidId) {
        SubLeggid subLeggid = subLeggidRepository.findById(subLeggidId)
                .orElseThrow(() -> new SubLeggidNotFoundException(subLeggidId.toString()));
        List<Post> posts = postRepository.findAllBySubLeggid(subLeggid);
        return posts.stream().map(postMapper::mapToDto).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return postRepository.findByUser(user)
                .stream()
                .map(postMapper::mapToDto)
                .collect(toList());
    }
}