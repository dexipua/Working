package com.calendar.backend.repositories.specification;

import com.calendar.backend.models.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserSpecification {
    public static Specification<User> filterUsers(Map<String, Object> filters) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (filters == null || filters.isEmpty()) {
                return null;
            }
            List<Predicate> predicates = new ArrayList<>();

            if (filters.containsKey("firstName")) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                        "%" + filters.get("firstName").toString().toLowerCase() + "%"));
            }

            if (filters.containsKey("lastName")) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),
                        "%" + filters.get("lastName").toString().toLowerCase() + "%"));
            }

            if (filters.containsKey("email")) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                        "%" + filters.get("email").toString().toLowerCase() + "%"));
            }

            if (filters.containsKey("role")) {
                String role = filters.get("role").toString();
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<User> notUser(Long userId) {
        return (root, query, cb) ->
                cb.notEqual(root.get("id"), userId);
    }

    public static Specification<User> notIncludeDeleted(){
        String emailOfDeletedUser = "!deleted-user!@deleted.com";
        return (root, query, cb) ->
                cb.notEqual(root.get("email"), emailOfDeletedUser);
    }
}
