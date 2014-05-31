package at.ac.tuwien.inso.tl.client.gui.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.ac.tuwien.inso.tl.client.client.ArtistService;
import at.ac.tuwien.inso.tl.client.client.LocationService;
import at.ac.tuwien.inso.tl.client.client.NewsService;
import at.ac.tuwien.inso.tl.client.client.PerformanceService;
import at.ac.tuwien.inso.tl.client.client.ShowService;
import at.ac.tuwien.inso.tl.client.exception.ServiceException;
import at.ac.tuwien.inso.tl.client.gui.dialog.ErrorDialog;
import at.ac.tuwien.inso.tl.client.gui.pane.*;
import at.ac.tuwien.inso.tl.client.util.SpringFxmlLoader;
import at.ac.tuwien.inso.tl.dto.ArtistDto;
import at.ac.tuwien.inso.tl.dto.KeyValuePairDto;
import at.ac.tuwien.inso.tl.dto.LocationDto;
import at.ac.tuwien.inso.tl.dto.NewsDto;
import at.ac.tuwien.inso.tl.dto.PerformanceDto;
import at.ac.tuwien.inso.tl.dto.ShowDto;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@Controller
@Scope("prototype")
public class ClientSearchController implements Initializable {
	private static final Logger LOG = Logger.getLogger(ClientSearchController.class);
	private EventHandler<MouseEvent> handler;

	@Autowired
	private ArtistService artistService;
	@Autowired
	private LocationService locationService;
	@Autowired
	private NewsService newsService;
	@Autowired
	private PerformanceService eventService;
	@Autowired
	private ShowService performanceService;
	
	@FXML private StackPane spSearchStack;
	@FXML private TabPane tpFilterTabs;
	@FXML private Tab tpEventTab;
	@FXML private Tab tpPerformanceTab;
	@FXML private Tab tpLocationTab;
	@FXML private Tab tpArtistTab;
	@FXML private GridPane gpChooseSeats;
	@FXML private GridPane gpSearch;
	@FXML private GridPane gpSearchEvents;
	@FXML private GridPane gpSearchPerformances;
	@FXML private GridPane gpTopTen;
	@FXML private GridPane gpTopTenChart;
	@FXML private VBox vbSearchBox;
	@FXML private VBox vbSearchEventsBox;
	@FXML private VBox vbSearchPerformancesBox;
	@FXML private VBox vbTopTenBox;
	@FXML private ChoiceBox<String> chbTopTenCategory;
	@FXML private TextField tfEventTitle;
	@FXML private ChoiceBox<String> cbEventType;
	@FXML private Slider sldEventDuration;
	@FXML private TextField tfEventContent;
	@FXML private TextField tfPerformanceDateFrom;
	@FXML private TextField tfPerformanceDateTo;
	@FXML private TextField tfPerformanceTime1From;
	@FXML private TextField tfPerformanceTime2From;
	@FXML private TextField tfPerformanceTime1To;
	@FXML private TextField tfPerformanceTime2To;
	@FXML private Slider sldPerformancePrice;
	@FXML private TextField tfPerformanceRooms;

