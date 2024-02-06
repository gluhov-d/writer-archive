package com.github.gluhov.repository.jpa;

import com.github.gluhov.model.Label;
import com.github.gluhov.model.Post;
import com.github.gluhov.repository.LabelRepository;
import com.github.gluhov.util.DatabaseUtil;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class JpaLabelRepositoryImpl implements LabelRepository {
    private final String CHECK_EXISTS = "SELECT count(id) FROM Label WHERE id = :id";
    private final String FIND_ALL = "SELECT l FROM Label l";
    private final SessionFactory sessionFactory = DatabaseUtil.getSessionFactory();

    @Override
    public Optional<Label> getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Label label = session.get(Label.class, id);
            return Optional.ofNullable(label);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            try {
                Label label = session.get(Label.class, id);
                if (label != null) {
                    removePostAssociations(label);
                    session.remove(label);
                    transaction.commit();
                }

            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                System.out.println("Failed to delete label.");
                e.printStackTrace();
            }
        }
    }

    private static void removePostAssociations(Label label) {
        Iterator<Post> postIterator = label.getPosts().iterator();
        while (postIterator.hasNext()) {
            Post post = postIterator.next();
            postIterator.remove();
            label.removePost(post);
        }
    }

    @Override
    public Optional<Label> save(Label label) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(label);
            transaction.commit();
            return Optional.of(label);
        }
    }

    @Override
    public Optional<Label> update(Label label) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Label existingLabel = session.get(Label.class, label.getId());
                if (existingLabel != null) {
                    existingLabel.setName(label.getName());
                    session.merge(existingLabel);
                    transaction.commit();
                    return Optional.of(existingLabel);
                }
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                System.out.println("Failed to update label.");
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<Label>> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.createQuery(FIND_ALL, Label.class).getResultList());
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
