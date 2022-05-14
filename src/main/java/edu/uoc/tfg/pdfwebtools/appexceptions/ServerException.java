package edu.uoc.tfg.pdfwebtools.appexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR,
		reason = "Server error")
public class ServerException extends RuntimeException {

	private static final long serialVersionUID = 1L;
}
