package com.example.algamoney.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;

@Service
public class PessoaService {

	@Autowired
	private PessoaRepository pesRepository;
		
	public Pessoa Atualizar(Long codigo, Pessoa pessoa) {
		Pessoa pesSalva = buscarPeloCodigo(codigo);
		BeanUtils.copyProperties(pessoa, pesSalva, "codigo");
		
		return pesRepository.save(pesSalva);	
	}

	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		Pessoa pesAtualizar = buscarPeloCodigo(codigo);
		pesAtualizar.setAtivo(ativo);
		pesRepository.save(pesAtualizar);		
	}
	
	private Pessoa buscarPeloCodigo(Long codigo) {
		Pessoa pesSalva = this.pesRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
		return pesSalva;
	}
}
