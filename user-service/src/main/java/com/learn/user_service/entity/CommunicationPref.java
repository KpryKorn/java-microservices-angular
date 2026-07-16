package com.learn.user_service.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.java.com.learn.user_service.entity.UserProfile;

@Entity
@Table(name = "communication_pref")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunicationPref {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserProfile userProfile;

    @Column(nullable = false)
    private boolean newsletter;

    @Column(nullable = false)
    private boolean sms;

    @Column(nullable = false)
    private boolean push;
}
