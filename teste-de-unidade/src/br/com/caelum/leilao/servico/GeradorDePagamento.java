package br.com.caelum.leilao.servico;

import java.util.Calendar;
import java.util.List;

import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamento;

public class GeradorDePagamento {
	private Avaliador avaliador;
	private RepositorioDeLeiloes repositorioDeLeiloes;
	private RepositorioDePagamento repositorioDePagamento;
	
	public GeradorDePagamento(Avaliador avaliador,
			RepositorioDeLeiloes repositorioDeLeiloes,
			RepositorioDePagamento repositorioDePagamento) {
		super();
		this.avaliador = avaliador;
		this.repositorioDeLeiloes = repositorioDeLeiloes;
		this.repositorioDePagamento = repositorioDePagamento;
	}


	public void gerar(){
		List<Leilao> correntes = repositorioDeLeiloes.correntes();
		
		for (Leilao leilao : correntes) {
			avaliador.avalia(leilao);
			
			Pagamento pagamento = new Pagamento(avaliador.getMaiorLance(), Calendar.getInstance());
			repositorioDePagamento.salvar(pagamento);
		}
	}

}
