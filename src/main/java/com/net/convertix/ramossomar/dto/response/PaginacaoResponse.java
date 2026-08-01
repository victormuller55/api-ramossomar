package com.net.convertix.ramossomar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

@Schema(description = "Resposta padrão de listagem paginada")
public class PaginacaoResponse<T> {

	@Schema(description = "Itens da página atual")
	private List<T> itens;

	@Schema(description = "Quantidade de itens na página atual", example = "20")
	private int num_itens;

	@Schema(description = "Quantidade total de itens no filtro", example = "100")
	private long max_itens;

	@Schema(description = "Número da página atual (base 1)", example = "1")
	private int num_pagina;

	@Schema(description = "Quantidade total de páginas", example = "5")
	private int max_paginas;

	public PaginacaoResponse() {
	}

	public static <T> PaginacaoResponse<T> de(List<T> itens, Page<?> page) {
		PaginacaoResponse<T> response = new PaginacaoResponse<>();
		response.setItens(itens);
		response.setNum_itens(itens.size());
		response.setMax_itens(page.getTotalElements());
		response.setNum_pagina(page.getNumber() + 1);
		response.setMax_paginas(page.getTotalPages());
		return response;
	}

	public List<T> getItens() {
		return itens;
	}

	public void setItens(List<T> itens) {
		this.itens = itens;
	}

	public int getNum_itens() {
		return num_itens;
	}

	public void setNum_itens(int num_itens) {
		this.num_itens = num_itens;
	}

	public long getMax_itens() {
		return max_itens;
	}

	public void setMax_itens(long max_itens) {
		this.max_itens = max_itens;
	}

	public int getNum_pagina() {
		return num_pagina;
	}

	public void setNum_pagina(int num_pagina) {
		this.num_pagina = num_pagina;
	}

	public int getMax_paginas() {
		return max_paginas;
	}

	public void setMax_paginas(int max_paginas) {
		this.max_paginas = max_paginas;
	}
}
