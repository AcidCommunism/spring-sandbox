package com.maxzamota.spring_sandbox.service;

import com.maxzamota.spring_sandbox.exception.BadRequestException;
import com.maxzamota.spring_sandbox.exception.DuplicateResourceException;
import com.maxzamota.spring_sandbox.exception.ResourceNotFoundException;
import com.maxzamota.spring_sandbox.model.CustomerEntity;
import com.maxzamota.spring_sandbox.repository.jpa.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(@Qualifier("jpa") CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Page<CustomerEntity> getAll(
            Integer pageNum,
            Integer pageSize,
            String sortBy,
            String sortDirection
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        return this.customerRepository.findAll(pageable);
    }

    public Page<CustomerEntity> getAll(Pageable pageable) {
        Page<CustomerEntity> customers;
        try {
            customers = this.customerRepository.findAll(pageable);
            return customers;
        } catch (PropertyReferenceException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public CustomerEntity getCustomerById(Integer id) {
        return this.customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id={%s} not found!"
                        .formatted(id)));
    }

    public CustomerEntity save(CustomerEntity customerEntity) {
        if (this.customerRepository.existsCustomerByEmail(customerEntity.getEmail())) {
            throw new DuplicateResourceException(
                    "Customer with email={%s} already exists!".formatted(customerEntity.getEmail())
            );
        }
        return this.customerRepository.save(customerEntity);
    }

    public String deleteById(Integer id) {
        this.customerRepository.deleteById(id);
        return "Entity with id={%s} successfully deleted (or ignored if it did not exist in the first place)!"
                .formatted(id);
    }

    public CustomerEntity update(CustomerEntity customerEntity) {
        if (!this.customerRepository.existsCustomerById(customerEntity.getId())) {
            throw new ResourceNotFoundException("Customer with id={%s} not found!"
                    .formatted(customerEntity.getId()));
        }
        if (customerEntity.equals(
                this.customerRepository
                        .findById(customerEntity.getId())
                        .orElseGet(() -> null))
        ) {
            return customerEntity;
        }
        if (!this.customerRepository.findCustomersByEmail(customerEntity.getEmail())
                .stream()
                .filter(record -> !Objects.equals(record.getId(), customerEntity.getId()))
                .toList()
                .isEmpty()
        ) {
            throw new DuplicateResourceException("Customer with email={%s} already exists!"
                    .formatted(customerEntity.getEmail())
            );
        }
        return this.customerRepository.save(customerEntity);
    }

    public Collection<CustomerEntity> saveAll(Collection<CustomerEntity> customerEntities) {
        return this.customerRepository.saveAll(customerEntities);
    }

    public void deleteAll() {
        this.customerRepository.deleteAll();
    }
}
