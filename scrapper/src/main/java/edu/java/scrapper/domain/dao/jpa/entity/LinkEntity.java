package edu.java.scrapper.domain.dao.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.net.URI;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "link")
@EntityListeners(AuditingEntityListener.class)
public class LinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @URL
    @Column(name = "url", nullable = false, updatable = false, unique = true, columnDefinition = "text")
    String url;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamp with time zone")
    private Instant createdAt;

    @CreatedDate
    @Column(name = "last_check_at", columnDefinition = "timestamp with time zone")
    private Instant lastCheckAt;

    public LinkEntity(String url) {
        this.url = url;
    }

    public LinkEntity(URI url) {
        this.url = url.toString();
    }
}
