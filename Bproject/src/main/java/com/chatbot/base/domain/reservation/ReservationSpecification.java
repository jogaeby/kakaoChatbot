package com.chatbot.base.domain.reservation;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ReservationSpecification {

    public static Specification<Reservation> withDynamicQuery(String category, String input) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || input == null || input.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            switch (category) {
                case "studentName":
                    return criteriaBuilder.like(root.get("studentName"), "%" + input + "%");
                case "studentPhone":
                    return criteriaBuilder.like(root.get("studentPhone"), "%" + input + "%");
                case "teacherName":
                    return criteriaBuilder.like(root.get("teacherName"), "%" + input + "%");
                case "teacherPhone":
                    return criteriaBuilder.like(root.get("teacherPhone"), "%" + input + "%");
                case "reservationDate":
                    return saleDatePredicate(root, criteriaBuilder, input);
                case "createDate":
                    return createDatePredicate(root, criteriaBuilder, input);
                default:
                    return criteriaBuilder.conjunction();
            }
        };
    }
    // LocalDate 처리 (saleDate)
    private static Predicate saleDatePredicate(Root<Reservation> root, CriteriaBuilder cb, String input) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(input + "T00:00:00");
            LocalDateTime endDateTime = startDateTime.plusDays(1).minusSeconds(1);
            return cb.between(root.get("reservationDate"), startDateTime, endDateTime);
        } catch (Exception e) {
            return cb.conjunction();
        }
    }
    // LocalDateTime 처리 (createDate)
    private static Predicate createDatePredicate(Root<Reservation> root, CriteriaBuilder cb, String input) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(input + "T00:00:00");
            LocalDateTime endDateTime = startDateTime.plusDays(1).minusSeconds(1);
            return cb.between(root.get("createDate"), startDateTime, endDateTime);
        } catch (Exception e) {
            return cb.conjunction();
        }
    }
}
