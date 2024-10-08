package com.maxzamota.spring_sandbox.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maxzamota.spring_sandbox.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE customer SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted <> TRUE")
@ToString(onlyExplicitlyIncluded = true)
@Table(
        name = "customer",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "customer_email_unique",
                        columnNames = "email"
                )
        }
)
public class CustomerEntity {
    @Id
    @Column(nullable = false, name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Integer id;

    @Column(nullable = false)
    @ToString.Include
    @NonNull
    private String name;

    @Column(nullable = false, unique = true)
    @ToString.Include
    @NonNull
    private String email;

    @Column(nullable = false)
    @ToString.Include
    @NonNull
    private Integer age;

    @Column(nullable = false)
    @ToString.Include
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::gender")
    @NonNull
    private Gender gender;

    @Column(
            name = "is_deleted",
            nullable = false
    )
    @JsonIgnore
    private boolean isDeleted;

    public CustomerEntity(
            @NonNull String name,
            @NonNull String email,
            @NonNull Integer age,
            @NonNull Gender gender,
            boolean isDeleted
    ) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.isDeleted = isDeleted;
    }

    public CustomerEntity(CustomerBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.age = builder.age;
        this.gender = builder.gender;
        this.isDeleted = builder.isDeleted;
    }

    @Setter
    public static class CustomerBuilder {
        private Integer id;
        private String name;
        private String email;
        private Integer age;
        private Gender gender;
        private boolean isDeleted;

        public CustomerEntity build() {
            return new CustomerEntity(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerEntity that = (CustomerEntity) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(email, that.email)
                && Objects.equals(age, that.age)
                && gender == that.gender
                && isDeleted == that.isDeleted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, age, gender, isDeleted);
    }
}
