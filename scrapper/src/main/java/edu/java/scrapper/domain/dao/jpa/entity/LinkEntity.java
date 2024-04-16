package edu.java.scrapper.domain.dao.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.net.URI;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "link")
public class LinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @URL
    @Column(name = "url", nullable = false, updatable = false, unique = true, columnDefinition = "text")
    private String url;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime createdAt;

    @Column(name = "last_check_at", columnDefinition = "timestamp with time zone")
    private OffsetDateTime lastCheckAt;

    public LinkEntity(String url) {
        this.url = url;
    }

    public LinkEntity(URI url) {
        this(url.toString());
    }

    @PrePersist
    private void beforeInit() {
        OffsetDateTime now = OffsetDateTime.now().withNano(0);

        if (createdAt == null) {
            createdAt = now;
        }
        if (lastCheckAt == null) {
            lastCheckAt = now;
        }
    }
}
