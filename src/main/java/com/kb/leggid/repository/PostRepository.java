package com.kb.leggid.repository;

import com.kb.leggid.model.Post;
import com.kb.leggid.model.SubLeggid;
import com.kb.leggid.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllBySubLeggid(SubLeggid subLeggid);

    List<Post> findByUser(User user);
}
