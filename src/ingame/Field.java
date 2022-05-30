package ingame;

import java.awt.Image;

import javax.swing.ImageIcon;


public class Field {
	
	private Image image; 
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Field(int stage, int fieldtype, int x, int y, int width, int height) {
		this.image = new ImageIcon("img/object/map"+stage+"/field"+fieldtype+".png").getImage();
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
	}
	
	public Image getImage() {
		return image;
	}
	public void setX(Image image) {
		this.image = image;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
}