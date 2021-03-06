package at.ac.tuwien.inso.tl.client.gui.pane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class LocationPane extends Pane{
	private Integer id;
	private String title;
	private String street;
	private String name;
	private String postal;
	private String country;
	
	private Double textWidth = 750d;
	
	private Text tx_title;
	private Label lbl_street;
	private Label lbl_details;
	
	public LocationPane(Integer id, String title, String street, 
						String name, String postal, String country){
		this.id = id;
		this.title = title;
		this.street = street;
		this.name = name;
		this.postal = postal;
		this.country = country;
		
		init();
	}
	
	private void init(){
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER_LEFT);
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setPadding(new Insets(25, 25, 25, 25));
		
		ColumnConstraints column = new ColumnConstraints();
		column.setMinWidth(200);
		grid.getColumnConstraints().add(column);
		int row = 0;
		
		tx_title = new Text(title);
		tx_title.setWrappingWidth(textWidth);
		tx_title.setId("tx_title");
		grid.add(tx_title, 0, row++);
		
		grid.add(new Separator(), 0, row++);
		
		lbl_street = new Label(street);
		lbl_street.setId("tx_date");
		grid.add(lbl_street, 0, row++);
		
		lbl_details = new Label(postal + ", " + name + ", " + country);
		lbl_details.setWrapText(true);
		lbl_details.setMaxWidth(textWidth);
		grid.add(lbl_details, 0, row++);
		
		this.getChildren().add(grid);
		this.getStylesheets().add("/gui/style.css");
	}
	
	public Integer getLocationId() {
		return id;
	}
}
