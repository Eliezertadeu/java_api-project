package com.example.algamoney.api.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

//Examina a aplicação procurando exceções
@ControllerAdvice
public class AlgaExceptionHandler extends ResponseEntityExceptionHandler{

	@Autowired
	private MessageSource msgSource;
	
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		String msgUser = msgSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
		
		String msgAdm = Optional.ofNullable(ex.getCause()).orElse(ex).toString();
		
		List<Erro> erros = Arrays.asList(new Erro(msgUser, msgAdm));
		
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		List<Erro> erros = listDeErro(ex.getBindingResult());
		
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	
	private List<Erro> listDeErro(BindingResult bindingResult){
		List<Erro> erros = new ArrayList<>();
		
		for(FieldError fError : bindingResult.getFieldErrors()) {
			
			String msgUser = msgSource.getMessage(fError, LocaleContextHolder.getLocale()); 
			String msgAdm = fError.toString();
			
			erros.add(new Erro(msgUser, msgAdm));
		}
		return erros;
	}
	@ExceptionHandler({EmptyResultDataAccessException.class})
	public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest request) {
		
		String msgUser = msgSource.getMessage("recurso.nao-encontrado", null, LocaleContextHolder.getLocale());
		String msgAdm = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(msgUser, msgAdm));
				
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	@ExceptionHandler({DataIntegrityViolationException.class})
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request){
		
		String msgUser = msgSource.getMessage("operacao.nao-permitida", null, LocaleContextHolder.getLocale());
		String msgAdm = ExceptionUtils.getRootCauseMessage(ex);
		List<Erro> erros = Arrays.asList(new Erro(msgUser, msgAdm));
				
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	
	public static class Erro{
		private String msgUser;
		private String msgAdm;
		
		public Erro(String msgUser, String msgAdm) {
			this.msgUser = msgUser;
			this.msgAdm = msgAdm;
		}
		
		public String getMsgUser() {
			return msgUser;
		}
		public String getMsgAdm() {
			return msgAdm;
		}
	}
}
