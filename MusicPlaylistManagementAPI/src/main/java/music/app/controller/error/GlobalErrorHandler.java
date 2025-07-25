package music.app.controller.error;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {

	private enum LogStatus {
		STACK_TRACE, MESSAGE_ONLY
	}

	@Data
	private class ExceptionMessage {
		private String message;
		private String statusReason;
		private int statusCode;
		private String timestamp;
		private String uri;
	}
	
	@ExceptionHandler(DuplicateUserException.class)
	@ResponseStatus(code = HttpStatus.CONFLICT)
	public ExceptionMessage handleDuplicateUserException(
			DuplicateUserException ex, WebRequest webRequest) {
		return buildExceptionMessage(
				ex, HttpStatus.CONFLICT, webRequest, LogStatus.MESSAGE_ONLY);
	}
	
	@ExceptionHandler(DuplicateSongException.class)
	@ResponseStatus(code = HttpStatus.CONFLICT)
	public ExceptionMessage handleDuplicateSongException(
			DuplicateSongException ex, WebRequest webRequest) {
		return buildExceptionMessage(
				ex, HttpStatus.CONFLICT, webRequest, LogStatus.MESSAGE_ONLY);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ExceptionMessage handleMethodArgumentNotValidException(
			MethodArgumentNotValidException ex, WebRequest webRequest) {
		return buildExceptionMessage(
				ex, HttpStatus.BAD_REQUEST, webRequest, LogStatus.MESSAGE_ONLY);
	}

	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ExceptionMessage handleIllegalStateException(
			IllegalStateException ex, WebRequest webRequest) {
		return buildExceptionMessage(
				ex, HttpStatus.BAD_REQUEST, webRequest, LogStatus.MESSAGE_ONLY);
	}

	@ExceptionHandler(UnsupportedOperationException.class)
	@ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
	public ExceptionMessage handleUnsupportedOperationException(
			UnsupportedOperationException ex, WebRequest webRequest) {
		return buildExceptionMessage(
				ex, HttpStatus.METHOD_NOT_ALLOWED, webRequest, LogStatus.MESSAGE_ONLY);
	}

	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public ExceptionMessage handleNoSuchElementException(
			NoSuchElementException ex, WebRequest webRequest) {
		return buildExceptionMessage(
				ex, HttpStatus.NOT_FOUND, webRequest, LogStatus.MESSAGE_ONLY);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(code = HttpStatus.CONFLICT)
	public ExceptionMessage handleDataIntegrityViolationException(
			DataIntegrityViolationException ex, WebRequest webRequest) {
		return buildExceptionMessage(
				ex, HttpStatus.CONFLICT, webRequest, LogStatus.MESSAGE_ONLY);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionMessage handleException(
			Exception ex, WebRequest webRequest) {
		return buildExceptionMessage(
				ex, HttpStatus.INTERNAL_SERVER_ERROR, webRequest, LogStatus.STACK_TRACE);
	}

	private ExceptionMessage buildExceptionMessage(
			Exception ex,
			HttpStatus status,
			WebRequest webRequest,
			LogStatus logStatus) {
			String message = ex.toString();
			String statusReason = status.getReasonPhrase();
			int statusCode = status.value();
			String uri = null;
			String timestamp =
					ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
			if(webRequest instanceof ServletWebRequest swr) {
				uri = swr.getRequest().getRequestURI();
			}
			if(logStatus == LogStatus.MESSAGE_ONLY) {
				log.error("Exception: {}", ex.toString());
			} else {
				log.error("Exception; ", ex);
			}
			ExceptionMessage excMsg = new ExceptionMessage();
			excMsg.setMessage(message);
			excMsg.setStatusCode(statusCode);
			excMsg.setStatusReason(statusReason);
			excMsg.setTimestamp(timestamp);
			excMsg.setUri(uri);
			return excMsg;
	}
}
