package ingame;

import java.awt.Image;

import javax.swing.ImageIcon;


public class Jelly {
	
	private Image effectImg = new ImageIcon("img/object/jelly/effect.png").getImage();
	private Image hpImg = new ImageIcon("img/object/jelly/jellyHP.png").getImage();
	private Image image; 
	private int x;
	private int y;
	private int width;
	private int height;
	private int alpha;
	private int score;
	
	public Jelly(int imageindex, int x, int y, int width, int height, int alpha, int score) {
		this.image = new ImageIcon("img/object/jelly/jelly"+imageindex+".png").getImage();
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.alpha=alpha;
		this.score=score;
	}
	
	public Jelly(String HP, int x, int y, int width, int height, int alpha, int score) {
		this.image = this.hpImg;
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.alpha=alpha;
		this.score=score;
	}
	
	public Image getEffectImage() {
		return effectImg;
	}
	public Image getHPImage() {
		return hpImg;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
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
	
	public int getAlpha() {
		return alpha;
	}
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
}
