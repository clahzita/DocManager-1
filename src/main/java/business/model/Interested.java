/**
 * 
 */
package business.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import business.exception.ValidationException;

/**
 * Classe representa o interessado do processo, pessoa vinculada ao processo como
 * parte interessada.
 * 
 * @author clah
 *
 */
@XmlRootElement(name="interested")
public class Interested {
	private Long id;
	private String name;
	private String cpf;
	private String contact;
	
	
	
	public Interested(Long id, String nome, String cpf, String contato) {
		this.id = id;
		this.name = nome;
		this.cpf = cpf;
		this.contact = contato;
	}
	
	public Interested() {}

	/**
	 * @return the id
	 */
	@XmlTransient
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	@XmlElement
	public String getName() {
		return name;
	}


	public void setName(String name) throws ValidationException {
		if(name == null || name.isEmpty()) {
			throw new ValidationException("O campo Nome não pode ser vazio.");
		}
		else if(!name.matches("[a-zA-Z\\s]+")) {
			throw new ValidationException("O campo Nome deve conter apenas letras.");
		}
		this.name = name;
	}
	
	@XmlElement(name="cpf")
	public String getFormatedCpf() {
		return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
	}
	
	@XmlTransient
	public String getCpf() {
		return cpf;
	}


	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	@XmlElement(name="contact")
	public String getFormatedContact() {
		return contact.replaceAll("(\\d{2})(\\d{5}|\\d{4})(\\d{4})", "($1) $2-$3");
	}
	
	@XmlTransient
	public String getContact() {
		return contact;
	}
	
	public void setContact(String contact) throws ValidationException {
		if(contact == null || (!contact.isEmpty() && contact.length() < 10)){
			throw new ValidationException("O contato inserido está incompleto");
		}
		this.contact = contact;
	}
}