package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamento;

public class GeradorDePagamentoTest {

	@Test
	public void deveGerarPagamentoComMaiorLance() {
		Calendar data = Calendar.getInstance();
		data.set(1999, 1, 20);

		RepositorioDeLeiloes repositorioDeLeiloes = mock(RepositorioDeLeiloes.class);
		RepositorioDePagamento repositorioDePagamento = mock(RepositorioDePagamento.class);

		Leilao leilao1 = new CriadorDeLeilao().para("Playstation").naData(data)
				.lance(new Usuario("yuri"), 100.0)
				.lance(new Usuario("carlos"), 500.0)
				.lance(new Usuario("dougras"), 1000.0).constroi();

		when(repositorioDeLeiloes.correntes()).thenReturn(
				Arrays.asList(leilao1));

		GeradorDePagamento geradorDePagamento = new GeradorDePagamento(
				new Avaliador(), repositorioDeLeiloes, repositorioDePagamento);
		geradorDePagamento.gerar();

		ArgumentCaptor<Pagamento> argumentCaptor = ArgumentCaptor
				.forClass(Pagamento.class);
		verify(repositorioDePagamento).salvar(argumentCaptor.capture());
		Pagamento pagamento = argumentCaptor.getValue();

		assertEquals(1000.0, pagamento.getValor(), 0.0001);
	}
}
