package com.github.gluhov.repository.jpa;

import com.github.gluhov.model.Writer;
import com.github.gluhov.repository.WriterRepository;
import com.github.gluhov.util.DatabaseUtil;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class JpaWriterRepositoryImpl implements WriterRepository {

    private final String CHECK_EXISTS = "SELECT count(id) FROM Writer w WHERE w.id = :id";
    private final String FIND_ALL = "SELECT w FROM Writer w";

    private final SessionFactory sessionFactory = DatabaseUtil.getSessionFactory();

    private Session openSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Optional<Writer> getById(Long id) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            Writer writer = session.get(Writer.class, id);
            transaction.commit();
            return Optional.ofNullable(writer);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            Writer writer = session.get(Writer.class, id);
            session.remove(writer);
            transaction.commit();
        }
    }

    @Override
    public Optional<Writer> save(Writer writer) {
        try (Session session = openSession()){
            Transaction transaction = session.beginTransaction();
            session.persist(writer);
            transaction.commit();
            return Optional.of(writer);
        }
    }

    @Override
    public Optional<Writer> update(Writer writer) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(writer);
            transaction.commit();
            return Optional.of(writer);
        }
    }

    @Override
    public Optional<List<Writer>> findAll() {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Writer> writers = session.createQuery(FIND_ALL, Writer.class).getResultList();
            transaction.commit();
            return Optional.ofNullable(writers);
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
