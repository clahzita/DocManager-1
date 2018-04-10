package apresentacao;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import negocio.dominio.Processo;
import negocio.fachada.FachadaCaixasDeEscolha;
import negocio.fachada.FachadaNegocio;
import negocio.servico.Observador;
import negocio.servico.ValidationException;
import utils.widget.DynamicMaskTextField;
import utils.widget.MaskedTextField;

public class ControleTelaBusca implements Initializable, Observador {

	private static Logger logger = Logger.getLogger(ControleTelaBusca.class);

	private static final URL ARQUIVO_FXML_TELA_EDICAO = ControleTelaPrincipal.class.getResource("/visoes/tela_editar_processo.fxml");
	private static final URL ARQUIVO_FXML_DIALOG_PASSWORD = ControleTelaPrincipal.class.getResource("/visoes/dialog_adm_password.fxml");
	private static final String CHOICEBOX_TEXTO_PADRAO = "-- SELECIONE --";
	private static final String MASCARA_NUM_OFICIO = "####/####";
	private static final String MASCARA_NUM_PROCESSO = "#####.######/####-##";
	private static final String MASCARA_CPF = "###.###.###-##";
	private static final String DIALOG_ADM_PASS_TITLE = "Autorização";
	private static final String EDITAR_PROCESSO_TITLE = "Editar Processo";

	private FachadaCaixasDeEscolha fachada;
	private Processo processoSelecionado;
	private MaskedTextField mTxtCpf;
	private DynamicMaskTextField dmTxtOficioNum;
	private UltimaBusca ultimaBusca;

	@FXML
	private Node root;

	@FXML
	private VBox vbNumero;

	@FXML
	private VBox vbInteressado;

	@FXML
	private CheckBox checkNumero;

	@FXML
	private CheckBox checkInteressado;

	@FXML
	private CheckBox checkOrgao;

	@FXML
	private CheckBox checkAssunto;

	@FXML
	private CheckBox checkSituacao;

	@FXML
	private ToggleGroup tgProcessoOficio;

	@FXML
	private ToggleGroup tgNomeCpf;

	@FXML
	private RadioButton radioProcesso;

	@FXML
	private RadioButton radioOficio;

	@FXML
	private RadioButton radioNome;

	@FXML
	private RadioButton radioCpf;

	@FXML
	private MaskedTextField mTxtProcessoNum;

	@FXML
	private TextField txtNome;

	@FXML
	private ChoiceBox<String> choiceOrgao;

	@FXML
	private ChoiceBox<String> choiceAssunto;

	@FXML
	private ChoiceBox<String> choiceSituacao;

	@FXML
	private TableView<Processo> tableResultados;

	@FXML
	private TableColumn<Processo, String> tabColTipo;

	@FXML
	private TableColumn<Processo, String> tabColNumero;

	@FXML
	private TableColumn<Processo, String> tabColInteressado;

	@FXML
	private TableColumn<Processo, String> tabColSituacao;

	@FXML
	private Button btnVerEditar;

