package com.example.kbasic.service.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;

public class PointsEvent {
    private Integer userId;
    private Integer orderId;
    private Integer value;
    private Integer points;
    private String gift;
    private Instant eventTime = Instant.now();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public PointsEvent() {}

    public PointsEvent(Integer userId, Integer orderId, Integer value, Integer points, String gift) {
        this.userId = userId;
        this.orderId = orderId;
        this.value = value;
        this.points = points;
        this.gift = gift;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public String getGift() { return gift; }
    public void setGift(String gift) { this.gift = gift; }

    public Instant getEventTime() { return eventTime; }
    public void setEventTime(Instant eventTime) { this.eventTime = eventTime; }

    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // fallback simple json
            return String.format("{\"userId\":%d,\"orderId\":%d,\"value\":%d,\"points\":%d,\"gift\":\"%s\",\"eventTime\":\"%s\"}",
                    userId, orderId, value, points, gift, eventTime.toString());
        }
    }
}
