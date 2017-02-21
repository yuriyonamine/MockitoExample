package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.mockito.InOrder;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;

public class EncerradorDeLeilaoTest {

	@Test
	public void deveEncerrarLeiloesQueComecaramUmaSemanaAntes() {
		Calendar data = Calendar.getInstance();
		data.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("Video Game").naData(data)
				.constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Computador").naData(data)
				.constroi();

		List<Leilao> leiloes = Arrays.asList(leilao1, leilao2);

		RepositorioDeLeiloes dao = mock(RepositorioDeLeiloes.class);
		when(dao.correntes()).thenReturn(leiloes);

		EnviadorDeEmail enviadorDeEmail = mock(EnviadorDeEmail.class);

		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(dao,
				enviadorDeEmail);
		encerradorDeLeilao.encerra();

		assertEquals(2, encerradorDeLeilao.getTotalEncerrados());
		assertTrue(leilao1.isEncerrado());
		assertTrue(leilao2.isEncerrado());
		verify(dao, times(1)).atualiza(leilao1);
		verify(dao, times(1)).atualiza(leilao2);
	}

	@Test
	public void naoDeveFinalizarLeiloesQueComecaramNoDiaAnterior() {
		Calendar data = Calendar.getInstance();
		data.add(Calendar.DAY_OF_MONTH, -1);

		Leilao leilao1 = new CriadorDeLeilao().para("Video Game").naData(data)
				.constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Computador").naData(data)
				.constroi();
		List<Leilao> leiloes = Arrays.asList(leilao1, leilao2);

		RepositorioDeLeiloes dao = mock(RepositorioDeLeiloes.class);
		when(dao.correntes()).thenReturn(leiloes);

		EnviadorDeEmail enviadorDeEmail = mock(EnviadorDeEmail.class);

		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(dao,
				enviadorDeEmail);
		encerradorDeLeilao.encerra();

		assertEquals(0, encerradorDeLeilao.getTotalEncerrados());
		assertFalse(leilao1.isEncerrado());
		assertFalse(leilao2.isEncerrado());
	}

	@Test
	public void naoDeveFazerNadaSemLeiloes() {
		RepositorioDeLeiloes dao = mock(RepositorioDeLeiloes.class);
		when(dao.correntes()).thenReturn(new ArrayList<Leilao>());

		EnviadorDeEmail enviadorDeEmail = mock(EnviadorDeEmail.class);

		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(dao,
				enviadorDeEmail);
		encerradorDeLeilao.encerra();

		assertEquals(0, encerradorDeLeilao.getTotalEncerrados());
	}

	@Test
	public void deveAtualizarLeilao() {
		Calendar data = Calendar.getInstance();
		data.add(Calendar.DAY_OF_MONTH, -9);

		Leilao leilao1 = new CriadorDeLeilao().para("Video Game").naData(data)
				.constroi();

		RepositorioDeLeiloes dao = mock(RepositorioDeLeiloes.class);
		when(dao.correntes()).thenReturn(Arrays.asList(leilao1));

		EnviadorDeEmail enviadorDeEmail = mock(EnviadorDeEmail.class);

		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(dao,
				enviadorDeEmail);
		encerradorDeLeilao.encerra();

		verify(dao, times(1)).atualiza(leilao1);
	}

	@Test
	public void deveEnviarEmailAposPersistirLeilaoEncerrado() {
		Calendar data = Calendar.getInstance();
		data.add(Calendar.DAY_OF_MONTH, -9);

		Leilao leilao1 = new CriadorDeLeilao().para("Video Game").naData(data)
				.constroi();

		RepositorioDeLeiloes dao = mock(RepositorioDeLeiloes.class);
		when(dao.correntes()).thenReturn(Arrays.asList(leilao1));

		EnviadorDeEmail enviadorDeEmail = mock(EnviadorDeEmail.class);

		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(dao,
				enviadorDeEmail);
		encerradorDeLeilao.encerra();

		InOrder inOrder = inOrder(dao, enviadorDeEmail);
		inOrder.verify(dao, times(1)).atualiza(leilao1);
		inOrder.verify(enviadorDeEmail, times(1)).envia(leilao1);
	}
	
	@Test
	public void deveContinuarExecutandoQuandoLancarExcecao(){
		Calendar data = Calendar.getInstance();
		data.add(Calendar.DAY_OF_MONTH, -10);

		Leilao leilao1 = new CriadorDeLeilao().para("Video Game").naData(data)
				.constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Computador").naData(data)
				.constroi();
		
		List<Leilao> leiloes = Arrays.asList(leilao1, leilao2);

		RepositorioDeLeiloes dao = mock(RepositorioDeLeiloes.class);
		when(dao.correntes()).thenReturn(leiloes);

		EnviadorDeEmail enviadorDeEmail = mock(EnviadorDeEmail.class);
		doThrow(new RuntimeException()).when(dao).atualiza(leilao1);
		
		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(dao,
				enviadorDeEmail);
		encerradorDeLeilao.encerra();
		
		verify(dao).atualiza(leilao2);
		verify(enviadorDeEmail).envia(leilao2);
	}
	
	@Test
	public void naoDeveInvocarEnviadorDeEmail(){
		Calendar data = Calendar.getInstance();
		data.add(Calendar.DAY_OF_MONTH, -10);

		Leilao leilao1 = new CriadorDeLeilao().para("Video Game").naData(data)
				.constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Computador").naData(data)
				.constroi();
		
		List<Leilao> leiloes = Arrays.asList(leilao1, leilao2);

		RepositorioDeLeiloes dao = mock(RepositorioDeLeiloes.class);
		when(dao.correntes()).thenReturn(leiloes);

		EnviadorDeEmail enviadorDeEmail = mock(EnviadorDeEmail.class);
		doThrow(new RuntimeException()).when(dao).atualiza(any(Leilao.class));
		
		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(dao,
				enviadorDeEmail);
		encerradorDeLeilao.encerra();
		
		verify(enviadorDeEmail, never()).envia(leilao1);
		verify(enviadorDeEmail, never()).envia(leilao2);
	}
}
