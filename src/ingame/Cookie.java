package ingame;

import java.awt.Image;

public class Cookie {
	
	private Image image;
	
	private int x = 160;
	private int y = 0;
	private int width = 80;
	private int height = 120;
	
	private int alpha = 255;

	private int health = 1000;

	private int big = 0; //미구현
	private int fast = 0; //미구현
	private int countJump = 0;
	private boolean invincible = false; 
	private boolean fall = false; 
	private boolean jump = false; 
	
	public Cookie(Image image){ 
		this.image = image;
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
	
	public int getAlpha() {
		return alpha;
	}
	
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getHealth() {
		return health;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public int getbig() {
		return big;
	}
	public void setBig(int big) {
		this.big = big;
	}
	
	public int getFast() {
		return fast;
	}
	public void setFast(int fast) {
		this.fast = fast;
	}
	
	public int getCountJump() {
		return countJump;
	}
	public void setCountJump(int countJump) {
		this.countJump = countJump;
	}
	
	public boolean isFall() {
		return fall;
	}
	public void setFall(boolean fall) {
		this.fall = fall;
	}
	
	public boolean isInvincible() {
		return invincible;
	}
	public void setInvincible(boolean invincible) {
		this.invincible = invincible;
	}
	
	public boolean isJump() {
		return jump;
	}
	public void setJump(boolean jump) {
		this.jump = jump;
	}
	
}