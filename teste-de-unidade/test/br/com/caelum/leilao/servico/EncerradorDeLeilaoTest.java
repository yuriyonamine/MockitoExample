package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeilao;

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
		RepositorioDeLeilao daoFalso = mock(RepositorioDeLeilao.class);
		
		when(daoFalso.correntes()).thenReturn(leiloes);
		
		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(daoFalso);
		encerradorDeLeilao.encerra();

		assertEquals(2, encerradorDeLeilao.getTotalEncerrados());
		assertTrue(leilao1.isEncerrado());
		assertTrue(leilao2.isEncerrado());
	}
	
	@Test
	public void naoDeveFinalizarLeiloesQueComecaramNoDiaAnterior(){
		Calendar data = Calendar.getInstance();
		data.add(Calendar.DAY_OF_MONTH, -1);
		
		Leilao leilao1 = new CriadorDeLeilao().para("Video Game").naData(data)
				.constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Computador").naData(data)
				.constroi();
		List<Leilao> leiloes = Arrays.asList(leilao1,leilao2);
		
		RepositorioDeLeilao dao= mock(RepositorioDeLeilao.class);
		when(dao.correntes()).thenReturn(leiloes);
		
		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(dao);
		encerradorDeLeilao.encerra();
		
		assertEquals(0, encerradorDeLeilao.getTotalEncerrados());
		assertFalse(leilao1.isEncerrado());
		assertFalse(leilao2.isEncerrado());
	}

@Test
public void naoDeveFazerNadaSemLeiloes(){
	RepositorioDeLeilao dao = mock(RepositorioDeLeilao.class);
	when(dao.correntes()).thenReturn(new ArrayList<Leilao>());
	
	EncerradorDeLeilao encerradorDeLeilao= new EncerradorDeLeilao(dao);
	encerradorDeLeilao.encerra();
	
	assertEquals(0, encerradorDeLeilao.getTotalEncerrados());
}
}
