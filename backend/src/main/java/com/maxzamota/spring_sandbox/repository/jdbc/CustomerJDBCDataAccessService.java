package com.maxzamota.spring_sandbox.repository.jdbc;

import com.maxzamota.spring_sandbox.model.CustomerEntity;
import com.maxzamota.spring_sandbox.repository.CustomerDao;
import com.maxzamota.spring_sandbox.repository.mappers.CustomerRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {
    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    @Autowired
    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public CustomerEntity save(CustomerEntity customerEntity) {
        if (Objects.nonNull(customerEntity.getId()) && this.existsCustomerById(customerEntity.getId())) {
            var updateQuery = """
                    UPDATE customer
                    SET name = ?, email = ?, age = ?, gender = ?::gender
                    WHERE id = ?
                    """;
            this.jdbcTemplate.update(
                    updateQuery,
                    customerEntity.getName(),
                    customerEntity.getEmail(),
                    customerEntity.getAge(),
                    customerEntity.getGender().toString(),
                    customerEntity.getId()
            );
            return this.findById(customerEntity.getId()).orElseThrow();
        }
        var insertQuery = """
                INSERT INTO customer(name, email, age, gender)
                VALUES (?, ?, ?, ?::gender)
                """;
        this.jdbcTemplate.update(
                insertQuery,
                customerEntity.getName(),
                customerEntity.getEmail(),
                customerEntity.getAge(),
                customerEntity.getGender().toString()
        );
        return this.findCustomersByEmail(customerEntity.getEmail()).stream()
                .findFirst()
                .orElseThrow();

    }

    @Override
    public Collection<CustomerEntity> findAll() {
        var query = """
                SELECT * FROM customer
                """;
        return this.jdbcTemplate.query(query, this.customerRowMapper);
    }

    @Override
    public Optional<CustomerEntity> findById(Integer id) {
        var sql = """
                SELECT *
                FROM customer
                WHERE id = ?
                """;

        return this.jdbcTemplate.query(sql, this.customerRowMapper, id).stream()
                .findFirst();
    }

    @Override
    public boolean existsCustomerByEmail(String email) {
        var sql = """
                SELECT count(*)
                FROM customer
                WHERE email = ?
                """;
        Integer count = this.jdbcTemplate.queryForObject(sql, Integer.class, email);
        return Objects.nonNull(count) && count > 0;
    }

    @Override
    public boolean existsCustomerById(Integer id) {
        var query = """
                SELECT count(*)
                FROM customer
                WHERE id = ?
                """;
        Integer count = this.jdbcTemplate.queryForObject(query, Integer.class, id);
        return Objects.nonNull(count) && count > 0;
    }

    @Override
    public void deleteById(Integer id) {
        var query = """
                DELETE FROM customer
                WHERE id = ?
                """;
        this.jdbcTemplate.update(query, id);
    }

    @Override
    public Collection<CustomerEntity> findCustomersByEmail(String email) {
        var query = """
                SELECT *
                FROM customer
                WHERE email = ?
                """;
        return this.jdbcTemplate.query(query, this.customerRowMapper, email);
    }

    @Override
    public void clear() {
        var query = """
                TRUNCATE TABLE customer
                """;
        this.jdbcTemplate.batchUpdate(query);
    }
}
