package com.onana.decoevent.dto.response;

import com.onana.decoevent.enums.TypeNotification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String titre;
    private String message;
    private TypeNotification type;
    private Long referenceId;
    private boolean lue;
    private LocalDateTime createdAt;
}