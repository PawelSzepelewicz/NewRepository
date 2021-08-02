package com.example.probation.event;

import com.example.probation.model.User;
import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;

    public OnRegistrationCompleteEvent(String appUrl, Locale locale, User user) {
        super(user);

        this.user = user;
        this.appUrl = appUrl;
        this.locale = locale;
    }
}
