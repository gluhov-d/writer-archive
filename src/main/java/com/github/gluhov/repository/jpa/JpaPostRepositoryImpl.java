package com.github.gluhov.repository.jpa;

import com.github.gluhov.model.Post;
import com.github.gluhov.model.PostStatus;
import com.github.gluhov.repository.PostRepository;
import com.github.gluhov.util.DatabaseUtil;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class JpaPostRepositoryImpl implements PostRepository {
    private final String GET_BY_ID = "SELECT p FROM Post p LEFT JOIN FETCH p.labels WHERE p.id = :id";
    private final String CHECK_EXISTS = "SELECT count(id) FROM Post p WHERE p.id = :id";
    private final String FIND_ALL = "SELECT p FROM Post p";

    private final SessionFactory sessionFactory = DatabaseUtil.getSessionFactory();

    @Override
    public Optional<Post> getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.createQuery(GET_BY_ID, Post.class).setParameter("id", id).getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Post post = session.get(Post.class, id);
                if (post != null) {
                    post.setStatus(PostStatus.DELETED);
                    session.merge(post);
                    transaction.commit();
                }
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                    System.out.println("Failed to delete post.");
                }
            }
        }
    }

    @Override
    public Optional<Post> save(Post post) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(post);
            transaction.commit();
            return Optional.of(post);
        }
    }

    @Override
    public Optional<Post> update(Post post) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Post updated = session.merge(post);
            transaction.commit();
            return Optional.of(updated);
        }
    }

    @Override
    public Optional<List<Post>> findAll() {
        try (Session session = sessionFactory.openSession()) {
            List<Post> posts = session.createQuery(FIND_ALL, Post.class).getResultList();
            return Optional.ofNullable(posts);
        }
    }

    @Override
    public Boolean checkIfExists(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery(CHECK_EXISTS, Long.class);
            query.setParameter("id", id);
            Long res = (Long) query.getSingleResult();
            return  res == 1;
        }
    }
}
