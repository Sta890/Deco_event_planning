package com.onana.decoevent.mapper;

import com.onana.decoevent.dto.response.NotificationResponse;
import com.onana.decoevent.models.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);

    List<NotificationResponse> toResponseList(List<Notification> notifications);
}