	@FXML
	private Button btnApagar;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.fachada = FachadaNegocio.getInstance();
		this.fachada.cadastrarObservador(this);
		this.processoSelecionado = null;
		this.ultimaBusca = null;
		this.mTxtCpf = new MaskedTextField(MASCARA_CPF);
		this.mTxtCpf.setMaxWidth(520.0);
		this.dmTxtOficioNum = new DynamicMaskTextField(MASCARA_NUM_OFICIO + "-*", 9);
		this.dmTxtOficioNum.setMaxWidth(520.0);
		configureForm();
		configurarTabela();
	}

	@Override
	public void atualizar() {
		if (this.ultimaBusca != null) {
			List<Processo> resultado = this.fachada.buscarProcessos(
					ultimaBusca.numero,
					ultimaBusca.nomeInteressado,
					ultimaBusca.cpfInteressado,
					ultimaBusca.idSituacao,
					ultimaBusca.idOrgao,
					ultimaBusca.idAssunto);

			atualizarTabela(resultado);
		}
	}

	@FXML
	private void buscar() {
		String numProcesso = (checkNumero.isSelected())? getProcessNumberEntry() : "";
		String nomeInteressado = (checkInteressado.isSelected() && radioProcesso.isSelected())? txtNome.getText() : "";
		String cpfInteressado = (checkInteressado.isSelected() && radioCpf.isSelected())? mTxtCpf.getPlainText() : "";
		int idOrgao = checkOrgao.isSelected()? choiceOrgao.getSelectionModel().getSelectedIndex() : 0;
		int idAssunto = checkAssunto.isSelected()? choiceAssunto.getSelectionModel().getSelectedIndex() : 0;
		int idSituacao = checkSituacao.isSelected()? choiceSituacao.getSelectionModel().getSelectedIndex() : 0;

		try {
			List<Processo> resultado = this.fachada.buscarProcessos(numProcesso, nomeInteressado, cpfInteressado, idSituacao, idOrgao, idAssunto);
			this.ultimaBusca = new UltimaBusca(numProcesso, nomeInteressado, cpfInteressado, idOrgao, idAssunto, idSituacao);
			atualizarTabela(resultado);
		} catch (ValidationException ve) {
			Alert alert = new Alert(AlertType.ERROR, ve.getMessage() + "\n\n");
			alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(
					node -> {
						((Label)node).setMinHeight(Region.USE_PREF_SIZE);
						((Label)node).setTextFill(Color.RED);
					});
			alert.setHeaderText(null);
			alert.setGraphic(null);
			alert.initOwner(root.getScene().getWindow());

			alert.showAndWait();
		}
	}

	private String getProcessNumberEntry() {
		if (radioProcesso.isSelected()) {
			return mTxtProcessoNum.plainTextProperty().getValue();
		}

		StringBuilder numProcesso = new StringBuilder();
		numProcesso.append(mTxtProcessoNum.plainTextProperty().getValue());
		if (checkOrgao.isSelected() && choiceOrgao.getSelectionModel().getSelectedIndex() != 0) {
			numProcesso.append(choiceOrgao.getSelectionModel().getSelectedItem().split("-")[0]);
		}
		return numProcesso.toString();
	}

	@FXML
	private void limparFormulario() {
		checkNumero.setSelected(false);
		checkInteressado.setSelected(false);
		checkOrgao.setSelected(false);
		checkAssunto.setSelected(false);
		checkSituacao.setSelected(false);
		radioProcesso.setSelected(true);
		radioNome.setSelected(true);
		choiceOrgao.getSelectionModel().select(0);
		choiceAssunto.getSelectionModel().select(0);
		choiceSituacao.getSelectionModel().select(0);
		txtNome.clear();
		mTxtProcessoNum.clear();
		mTxtCpf.clear();
		dmTxtOficioNum.clear();
		dmTxtOficioNum.setDynamic(true);
	}

	@FXML
	private void fecharJanela() {
		Stage window = (Stage) this.root.getScene().getWindow();
		if (window != null)
			window.close();
	}

	private void configureForm() {
		preencherChoiceBoxes();
		configurarRadioButtons();
		configurarChoiceBoxOrgao();
		configurarChoiceBoxAssunto();
		configurarChoiceBoxSituacao();
		configurarCheckBoxOrgao();
		configurarTextFieldsNumeroProcesso();
		configurarTextFieldsInteressado();
	}

	private void preencherChoiceBoxes() {
		ObservableList<String> obsListaOrgaos = this.choiceOrgao.getItems();
		obsListaOrgaos.add(CHOICEBOX_TEXTO_PADRAO);
		obsListaOrgaos.addAll(fachada.getListaOrgaos());
		this.choiceOrgao.getSelectionModel().select(0);

		ObservableList<String> obsListaAssuntos = this.choiceAssunto.getItems();
		obsListaAssuntos.add(CHOICEBOX_TEXTO_PADRAO);
		obsListaAssuntos.addAll(fachada.getListaAssuntos());
		this.choiceAssunto.getSelectionModel().select(0);

		ObservableList<String> obsListaSituacoes = this.choiceSituacao.getItems();
		obsListaSituacoes.add(CHOICEBOX_TEXTO_PADRAO);
		obsListaSituacoes.addAll(fachada.getListaSituacoes());
		this.choiceSituacao.getSelectionModel().select(0);
	}

	private void configurarRadioButtons() {
		this.tgProcessoOficio.selectedToggleProperty().addListener(
				(observavel, valorAnterior, novoValor) ->  {
					if(Objects.equals(novoValor, this.radioProcesso)) {
						this.vbNumero.getChildren().remove(this.dmTxtOficioNum);
						this.vbNumero.getChildren().add(this.mTxtProcessoNum);
					} else {
						this.vbNumero.getChildren().remove(this.mTxtProcessoNum);
						this.vbNumero.getChildren().add(this.dmTxtOficioNum);
					}

				});
		this.tgNomeCpf.selectedToggleProperty().addListener(
				(observavel, valorAnterior, novoValor) -> {
					if(Objects.equals(novoValor, this.radioNome)) {
						this.vbInteressado.getChildren().remove(this.mTxtCpf);
						this.vbInteressado.getChildren().add(this.txtNome);
					} else {
						this.vbInteressado.getChildren().remove(this.txtNome);
						this.vbInteressado.getChildren().add(this.mTxtCpf);
					}
				});
	}

	private void configurarChoiceBoxOrgao() {
		choiceOrgao.getSelectionModel().selectedIndexProperty().addListener(
				(observableValue, oldValue, newValue) -> { 
					if (newValue.intValue() == 0) {
						this.dmTxtOficioNum.setDynamic(true);
						if (oldValue.intValue() != 0 && maskIsCompletelyFilled(dmTxtOficioNum, "#")) {	
							int oldIndex = oldValue.intValue();
							StringBuilder newText = new StringBuilder(dmTxtOficioNum.getPlainText());

							newText.append(choiceOrgao.getItems().get(oldIndex).split(" - ")[0]);
							dmTxtOficioNum.adjustMask(newText.length());
							dmTxtOficioNum.setPlainText(newText.toString());
						}
					} else {
						if (!checkOrgao.isSelected()) {
							checkOrgao.setSelected(true);
						}
						int newIndex = newValue.intValue();
						String initials = choiceOrgao.getItems().get(newIndex).split(" - ")[0];
						this.dmTxtOficioNum.setDynamic(false);
						this.dmTxtOficioNum.setMask(MASCARA_NUM_OFICIO + "-" + initials);
					}
				});
	}

	private void configurarChoiceBoxAssunto() {
		choiceAssunto.getSelectionModel().selectedIndexProperty().addListener(
				(observableValue, oldValue, newValue) -> {
					if (newValue.intValue() != 0)
						checkAssunto.setSelected(true);
				});
	}

	private void configurarChoiceBoxSituacao() {
		choiceSituacao.getSelectionModel().selectedIndexProperty().addListener(
				(observableValue, oldValue, newValue) -> {
					if (newValue.intValue() != 0)
						checkSituacao.setSelected(true);
				});
	}

	private void configurarCheckBoxOrgao() {
		checkOrgao.selectedProperty().addListener(
				(valorObservado, valorAntigo, valorNovo) -> {
					if (valorNovo && choiceOrgao.getSelectionModel().getSelectedIndex() != 0) {
						this.dmTxtOficioNum.setDynamic(false);
						String orgao = choiceOrgao.getSelectionModel().getSelectedItem();
						this.dmTxtOficioNum.setMask(MASCARA_NUM_OFICIO + "-" + orgao.split(" - ")[0]);

					} else {
						this.dmTxtOficioNum.setDynamic(true);
						boolean validChoice = choiceOrgao.getSelectionModel().getSelectedIndex() != 0;

						if (validChoice && maskIsCompletelyFilled(dmTxtOficioNum, "#")) {
							StringBuilder newText = new StringBuilder(dmTxtOficioNum.getPlainText());
							String initials = choiceOrgao.getSelectionModel().getSelectedItem().split(" - ")[0];

							newText.append(initials);
							dmTxtOficioNum.adjustMask(newText.length());
							dmTxtOficioNum.setPlainText(newText.toString());
						}
					}
				});
	}

	private void configurarTextFieldsNumeroProcesso() {
		mTxtProcessoNum.focusedProperty().addListener(
				(observableValue, oldValue, newValue) -> {
					if (!newValue && !mTxtProcessoNum.getPlainText().isEmpty())
						checkNumero.setSelected(true);
				});

		dmTxtOficioNum.focusedProperty().addListener(
				(observableValue, oldValue, newValue) -> {
					if (!newValue && !dmTxtOficioNum.getPlainText().isEmpty())
						checkNumero.setSelected(true);
				});
	}

	private void configurarTextFieldsInteressado() {
		txtNome.focusedProperty().addListener(
				(observableValue, oldValue, newValue) -> {
					if (!newValue && !txtNome.getText().isEmpty())
						checkInteressado.setSelected(true);
				});
		mTxtCpf.focusedProperty().addListener(
				(observableValue, oldValue, newValue) -> {
					if (!newValue && !mTxtCpf.getPlainText().isEmpty())
						checkInteressado.setSelected(true);
				});
	}

	private void configurarTabela() {
		// inicia as colunas
		tabColTipo.setCellValueFactory(
				conteudo -> new ReadOnlyStringWrapper(conteudo.getValue().getTipo()));
		tabColNumero.setCellValueFactory(
				conteudo -> {
					String rawText = conteudo.getValue().getNumero();
					MaskedTextField numProcessoMascara;
					StringBuilder finalText;
					if(conteudo.getValue().isTipoOficio()) {
						numProcessoMascara = new MaskedTextField(MASCARA_NUM_OFICIO + "-");
						numProcessoMascara.setPlainText(rawText);
						finalText = new StringBuilder(numProcessoMascara.getText());
						finalText.append(rawText.substring(8));
					} else {
						numProcessoMascara = new MaskedTextField(MASCARA_NUM_PROCESSO);
						numProcessoMascara.setPlainText(rawText);
						finalText = new StringBuilder(numProcessoMascara.getText());
					}
					return new ReadOnlyStringWrapper(finalText.toString());
				});
		tabColInteressado.setCellValueFactory(
				conteudo -> new ReadOnlyStringWrapper(conteudo.getValue().getInteressado().getNome()));
		tabColSituacao.setCellValueFactory(
				conteudo -> new ReadOnlyStringWrapper(conteudo.getValue().getSituacao().getStatus()));

		// eventHandle para detectar o processo selecionado
		tableResultados.getSelectionModel().selectedItemProperty().addListener(
				(observavel, selecionandoAnterior, selecionadoNovo) -> {
					this.processoSelecionado = selecionadoNovo;
					this.btnVerEditar.setDisable(selecionadoNovo!=null? false : true);
					this.btnApagar.setDisable(selecionadoNovo!=null? false : true);
				});
	}

	public void configurarFechamento() {
		this.root.getScene().getWindow().setOnHidden(
				event -> this.fachada.descadastrarObservador(this)
				);
	}

	private void atualizarTabela(List<Processo> lista) {
		tableResultados.getItems().setAll(lista);
	}

	private boolean maskIsCompletelyFilled (MaskedTextField mTextField, String maskChar) {
		String mask = mTextField.getMask();
		int maskFillingLength = mask.length() - mask.replaceAll(maskChar, "").length();
		int plainTextLength = mTextField.getPlainText().length();

		return (plainTextLength == maskFillingLength);
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
			dialogAdmPassword.initOwner(this.root.getScene().getWindow());
			dialogAdmPassword.setScene(new Scene(novoPainel, 300, 190));

			ControleDialogAdmPassword dialAdmPassController = loader.getController();
			dialAdmPassController.setProcesso(this.processoSelecionado);

			dialogAdmPassword.show();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@FXML
	private void criarTelaEdicao() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ARQUIVO_FXML_TELA_EDICAO);
			Pane novoPainel = loader.load();

			Stage telaEdicao = new Stage();
			telaEdicao.setTitle(EDITAR_PROCESSO_TITLE);
			telaEdicao.initModality(Modality.WINDOW_MODAL);
			telaEdicao.initOwner(this.root.getScene().getWindow());
			telaEdicao.setScene(new Scene(novoPainel, 720, 540));

			ControleTelaEdicao controleTelaEdicao = loader.getController();
			controleTelaEdicao.montarFormulario(this.processoSelecionado);

			telaEdicao.show();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/* Estrutura de Dados para armazenar a ultima busca */
	private class UltimaBusca {
		private String numero; 
		private String nomeInteressado;
		private String cpfInteressado;
		private int idOrgao;
		private int idAssunto;
		private int idSituacao; 

		public UltimaBusca(String num, String nomeInter, String cpfInter, int idOrg, int idAss, int idSit) {
			numero = num; 
			nomeInteressado = nomeInter;
			cpfInteressado = cpfInter;
			idOrgao = idOrg;
			idAssunto = idAss;
			idSituacao = idSit;
		}
	}
}
