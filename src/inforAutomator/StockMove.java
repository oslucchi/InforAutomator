package inforAutomator;

import java.util.Date;

public class StockMove
{	

	protected int idStockMove;
	protected int idStock;
	protected int fromLocation;
	protected int toLocation;
	protected int idArticle;
	protected String articleCode;
	protected String articleDescription;
	protected double quantity;
	protected Date timestamp;

	protected boolean selected = false;
	
	private String tableName = "StockMove";
	private String idColName = "idStockMove";

	public StockMove()
	{
	}

	public int getIdStockMove() {
		return idStockMove;
	}

	public void setIdStockMove(int idStockMove) {
		this.idStockMove = idStockMove;
	}

	public int getIdStock() {
		return idStock;
	}

	public void setIdStock(int idStock) {
		this.idStock = idStock;
	}

	public int getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(int fromLocation) {
		this.fromLocation = fromLocation;
	}

	public int getToLocation() {
		return toLocation;
	}

	public void setToLocation(int toLocation) {
		this.toLocation = toLocation;
	}

	public int getIdArticle() {
		return idArticle;
	}

	public void setIdArticle(int idArticle) {
		this.idArticle = idArticle;
	}

	public String getArticleCode() {
		return articleCode;
	}

	public void setArticleCode(String articleCode) {
		this.articleCode = articleCode;
	}

	public String getArticleDescription() {
		return articleDescription;
	}

	public void setArticleDescription(String articleDescription) {
		this.articleDescription = articleDescription;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getIdColName() {
		return idColName;
	}

	public void setIdColName(String idColName) {
		this.idColName = idColName;
	}
}
