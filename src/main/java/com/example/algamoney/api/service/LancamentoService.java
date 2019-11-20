package com.example.algamoney.api.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.LancamentoRepository;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.service.exception.PessoaInexistenteOuInativaException;

@Service
public class LancamentoService {

	@Autowired
	private PessoaRepository pesRepository;
	
	@Autowired
	private LancamentoRepository lanRep;
	
	public Lancamento salvar(Lancamento lan) {
		
		Optional<Pessoa> pes = pesRepository.findById(lan.getPessoa().getCodigo());
				
		if(pes == null || !pes.isPresent() || pes.get().isInativa()) {
			throw new PessoaInexistenteOuInativaException();
		}
		
		return lanRep.save(lan);
	}
	
	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		Lancamento lancamentoAtt = buscarLancamentoExistente(codigo);
		if(!lancamento.getPessoa().equals(lancamentoAtt.getPessoa())) {
			validarPessoa(lancamento);
		}
		BeanUtils.copyProperties(lancamento, lancamentoAtt, "codigo");
		
		return lanRep.save(lancamentoAtt);
	}

	private void validarPessoa(Lancamento lancamento) {
		Optional<Pessoa> pessoa = null;

		if(lancamento.getPessoa().getCodigo()!=null) {
			pessoa = this.pesRepository.findById(lancamento.getPessoa().getCodigo());
		}
		
		if(pessoa == null || pessoa.get().isInativa()) {
			throw new PessoaInexistenteOuInativaException();
		}
	}

	private Lancamento buscarLancamentoExistente(Long codigo) {
		Lancamento lanSalva = this.lanRep.findById(codigo).orElseThrow(() -> new IllegalArgumentException());
		return lanSalva;
	}
	
}
