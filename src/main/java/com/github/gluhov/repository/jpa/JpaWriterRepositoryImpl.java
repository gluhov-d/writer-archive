package com.github.gluhov.repository.jpa;

import com.github.gluhov.model.Post;
import com.github.gluhov.model.Writer;
import com.github.gluhov.repository.WriterRepository;
import com.github.gluhov.util.DatabaseUtil;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class JpaWriterRepositoryImpl implements WriterRepository {

    private final String CHECK_EXISTS = "SELECT count(id) FROM Writer w WHERE w.id = :id";
    private final String FIND_ALL = "SELECT w FROM Writer w";

    private final SessionFactory sessionFactory = DatabaseUtil.getSessionFactory();

    @Override
    public Optional<Writer> getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Writer> cq = cb.createQuery(Writer.class);

            Root<Writer> writerRoot = cq.from(Writer.class);
            Fetch<Writer, Post> postFetch = writerRoot.fetch("posts", JoinType.LEFT);
            postFetch.fetch("labels", JoinType.LEFT);
            cq.where(cb.equal(writerRoot.get("id"), id));
            TypedQuery<Writer> query = session.createQuery(cq);
            return query.getResultList().stream().findFirst();
        } catch (Exception e) {
            System.out.println("Failed to get writer.");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Writer writer = session.get(Writer.class, id);
                if (writer != null) {
                    session.remove(writer);
                    transaction.commit();
                }
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                System.out.println("Failed to delete writer.");
                e.printStackTrace();
            }
        }
    }

    @Override
    public Optional<Writer> save(Writer writer) {
        try (Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            Set<Post> postsToSave = new HashSet<>();
            for (Post p: writer.getPosts()) {
                try {
                    Post existingPost = session.get(Post.class, p.getId());
                    if (existingPost != null) {
                        postsToSave.add(existingPost);
                    }
                } catch (Exception e) {
                    if (transaction != null) {
                        transaction.rollback();
                    }
                    System.out.println("Failed to save writer.");
                }
            }
            writer.setPosts(postsToSave);
            session.persist(writer);
            if (transaction != null) transaction.commit();
            return Optional.of(writer);
        }
    }

    @Override
    public Optional<Writer> update(Writer writer) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Writer existingWriter = session.get(Writer.class, writer.getId());

                if (existingWriter != null) {
                    Hibernate.initialize(existingWriter.getPosts());
                    existingWriter.getPosts().removeIf(existingPost ->
                            writer.getPosts().stream().noneMatch(newPost ->
                                    newPost.getId().equals(existingPost.getId())));
                    for (Post post: writer.getPosts()) {
                        Post existingPost = session.get(Post.class, post.getId());
                        existingWriter.getPosts().add(existingPost);
                    }
                    existingWriter.setFirstName(writer.getFirstName());
                    existingWriter.setLastName(writer.getLastName());
                    session.merge(existingWriter);
                    transaction.commit();
                    return Optional.of(existingWriter);
                }
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                System.out.println("Failed to update writer.");
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<Writer>> findAll() {
        try (Session session = sessionFactory.openSession()) {
            List<Writer> writers = session.createQuery(FIND_ALL, Writer.class).getResultList();
            return Optional.ofNullable(writers);
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
