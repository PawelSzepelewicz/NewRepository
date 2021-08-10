package com.example.probation.event;

import com.example.probation.core.entity.User;
import lombok.*;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    final private User user;

    public OnRegistrationCompleteEvent( User user) {
        super(user);
        this.user = user;
    }
}
