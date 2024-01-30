package com.github.gluhov.repository.jpa;

import com.github.gluhov.model.Label;
import com.github.gluhov.model.Post;
import com.github.gluhov.repository.PostRepository;
import com.github.gluhov.util.DatabaseUtil;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class JpaPostRepositoryImpl implements PostRepository {
    private final String GET_BY_ID = "SELECT p, l FROM Post p LEFT JOIN p.labels l WHERE p.id = :id";
    private final String CHECK_EXISTS = "SELECT count(id) FROM Post p WHERE p.id = :id";
    private final String FIND_ALL = "SELECT p FROM Post p";

    private final SessionFactory sessionFactory = DatabaseUtil.getSessionFactory();

    private Session openSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Optional<Post> getById(Long id) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            Query query = session.createQuery(GET_BY_ID);
            query.setParameter("id", id);
            List<Object[]> results = query.getResultList();
            Post post = null;
            if (!results.isEmpty()) {
                post = (Post) results.get(0)[0];
                Set<Label> labels = new HashSet<>();

                for (Object[] result : results) {
                    Label label = (Label) result[1];
                    if (label != null) {
                        labels.add(label);
                    }
                }

                post.setLabels(labels);
            }
            transaction.commit();
            return Optional.ofNullable(post);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            Post post = session.get(Post.class, id);
            session.remove(post);
            transaction.commit();
        }
    }

    @Override
    public Optional<Post> save(Post post) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            LocalDateTime created = LocalDateTime.now();
            post.setCreated(created);
            post.setUpdated(created);
            session.merge(post);
            transaction.commit();
            return Optional.of(post);
        }
    }

    @Override
    public Optional<Post> update(Post post) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            LocalDateTime updated = LocalDateTime.now();
            post.setUpdated(updated);
            session.merge(post);
            transaction.commit();
            return Optional.of(post);
        }
    }

    @Override
    public Optional<List<Post>> findAll() {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Post> posts = session.createQuery(FIND_ALL, Post.class).getResultList();
            transaction.commit();
            return Optional.ofNullable(posts);
        }
    }

    @Override
    public Boolean checkIfExists(Long id) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            Query query = session.createQuery(CHECK_EXISTS, Long.class);
            query.setParameter("id", id);
            Long res = (Long) query.getSingleResult();
            transaction.commit();
            return  res == 1;
        }
    }
}
