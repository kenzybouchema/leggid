package com.kb.leggid.repository;

import com.kb.leggid.model.Comment;
import com.kb.leggid.model.Post;
import com.kb.leggid.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByUser(User user);

    List<Comment> findByPost(Post post);
}
