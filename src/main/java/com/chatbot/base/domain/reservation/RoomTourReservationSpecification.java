package com.chatbot.base.domain.reservation;

import com.chatbot.base.domain.constant.RoomTourReservationStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class RoomTourReservationSpecification {

    public static Specification<RoomTourReservation> withDynamicQuery(String category, String input) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || input == null || input.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            switch (category) {
                case "name":
                    return criteriaBuilder.like(root.get("name"), "%" + input + "%");
                case "phone":
                    return criteriaBuilder.like(root.get("phone"), "%" + input + "%");
                case "status":
                    try {
                        RoomTourReservationStatus roomTourReservationStatus = RoomTourReservationStatus.fromString(input);
                        return criteriaBuilder.equal(root.get("status"), roomTourReservationStatus);
                    } catch (IllegalArgumentException e) {
                        // 존재하지 않는 enum일 경우, 절대 true가 될 수 없는 조건으로 필터링
                        return criteriaBuilder.disjunction(); // 항상 false
                    }
                case "visitDate":
                    return visitDatePredicate(root, criteriaBuilder, input);
                case "createDate":
                    return createDatePredicate(root, criteriaBuilder, input);
                default:
                    return criteriaBuilder.conjunction();
            }
        };
    }
    // LocalDate 처리 (saleDate)
    private static Predicate visitDatePredicate(Root<RoomTourReservation> root, CriteriaBuilder cb, String input) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(input + "T00:00:00");
            LocalDateTime endDateTime = startDateTime.plusDays(1).minusSeconds(1);
            return cb.between(root.get("visitDate"), startDateTime, endDateTime);
        } catch (Exception e) {
            return cb.conjunction();
        }
    }
    // LocalDateTime 처리 (createDate)
    private static Predicate createDatePredicate(Root<RoomTourReservation> root, CriteriaBuilder cb, String input) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(input + "T00:00:00");
            LocalDateTime endDateTime = startDateTime.plusDays(1).minusSeconds(1);
            return cb.between(root.get("createDate"), startDateTime, endDateTime);
        } catch (Exception e) {
            return cb.conjunction();
        }
    }
}
