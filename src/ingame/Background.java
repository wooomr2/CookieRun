package ingame;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Background {
	private Image image;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Background(Image image, int x, int y, int width, int height) {
		this.image = image;
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
	}

	public Background(int stage, int bgtype, int order) {
		this.image = new ImageIcon("img/object/map"+stage+"/bg"+bgtype+".png").getImage();
		
		if(order == 1) this.x= 0;
		else if(order == 2) this.x= this.image.getWidth(null);
		
		this.y=0;
		this.width=this.image.getWidth(null);
		this.height=this.image.getHeight(null);
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