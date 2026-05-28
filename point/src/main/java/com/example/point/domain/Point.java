package com.example.point.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Point {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long amount;

    public Point(Long userId, Long amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public void use(Long amount) {
        if (this.amount < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        this.amount -= amount;
    }
}
