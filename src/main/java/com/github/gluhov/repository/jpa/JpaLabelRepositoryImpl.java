package com.github.gluhov.repository.jpa;

import com.github.gluhov.model.Label;
import com.github.gluhov.repository.LabelRepository;
import com.github.gluhov.util.DatabaseUtil;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class JpaLabelRepositoryImpl implements LabelRepository {
    private final String CHECK_EXISTS = "SELECT count(id) FROM Label WHERE id = :id";
    private final String FIND_ALL = "SELECT l FROM Label l";
    private final String GET_BY_ID_JOIN_FETCH = "SELECT l FROM Label l JOIN FETCH l.posts WHERE l.id = :id";

    private final SessionFactory sessionFactory = DatabaseUtil.getSessionFactory();

    private Session openSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Optional<Label> getById(Long id) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            Label label = session.get(Label.class, id);
            transaction.commit();
            return Optional.ofNullable(label);
        }
    }

    public Optional<Label> getByIdJoinFetch(Long id) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            Query query = session.createQuery(GET_BY_ID_JOIN_FETCH, Label.class);
            query.setParameter("id", id);
            Label label = (Label) query.getSingleResult();

            transaction.commit();
            return Optional.ofNullable(label);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = openSession()){
            Transaction transaction = session.beginTransaction();
            Label label = session.get(Label.class, id);
            session.remove(label);
            transaction.commit();
        }
    }

    @Override
    public Optional<Label> save(Label label) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(label);
            transaction.commit();
            return Optional.of(label);
        }
    }

    @Override
    public Optional<Label> update(Label label) {

        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(label);
            transaction.commit();
            return Optional.of(label);
        }
    }

    @Override
    public Optional<List<Label>> findAll() {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Label> labels = session.createQuery(FIND_ALL, Label.class).getResultList();
            transaction.commit();
            return Optional.ofNullable(labels);
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
