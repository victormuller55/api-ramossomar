package com.net.convertix.ramossomar.util;

import com.net.convertix.ramossomar.exception.NegocioException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public final class PaginacaoUtil {

	public static final int ITENS_POR_PAGINA = 20;

	private PaginacaoUtil() {
	}

	public static Pageable criar(Integer pagina) {
		int numPagina = pagina == null ? 1 : pagina;
		if (numPagina < 1) {
			throw new NegocioException("O parâmetro pagina deve ser maior ou igual a 1");
		}
		return PageRequest.of(numPagina - 1, ITENS_POR_PAGINA);
	}
}
