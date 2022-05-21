package edu.uoc.tfg.pdfwebtools.appconfig;

import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;

/**
 *
 */
@Configuration
public class CustomErrorViewResolver implements ErrorViewResolver {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status,
                                         Map<String, Object> model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        if(throwable instanceof MaxUploadSizeExceededException ||
                throwable instanceof SizeLimitExceededException) return handleMaxSizeException();
        String exceptionMessage = getExceptionMessage(throwable, statusCode);

        ModelAndView modelv = new ModelAndView("error");
        modelv.addObject("status", request.getAttribute("javax.servlet.error.status_code"));
        return modelv;
    }


    private String getExceptionMessage(Throwable throwable, Integer statusCode) {
        if (throwable != null) {
            return throwable.getMessage();
        }

        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        return httpStatus.getReasonPhrase();
    }


    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleValidationFailure(ConstraintViolationException ex) {

        StringBuilder messages = new StringBuilder();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            messages.append(violation.getMessage()).append("\n");
        }

        return messages.toString();
    }

    public ModelAndView handleMaxSizeException() {

        ModelAndView modelAndView = new ModelAndView("repository");
        modelAndView.getModelMap().addAttribute("message_error", "File too large! The maximum size allowed is: " + maxFileSize);
        return new ModelAndView("redirect:/", modelAndView.getModelMap());
    }
}


