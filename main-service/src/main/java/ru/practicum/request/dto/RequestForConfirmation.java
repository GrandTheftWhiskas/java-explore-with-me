package ru.practicum.request.dto;

import lombok.Data;

import java.util.List;

@Data
public class RequestForConfirmation {
    private String status;
    private List<Long> requestIds;
}
