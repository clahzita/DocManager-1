/**
 * 
 */
package business.model;

import business.service.ValidationException;

/**
 * Classe representa o interessado do processo, pessoa vinculada ao processo como
 * parte interessada.
 * 
 * @author clah
 *
 */
public class Interested {
	private Long id;
	private String nome;
	private String cpf;
	private String contato;
	
	
	
	public Interested(Long id, String nome, String cpf, String contato) {
		this.id = id;
		this.nome = nome;
		this.cpf = cpf;
		this.contato = contato;
	}
	
	public Interested() {

	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) throws ValidationException {
		if(nome ==null || nome.isEmpty()) {
			throw new ValidationException("Você não preencheu o campo Nome!", "Nome", "O campo Nome não pode ser vazio.");
		}
		else if(!nome.matches("[a-zA-Z\\s]+")) {
			throw new ValidationException("Campo nome contem caracteres inválidos!", "Nome", "O campo Nome deve conter apenas letras.");
		}
		this.nome = nome;
	}


	public String getCpf() {
		return this.cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
	}


	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	public String getContato() {
		return this.contato.replaceAll("(\\d{2})(\\d{5}|\\d{4})(\\d{4})", "($1)$2-$3");
	}
	
	public void setContato(String contato) throws ValidationException {
		if(contato==null) {
			throw new ValidationException("O contato não foi digitado corretamente!", "Contato", "O contato inserido está incompleto");
		}
		else if(!contato.isEmpty() && contato.length() < 10){
			throw new ValidationException("O contato não foi digitado corretamente!", "Contato", "O contato inserido está incompleto");
		}
		this.contato = contato;
	}

}