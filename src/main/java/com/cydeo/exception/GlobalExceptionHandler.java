package com.cydeo.exception;

import com.cydeo.annotation.TicketingExceptionMessage;
import com.cydeo.dto.DefaultExceptionMessageDto;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TicketingException.class)
    public String serviceException(TicketingException exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        return "error";
    }

    // whenever one of these exception throws, run this method
    // if there is our custom annotation at any method, it'll build the response including that message,
    // otherwise it'll show "Something went wrong!" message
    @ExceptionHandler({Throwable.class})
    public String genericException(Throwable exception, HandlerMethod handlerMethod, Model model) {
        String message = "Something went wrong!";
        Optional<DefaultExceptionMessageDto> defaultMessage = getMessageFromAnnotation(handlerMethod.getMethod());
        if (defaultMessage.isPresent()) {
            message = defaultMessage.get().getMessage();
        } else if (exception.getMessage() != null) {
            message = exception.getMessage();
        }
        model.addAttribute("message", message);
        return "error";
    }

    private Optional<DefaultExceptionMessageDto> getMessageFromAnnotation(Method method) {
        TicketingExceptionMessage defaultExceptionMessage = method
                .getAnnotation(TicketingExceptionMessage.class);
        if (defaultExceptionMessage != null) {
            DefaultExceptionMessageDto defaultExceptionMessageDto = DefaultExceptionMessageDto
                    .builder()
                    .message(defaultExceptionMessage.defaultMessage())
                    .build();
            return Optional.of(defaultExceptionMessageDto);
        }
        return Optional.empty();
    }
}