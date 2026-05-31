package com.example.tccpoint.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long amount;

    private Long reservedAmount;

    @Version
    private Long version;

    public Point(Long userId, Long amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public void use(Long amount) {
        if (this.amount < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.amount -= amount;
    }

    public void reserve(Long reserveAmount) {
        long reservableAmount = this.amount - this.reservedAmount;

        if (reservableAmount < reserveAmount) {
            throw new RuntimeException("금액이 부족합니다.");
        }

        this.reservedAmount += reserveAmount;
    }
}