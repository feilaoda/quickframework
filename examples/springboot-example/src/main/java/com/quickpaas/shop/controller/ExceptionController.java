package com.quickpaas.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickpaas.framework.domain.Result;
import com.quickpaas.framework.exception.QuickException;
import com.quickpaas.framework.exception.WebException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionController {
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @ExceptionHandler({QuickException.class, WebException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleError(HttpServletRequest req, HttpServletResponse response, QuickException ex) {
        logger.error(ex.getMessage(), ex);
        try {
            Result result = Result.error(ex.getCode(), ex.getMessage());
            ObjectMapper mapper = new ObjectMapper();
            String body = mapper.writeValueAsString(result);
            if (ex.getCode() > 1000) {
                response.setStatus(500);
            } else {
                response.setStatus(ex.getCode());
            }
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(body);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleError(HttpServletRequest req, HttpServletResponse response, Exception ex) {
        logger.error(ex.getMessage(), ex);
        try {
            Result result = Result.error(500, ex.getMessage());
            ObjectMapper mapper = new ObjectMapper();
            String body = mapper.writeValueAsString(result);
            response.setStatus(500);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(body);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleCommonError(HttpServletRequest req, HttpServletResponse response, MethodArgumentNotValidException ex) {
        logger.error(ex.getMessage(), ex);
        try {
            BindingResult res = ex.getBindingResult();
            List<String> errorMessages = res.getAllErrors()
                    .stream()
//                    .map(objectError -> messageSource.getMessage(objectError, locale))
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.toList());

            Result result = Result.error(500, String.join(",", errorMessages));
            ObjectMapper mapper = new ObjectMapper();
            String body = mapper.writeValueAsString(result);
            response.setStatus(500);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(body);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
