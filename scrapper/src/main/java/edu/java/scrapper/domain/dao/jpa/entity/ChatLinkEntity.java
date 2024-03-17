package edu.java.scrapper.domain.dao.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "chat_link", uniqueConstraints = {@UniqueConstraint(columnNames = {"chat_id", "link_id"})})
@EntityListeners(AuditingEntityListener.class)
public class ChatLinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamp with time zone")
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false, updatable = false)
    ChatEntity chat;

    @ManyToOne
    @JoinColumn(name = "link_id", nullable = false, updatable = false)
    LinkEntity link;

    public ChatLinkEntity(ChatEntity chat, LinkEntity link) {
        this.chat = chat;
        this.link = link;
    }
}
