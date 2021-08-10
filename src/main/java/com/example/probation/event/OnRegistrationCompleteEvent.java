package com.example.probation.event;

import com.example.probation.core.entity.User;
import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    final private String appUrl;
    final private Locale locale;
    final private User user;

    public OnRegistrationCompleteEvent(String appUrl, Locale locale, User user) {
        super(user);

        this.user = user;
        this.appUrl = appUrl;
        this.locale = locale;
    }
}
