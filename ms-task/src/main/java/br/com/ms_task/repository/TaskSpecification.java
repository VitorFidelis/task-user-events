package br.com.ms_task.repository;

import br.com.ms_task.entity.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Utility class for building dynamic JPA Specifications for Task queries.
 */
public class TaskSpecification {

    private TaskSpecification() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Creates a Specification for filtering Task entities by status.
     * @param status the status to filter by (optional)
     * @return Specification<Task>
     */
    public static Specification<Task> create(final String status) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (StringUtils.hasText(status)) {
                predicates = criteriaBuilder.and(
                    predicates,
                    criteriaBuilder.equal(root.get("status"), status.toUpperCase())
                );
            }

            return predicates;
        };
    }
}
