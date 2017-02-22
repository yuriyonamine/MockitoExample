package br.com.caelum.leilao.servico;

import java.util.Calendar;
import java.util.List;

import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Relogio;
import br.com.caelum.leilao.dominio.RelogioDoSistema;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamento;

public class GeradorDePagamento {
	private Avaliador avaliador;
	private RepositorioDeLeiloes repositorioDeLeiloes;
	private RepositorioDePagamento repositorioDePagamento;
	private Relogio relogio;

	public GeradorDePagamento(Avaliador avaliador,
			RepositorioDeLeiloes repositorioDeLeiloes,
			RepositorioDePagamento repositorioDePagamento) {
		this(avaliador, repositorioDeLeiloes, repositorioDePagamento,
				new RelogioDoSistema());
	}

	public GeradorDePagamento(Avaliador avaliador,
			RepositorioDeLeiloes repositorioDeLeiloes,
			RepositorioDePagamento repositorioDePagamento, Relogio relogio) {
		this.avaliador = avaliador;
		this.repositorioDeLeiloes = repositorioDeLeiloes;
		this.repositorioDePagamento = repositorioDePagamento;
		this.relogio = relogio;
	}

	public void gerar() {
		List<Leilao> correntes = repositorioDeLeiloes.correntes();

		for (Leilao leilao : correntes) {
			avaliador.avalia(leilao);

			Pagamento pagamento = new Pagamento(avaliador.getMaiorLance(),
					primeiroDiaUtil());
			repositorioDePagamento.salvar(pagamento);
		}
	}

	private Calendar primeiroDiaUtil() {
		Calendar calendar = relogio.hoje();
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		if (dayOfWeek == Calendar.SATURDAY) {
			calendar.add(Calendar.DAY_OF_MONTH, 2);
		} else if (dayOfWeek == Calendar.SUNDAY) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		return calendar;
	}

}