	@Override
	public void initialize(URL url, ResourceBundle resBundle) {				
		if(null != vbSearchBox){
			handler = new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
						if(mouseEvent.getClickCount() == 2){
							updateResultList();
						}
					}
				}
			};
			initFilterTabsBehaviour();
			initEventTab();
			initControlListener();
		}
	}

	private void initFilterTabsBehaviour() {		
		tpFilterTabs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> arg0, Tab arg1, Tab arg2) {
				if(arg2.equals(tpEventTab)) {
					initEventTab();
				} else if(arg2.equals(tpPerformanceTab)) {
					initPerformanceTab();
				} else if(arg2.equals(tpLocationTab)) {
					initLocationTab();
				} else {
					initArtistTab();
				}
			}
		});
	}    

	private void initEventTab() {
		LOG.info("initEventTab clicked");
		vbSearchBox.getChildren().clear();

		List<PerformanceDto> events = null;
		try {
			cbEventType.getItems().clear();
			List<String> categories = this.eventService.getAllPerformanceTypes();
			cbEventType.getItems().addAll(categories);
			events = this.eventService.getAllPerformances();
		} catch (ServiceException e) {
			LOG.error("Could not retrieve performances: " + e.getMessage(), e);
			Stage error = new ErrorDialog(e.getMessage());
			error.show();
			return;
		}

		ListView<EventPane> listview = new ListView<EventPane>();
		listview.setMinWidth(vbSearchBox.getWidth());
		if(!events.isEmpty()) {		
			List<EventPane> eventList = new ArrayList<EventPane>();
			for(PerformanceDto p : events){
				eventList.add(new EventPane(p.getDescription(), p.getPerformancetype(), p.getDurationInMinutes(), p.getContent()));
			}
	
			listview = new ListView<EventPane>(FXCollections.observableArrayList(eventList));
			listview.setMinWidth(vbSearchBox.getWidth());
			listview.setOnMouseClicked(handler);
		}
		vbSearchBox.getChildren().add(listview);
	}
	
	private void initControlListener() {
		sldEventDuration.valueProperty().addListener(new ChangeListener<Number>() {
			@Override public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
				updateEventList();
			}
		});
		cbEventType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				updateEventList();
			}
		});
	} 

	private void initPerformanceTab() {
		LOG.info("initPerformanceTab clicked");
		vbSearchBox.getChildren().clear();
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		SimpleDateFormat df2 = new SimpleDateFormat("H:m");

		List<ShowDto> performances = null;
		try {
			performances = this.performanceService.getAllShows();
		} catch (ServiceException e) {
			LOG.error("Could not retrieve news: " + e.getMessage(), e);
			Stage error = new ErrorDialog(e.getMessage());
			error.show();
			return;
		}

		List<PerformancePane> performanceList = new ArrayList<PerformancePane>();
		for(ShowDto s : performances){
			Date performanceDate = s.getDateOfPerformance();
			performanceList.add(new PerformancePane("Titel der Aufführung", df.format(performanceDate), 
													df2.format(performanceDate), s.getPriceInCent()/100d, s.getRoom()));
		}

		ListView<PerformancePane> listview = new ListView<PerformancePane>(FXCollections.observableArrayList(performanceList));
		listview.setMinWidth(vbSearchBox.getWidth());
		listview.setOnMouseClicked(handler);
		vbSearchBox.getChildren().add(listview);
	}

	private void initLocationTab() {
		LOG.info("initLocationTab clicked");
		vbSearchBox.getChildren().clear();

		List<LocationDto> locations = null;
		try {
			locations = this.locationService.getAllLocations();
		} catch (ServiceException e) {
			LOG.error("Could not retrieve news: " + e.getMessage(), e);
			Stage error = new ErrorDialog(e.getMessage());
			error.show();
			return;
		}

		List<LocationPane> locationList = new ArrayList<LocationPane>();
		for(LocationDto l : locations){	        	
			locationList.add(new LocationPane("Bezeichnung", l.getStreet(), l.getCity(), l.getPostalcode(), l.getCountry(), l.getDescription()));
		}

		ListView<LocationPane> listview = new ListView<LocationPane>(FXCollections.observableArrayList(locationList));
		listview.setMinWidth(vbSearchBox.getWidth());
		listview.setOnMouseClicked(handler);
		vbSearchBox.getChildren().add(listview);
	}

	private void initArtistTab() {
		LOG.info("initArtistTab clicked");
		vbSearchBox.getChildren().clear();

		List<ArtistDto> artists = null;
		try {
			artists = this.artistService.getAllArtists();
		} catch (ServiceException e) {
			LOG.error("Could not retrieve news: " + e.getMessage(), e);
			Stage error = new ErrorDialog(e.getMessage());
			error.show();
			return;
		}

		List<ArtistPane> artistList = new ArrayList<ArtistPane>();
		for(ArtistDto a : artists){	        	
			artistList.add(new ArtistPane(a.getFirstname(), a.getLastname()));
		}

		ListView<ArtistPane> listview = new ListView<ArtistPane>(FXCollections.observableArrayList(artistList));
		listview.setMinWidth(vbSearchBox.getWidth());
		listview.setOnMouseClicked(handler);
		vbSearchBox.getChildren().add(listview);
	}

	private void initTopTen() {
		LOG.info("initEventTab clicked");
		gpTopTenChart.add(new TopTenBarChartPane(),0,1);
		vbTopTenBox.getChildren().clear();

		List<NewsDto> news = null;
		List<String> categories = null;
		try {
			news = this.newsService.getNews();
			categories = this.eventService.getAllPerformanceTypes();
			chbTopTenCategory.getItems().addAll(categories);
			chbTopTenCategory.getSelectionModel().selectFirst();
		} catch (ServiceException e) {
			LOG.error("Could not retrieve news: " + e.getMessage(), e);
			Stage error = new ErrorDialog(e.getMessage());
			error.show();
			return;
		}

		List<EventPane> eventList = new ArrayList<EventPane>();
		for(NewsDto n : news){
			String newsText = new String(n.getNewsText());
			String title = new String(n.getTitle());
			eventList.add(new EventPane(title, "Konzert", 30, newsText));
		}

		ListView<EventPane> listview = new ListView<EventPane>(FXCollections.observableArrayList(eventList));
		listview.setMinWidth(vbTopTenBox.getWidth());
		listview.setOnMouseClicked(handler);
		vbTopTenBox.getChildren().add(listview);
	}

	@FXML
	void handleGoToTopTen(ActionEvent event) {
		gpSearch.setVisible(false);
		initTopTen();
		gpTopTen.setVisible(true);
	}

	@FXML
	void handleGoToSearch(ActionEvent event) {
		gpTopTen.setVisible(false);
		initEventTab();
		gpSearch.setVisible(true);
	}
	
	private void updateEventList() {
		try {
			vbSearchBox.getChildren().clear();
			List<EventPane> eventList = new ArrayList<EventPane>();
			List<KeyValuePairDto<PerformanceDto, Integer>> keyValueList = eventService.findPerformancesSortedBySales(
					tfEventContent.getText(), tfEventTitle.getText(), (int)sldEventDuration.getValue()-10,
					(int)sldEventDuration.getValue()+10, cbEventType.getSelectionModel().getSelectedItem(),	null);
						
			LOG.info("keyValueList is empty: " + keyValueList.isEmpty());
			for(KeyValuePairDto<PerformanceDto, Integer> keyValue : keyValueList) {
				PerformanceDto p = keyValue.getKey();
				eventList.add(new EventPane(p.getDescription(), p.getPerformancetype(), p.getDurationInMinutes(), p.getContent()));
			}

			ListView<EventPane> listview = new ListView<EventPane>(FXCollections.observableArrayList(eventList));
			listview.setMinWidth(vbSearchBox.getWidth());
			listview.setMinWidth(vbTopTenBox.getWidth());
			listview.setOnMouseClicked(handler);
			vbSearchBox.getChildren().add(listview);
			
		} catch (ServiceException e) {
			LOG.error("Could not update events: " + e.getMessage(), e);
			Stage error = new ErrorDialog(e.getMessage());
			error.show();
			return;
		}
	}
	
	private void updatePerformanceList() {
		
	}
	
	private void updateLocationList() {
		
	}
	
	private void updateArtistList() {
		
	}

	private void updateResultList() {
		if(gpTopTen.isVisible()) {
			gpTopTen.setVisible(false);
			findPerformancesByEvent();
			gpSearchPerformances.setVisible(true);
		} else if(gpSearchEvents.isVisible()) {
			gpSearchEvents.setVisible(false);
			findPerformancesByEvent();
			gpSearchPerformances.setVisible(true);
		} else if(gpSearchPerformances.isVisible()) {
			gpSearchPerformances.setVisible(false);
			findPerformancesByEvent();
			gpChooseSeats.setVisible(true);
		} else {
			Tab current = tpFilterTabs.getSelectionModel().getSelectedItem();
			if(current.equals(tpEventTab)) {
				gpSearch.setVisible(false);
				findPerformancesByEvent();
				gpSearchPerformances.setVisible(true);
			} else if(current.equals(tpPerformanceTab)) {
				gpSearch.setVisible(false);
				gpChooseSeats.setVisible(true);
			} else if(current.equals(tpLocationTab)) {
				gpSearch.setVisible(false);
				findPerformancesByLocation();
				gpSearchPerformances.setVisible(true);
			} else {
				gpSearch.setVisible(false);
				findEventsByArtist();
				gpSearchEvents.setVisible(true);
			}
		}
	}
	
	private void findPerformancesByEvent() {
		LOG.info("getPerformancesByEvent");
		vbSearchPerformancesBox.getChildren().clear();
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

		List<NewsDto> news = null;
		try {
			news = this.newsService.getNews();
		} catch (ServiceException e) {
			LOG.error("Could not retrieve news: " + e.getMessage(), e);
			Stage error = new ErrorDialog(e.getMessage());
			error.show();
			return;
		}

		List<PerformancePane> performanceList = new ArrayList<PerformancePane>();
		for(NewsDto n : news){
			String newsText = new String(n.getNewsText());
			String title = new String(n.getTitle());
			performanceList.add(new PerformancePane(title, df.format(n.getSubmittedOn()), "00:30", 19.99, newsText));
		}

		ListView<PerformancePane> listview = new ListView<PerformancePane>(FXCollections.observableArrayList(performanceList));
		listview.setMinWidth(vbSearchPerformancesBox.getWidth());
		listview.setOnMouseClicked(handler);
		vbSearchPerformancesBox.getChildren().add(listview);
	}
	
	private void findPerformancesByLocation() {
		LOG.info("getPerformancesByEvent");
		vbSearchPerformancesBox.getChildren().clear();
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

		List<NewsDto> news = null;
		try {
			news = this.newsService.getNews();
		} catch (ServiceException e) {
			LOG.error("Could not retrieve news: " + e.getMessage(), e);
			Stage error = new ErrorDialog(e.getMessage());
			error.show();
			return;
		}

		List<PerformancePane> performanceList = new ArrayList<PerformancePane>();
		for(NewsDto n : news){
			String newsText = new String(n.getNewsText());
			String title = new String(n.getTitle());
			performanceList.add(new PerformancePane(title, df.format(n.getSubmittedOn()), "00:30", 19.99, newsText));
		}

		ListView<PerformancePane> listview = new ListView<PerformancePane>(FXCollections.observableArrayList(performanceList));
		listview.setMinWidth(vbSearchPerformancesBox.getWidth());
		listview.setOnMouseClicked(handler);
		vbSearchPerformancesBox.getChildren().add(listview);
	}
	
	private void findEventsByArtist() {
		LOG.info("initEventTab clicked");
		vbSearchEventsBox.getChildren().clear();

		List<NewsDto> news = null;
		try {
			news = this.newsService.getNews();
		} catch (ServiceException e) {
			LOG.error("Could not retrieve news: " + e.getMessage(), e);
			Stage error = new ErrorDialog(e.getMessage());
			error.show();
			return;
		}

		List<EventPane> eventList = new ArrayList<EventPane>();
		for(NewsDto n : news){
			String newsText = new String(n.getNewsText());
			String title = new String(n.getTitle());
			eventList.add(new EventPane(title, "Veranstaltungstyp", 30, newsText));
		}

		ListView<EventPane> listview = new ListView<EventPane>(FXCollections.observableArrayList(eventList));
		listview.setMinWidth(vbSearchEventsBox.getWidth());
		listview.setOnMouseClicked(handler);
		vbSearchEventsBox.getChildren().add(listview);
	}
	
	@FXML
	void handleEventTitleChanged(KeyEvent event) {
		
	}
	
	@FXML
	void handleEventContentChanged(KeyEvent event) {
		
	}
	
	@FXML
	void handleSelectEventsFromSearchEvents(ActionEvent event) {
		LOG.info("handleSelectPerformanceFromSearchPerformances clicked");
		gpSearchEvents.setVisible(false);
		findPerformancesByEvent();
		gpSearchPerformances.setVisible(true);
	}
	
	@FXML
	void handleReturnFromSearchEvents(ActionEvent event) {
		LOG.info("handleReturnFromSearchEvents clicked");
		gpSearchEvents.setVisible(false);
		gpSearch.setVisible(true);
	}
	
	@FXML
	void handleSelectPerformanceFromSearchPerformances(ActionEvent event) {
		LOG.info("handleSelectPerformanceFromSearchPerformances clicked");
		gpSearchPerformances.setVisible(false);
		gpChooseSeats.setVisible(true);
	}
	
	@FXML
	void handleReturnFromSearchPerformances(ActionEvent event) {
		LOG.info("handleReturnFromSearchPerformances clicked");
		gpSearchPerformances.setVisible(false);
		gpSearch.setVisible(true);
	}
	
	@FXML
	void handleReserveSeats(ActionEvent event) {
		LOG.info("handleReserveSeats clicked");
		BorderPane parent = (BorderPane)spSearchStack.getParent().lookup("#bpSellTicket");
		Image imgWorkflow = new Image(SpringFxmlLoader.class.getResource("/images/ClientStep.png").toString());
		ImageView iv = (ImageView)spSearchStack.getParent().lookup("#ivWorkflow");
		iv.setImage(imgWorkflow);
		parent.setCenter((Node)SpringFxmlLoader.getInstance().load("/gui/ClientChooseClientGui.fxml"));
	}
}