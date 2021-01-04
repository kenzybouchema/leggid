package com.kb.leggid.service;

import com.kb.leggid.dto.CommentsDto;
import com.kb.leggid.exceptions.PostNotFoundException;
import com.kb.leggid.mapper.CommentMapper;
import com.kb.leggid.model.Comment;
import com.kb.leggid.model.NotificationEmail;
import com.kb.leggid.model.Post;
import com.kb.leggid.model.User;
import com.kb.leggid.repository.CommentRepository;
import com.kb.leggid.repository.PostRepository;
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
public class CommentService {

    //TODO: Construct POST URL
    private static final String POST_URL = "";

    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

    @Transactional
    public CommentsDto createComment(CommentsDto commentsDto) {
        Post post = postRepository.findById(commentsDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(commentsDto.getPostId().toString()));
        Comment comment = commentMapper.map(commentsDto, post, authService.getCurrentUser());
        Comment createdComment = commentRepository.save(comment);
        String message = mailContentBuilder.build(post.getUser().getUsername() + " posted a comment on your post." + POST_URL);
        sendCommentNotification(message, post.getUser());
        return commentMapper.mapToDto(createdComment);
    }

    @Transactional(readOnly = true)
    public List<CommentsDto> getAllComments() {
        return commentRepository.findAll()
                .stream()
                .map(commentMapper::mapToDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<CommentsDto> getCommentByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId.toString()));
        return commentRepository.findByPost(post)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<CommentsDto> getCommentsByUser(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
        return commentRepository.findAllByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(toList());
    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() + " Commented on your post", user.getEmail(), message));
    }
}