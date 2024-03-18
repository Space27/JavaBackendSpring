package edu.java.scrapper.domain.dao.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "chat")
public class ChatEntity {

    @Id
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime createdAt;

    public ChatEntity(Long id) {
        this.id = id;
    }

    @PrePersist
    private void beforeInit() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now().withNano(0);
        }
    }
}
