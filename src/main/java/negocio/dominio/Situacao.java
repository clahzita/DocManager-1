package negocio.dominio;

import java.util.ArrayList;
import java.util.List;

public enum Situacao {
	
	NULL("- Inválido -"),
	ANALISE("Análise"),
	CONVOCAR("A convocar"),
	AGUARDANDODOCUMENTO("Aguardando Documento(s)"),
	AGENDADO("Agendado"),
	CONCLUIDO("Concluido"),
	CONVOCADO("Convocado"),
	SOLICITARDOCUMENTO("Solicitar Documento(s)"),
	SEMEXITO1("Contato Sem Êxito - 1ª vez"),
	SEMEXITO2("Contato Sem Êxito - 2ª vez"),
	SEMEXITO3("Contato Sem Êxito - 3ª vez"),
	ENCAMINHADOCOVEPS("Encaminhado a Coordenação COVEPS"),
	ENCAMINHADOEQUIPEMULTI("Encaminhado p/ Eq. Multiprofissional"),
	AGUARDANDOPERITO("Aguardando Perito Finalizar"),
	AGUARDANDOEXTERNA("Aguardando Perícia Externa"),
	AGENDADAEXTERNA("Agendada Perícia Externa"),
	PROBLEMASIAPE("Aguardando Resolver Problema SIAPE"),
	PRONTODESPACHAR("Pronto para Despachar"),
	INTERESSADOIMPEDIDO("Interessado Impedido de Ser Periciado");
	
	private String status;
	
	Situacao(String status){
		this.status = status;
	}
	
	public static List<String> getSituacoes() {
		List<String> listaSituacoes = new ArrayList<String>();
		for(Situacao situacao : Situacao.values()) {
			listaSituacoes.add(situacao.status);
		}
		listaSituacoes.remove(0);
		return listaSituacoes;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public static Situacao getSituacaoPorId(int id) throws ValidationException{
		if(id == 0) {
			throw new ValidationException("Você não selecionou a Situação!", "Situacao", "O campo Situação é obrigatório.");
		}
		else {
			return Situacao.values()[id];
		}
	}
}
