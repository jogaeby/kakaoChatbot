package com.chatbot.base.domain.product;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProductSpecification {

    public static Specification<Product> withDynamicQuery(String category, String input) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || input == null || input.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            switch (category) {
                case "title":
                    return criteriaBuilder.like(root.get("title"), "%" + input + "%");
                case "no":
                    return criteriaBuilder.like(root.get("no"), "%" + input + "%");
                case "category":
                    return criteriaBuilder.like(root.get("category"), "%" + input + "%");
                case "location":
                    return criteriaBuilder.like(root.get("location"), "%" + input + "%");
                case "saleDate":
                    return saleDatePredicate(root, criteriaBuilder, input);
                case "createDate":
                    return createDatePredicate(root, criteriaBuilder, input);
                default:
                    return criteriaBuilder.conjunction();
            }
        };
    }
    // LocalDate 처리 (saleDate)
    private static Predicate saleDatePredicate(Root<Product> root, CriteriaBuilder cb, String input) {
        try {
            LocalDate date = LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return cb.equal(root.get("saleDate"), date);
        } catch (Exception e) {
            return cb.conjunction(); // 형식 불일치 시 무조건 참 반환
        }
    }
    // LocalDateTime 처리 (createDate)
    private static Predicate createDatePredicate(Root<Product> root, CriteriaBuilder cb, String input) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(input + "T00:00:00");
            LocalDateTime endDateTime = startDateTime.plusDays(1).minusSeconds(1);
            return cb.between(root.get("createDate"), startDateTime, endDateTime);
        } catch (Exception e) {
            return cb.conjunction();
        }
    }
}
