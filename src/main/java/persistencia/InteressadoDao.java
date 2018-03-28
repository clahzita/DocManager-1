package persistencia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import negocio.GenericoDao;
import negocio.dominio.Interessado;
/**
 * 
 * @author Allan
 *
 */
public class InteressadoDao implements GenericoDao<Interessado> {
	
	private static final HashMap<String, Interessado> banco = new HashMap<String, Interessado>();

	public void salvar(Interessado bean) {
		banco.put(bean.getCpf(), bean);
		
	}

	public void atualizar(Interessado bean) {
		// TODO Auto-generated method stub
	}

	public void deletar(Interessado bean) {
		banco.remove(bean.getCpf());
	}

	public Interessado getById(String cpf) {
		return banco.get(cpf);
	}

	public boolean contem(Interessado bean) {
		return banco.containsKey(bean.getCpf());
	}

	public List<Interessado> getAll() {
		return new ArrayList<Interessado>(banco.values());
	}

}