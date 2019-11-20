package com.example.algamoney.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.example.algamoney.api.model.Categoria_;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Lancamento_;
import com.example.algamoney.api.model.Pessoa_;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import com.example.algamoney.api.repository.projection.ResumoLancamento;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery{

	@PersistenceContext
	private EntityManager manager;
	
	@Override
	public Page<Lancamento> filtrar(LancamentoFilter lanFilter, Pageable pageable)
	{
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		Predicate[] predicates = criarRestricoes(lanFilter, builder, root);
		
		criteria.where(predicates);
		
		TypedQuery<Lancamento> query = manager.createQuery(criteria);
		
		adicionarRestricoesPaginacao(query, pageable);
		
		
		return new PageImpl<>(query.getResultList(), pageable, total(lanFilter));
	}
	
	@Override
	public Page<ResumoLancamento> resumir(LancamentoFilter lanFilter, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery(ResumoLancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
				
		criteria.select(builder.construct(ResumoLancamento.class, 
				root.get(Lancamento_.codigo),
				root.get(Lancamento_.descricao),
				root.get(Lancamento_.dataVencimento),
				root.get(Lancamento_.dataPagamento),
				root.get(Lancamento_.valor),
				root.get(Lancamento_.tipo),
				root.get(Lancamento_.categoria).get(Categoria_.nome),
				root.get(Lancamento_.pessoa).get(Pessoa_.nome))
			);
		Predicate[] predicates = criarRestricoes(lanFilter, builder, root);
		
		criteria.where(predicates);
		
		TypedQuery<ResumoLancamento> query = manager.createQuery(criteria);
		
		adicionarRestricoesPaginacao(query, pageable);
		
		
		return new PageImpl<>(query.getResultList(), pageable, total(lanFilter));	
	}
	

	private Long total(LancamentoFilter lanFilter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		Predicate[] predicates = criarRestricoes(lanFilter, builder, root);
		
		criteria.where(predicates);
		criteria.select(builder.count(root));
		
		return manager.createQuery(criteria).getSingleResult();
	}

	private void adicionarRestricoesPaginacao(TypedQuery<?> query, Pageable pageable) {
		int pagAtual = pageable.getPageNumber();
		int totalRegistro = pageable.getPageSize();
		int primeiroRegistro = pagAtual * totalRegistro;
		
		query.setFirstResult(primeiroRegistro);
		query.setMaxResults(totalRegistro);
		
	}

	private Predicate[] criarRestricoes(LancamentoFilter lanFilter, CriteriaBuilder builder, Root<Lancamento> root) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		
		if(!StringUtils.isEmpty(lanFilter.getDescricao())) {
			predicates.add(builder.like(
					builder.lower(root.get(Lancamento_.DESCRICAO)),
					"%" + lanFilter.getDescricao().toLowerCase() + "%"));
		}
		
		if (lanFilter.getDataVencimentoDe() != null) {
			predicates.add(builder.greaterThanOrEqualTo(
					root.get(Lancamento_.dataVencimento), lanFilter.getDataVencimentoDe()
					));
		}
		
		if(lanFilter.getDataVencimentoAte() != null) {
			predicates.add(builder.lessThanOrEqualTo(
					root.get(Lancamento_.dataVencimento), lanFilter.getDataVencimentoAte()
					));
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}


}
