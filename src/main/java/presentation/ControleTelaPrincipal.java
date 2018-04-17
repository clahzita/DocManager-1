package presentation;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import business.model.Process;
import business.service.ConcreteProcessService;
import business.service.Observer;
import business.service.ProcessService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import presentation.utils.widget.MaskedTextField;

/**
 * @author hugotho
 * 
 */
public class ControleTelaPrincipal implements Initializable, Observer {

	private static final URL ARQUIVO_FXML_TELA_EDICAO = ControleTelaPrincipal.class.getResource("/visions/tela_editar_processo.fxml");
	private static final URL ARQUIVO_FXML_DIALOG_PASSWORD = ControleTelaPrincipal.class.getResource("/visions/dialog_adm_password.fxml");
	private static final URL ARQUIVO_FXML_TELA_BUSCA = ControleTelaPrincipal.class.getResource("/visions/tela_buscar_processos.fxml");
	private static final String CRIAR_PROCESSO = "Novo Processo / Ofício";
	private static final String EDITAR_PROCESSO = "Editar Processo";
	private static final String BUSCAR_PROCESSO = "Buscar Processos / Ofícios";
	private static final String DIALOG_ADM_PASS_TITLE = "Autorização";
	private static final int NUM_OFICIO_OFFSET = 8;
	private static final String MASCARA_NUM_OFICIO = "####/####";
	private static final String MASCARA_NUM_PROCESSO = "#####.######/####-##";

	private ProcessService processService;
	private Process processoSelecionado;

	@FXML
	private Pane painel;

	@FXML
	private Button btnNovo;

	@FXML
	private Button btnVerEditar;

	@FXML
	private Button btnApagar;

	@FXML
	private TableView<Process> tabelaProcessosOficios;

	@FXML
	private TableColumn<Process, String> tabColunaTipo;

	@FXML
	private TableColumn<Process, String> tabColunaNumero;

	@FXML
	private TableColumn<Process, String> tabColunaInteressado;

	@FXML
	private TableColumn<Process, String> tabColunaSituacao;

	private Logger logger = Logger.getLogger(ControleTelaPrincipal.class);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.processoSelecionado = null;
		processService = ConcreteProcessService.getInstance();
		processService.attach(this);
		
		this.configurarTabela();
		this.atualizarTabela(this.processService.getList());
	}
	
	@Override
	public void update() {
		this.atualizarTabela(this.processService.getList());
	}

	@FXML
	private void criarFormularioNovo() {
		this.criarTelaEdicao(CRIAR_PROCESSO, null);
	}

	@FXML
	private void criarFormularioEdicao() {
		this.criarTelaEdicao(EDITAR_PROCESSO, processoSelecionado);
	}

	private void criarTelaEdicao(String titulo, Process processo) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ARQUIVO_FXML_TELA_EDICAO);
			Pane novoPainel = loader.load();

			Stage telaEdicao = new Stage();
			telaEdicao.setTitle(titulo);
			telaEdicao.initModality(Modality.WINDOW_MODAL);
			telaEdicao.initOwner(this.painel.getScene().getWindow());
			telaEdicao.setScene(new Scene(novoPainel, 720, 540));

			ControleTelaEdicao controleTelaEdicao = loader.getController();
			controleTelaEdicao.montarFormulario(processo);
			
			telaEdicao.show();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	@FXML
	private void criarDialogAdmPassword() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ARQUIVO_FXML_DIALOG_PASSWORD);
			Pane novoPainel = loader.load();

			Stage dialogAdmPassword = new Stage();
			dialogAdmPassword.setTitle(DIALOG_ADM_PASS_TITLE);
			dialogAdmPassword.initModality(Modality.WINDOW_MODAL);
			dialogAdmPassword.initOwner(this.painel.getScene().getWindow());
			dialogAdmPassword.setScene(new Scene(novoPainel, 300, 190));

			ControleDialogAdmPassword dialAdmPassController = loader.getController();
			dialAdmPassController.setProcesso(processoSelecionado);
			
			dialogAdmPassword.show();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	@FXML
	private void criarTelaBusca() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ARQUIVO_FXML_TELA_BUSCA);
			Pane novoPainel = loader.load();

			Stage telaBusca = new Stage();
			telaBusca.setTitle(BUSCAR_PROCESSO);
			telaBusca.initModality(Modality.WINDOW_MODAL);
			telaBusca.initOwner(this.painel.getScene().getWindow());
			telaBusca.setScene(new Scene(novoPainel, 720, 660));

			ControleTelaBusca controleTelaBusca = loader.getController();
			controleTelaBusca.configurarFechamento();
			
			telaBusca.show();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void configurarTabela() {
		// inicia as colunas
		tabColunaTipo.setCellValueFactory(
				conteudo -> new ReadOnlyStringWrapper(conteudo.getValue().getTipo()));
		tabColunaNumero.setCellValueFactory(
				conteudo -> {
					String rawText = conteudo.getValue().getNumero();
					MaskedTextField numProcessoMascara;
					StringBuilder finalText;
					if(conteudo.getValue().isTipoOficio()) {
						numProcessoMascara = new MaskedTextField(MASCARA_NUM_OFICIO + "-");
						numProcessoMascara.setPlainText(rawText);
						finalText = new StringBuilder(numProcessoMascara.getText());
						finalText.append(rawText.substring(NUM_OFICIO_OFFSET));
					} else {
						numProcessoMascara = new MaskedTextField(MASCARA_NUM_PROCESSO);
						numProcessoMascara.setPlainText(rawText);
						finalText = new StringBuilder(numProcessoMascara.getText());
					}
					return new ReadOnlyStringWrapper(finalText.toString());
				});
		tabColunaInteressado.setCellValueFactory(
				conteudo -> new ReadOnlyStringWrapper(conteudo.getValue().getInteressado().getNome()));
		tabColunaSituacao.setCellValueFactory(
				conteudo -> new ReadOnlyStringWrapper(conteudo.getValue().getSituacao().getStatus()));

		// eventHandle para detectar o processo selecionado
		tabelaProcessosOficios.getSelectionModel().selectedItemProperty().addListener(
				(observavel, selecionandoAnterior, selecionadoNovo) -> {
					this.processoSelecionado = selecionadoNovo;
					this.btnVerEditar.setDisable(selecionadoNovo!=null? false : true);
					this.btnApagar.setDisable(selecionadoNovo!=null? false : true);
				});
	}

	private void atualizarTabela(List<Process> lista) {
		tabelaProcessosOficios.getItems().setAll(lista);
	}
}