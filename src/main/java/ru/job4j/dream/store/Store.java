package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.util.Collection;

public interface Store {
    Collection<Post> findAllPosts();

    Collection<Candidate> findAllCandidates();

    Collection<User> findAllUsers();

    void save(Post post);

    void save(User user);

    void save(Candidate candidate);

    Post findById(int id);

    User findUserById(int id);

    void removeCandidate(int id);

    void removeUser(int id);

    Candidate findCandidateById(int id);

    User findUserByEmail(String email);
}