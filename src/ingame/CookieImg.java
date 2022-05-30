package ingame;

import java.awt.Image;

public class CookieImg {
	private Image normal;
	private Image jump;
	private Image doubleJump; 
	private Image fall; 
	private Image slide; 
	private Image hit; 
	
	public CookieImg(Image normal, Image jump, Image doubleJump, Image fall, Image slide, Image hit) {
		this.normal = normal;
		this.jump = jump;
		this.doubleJump = doubleJump;
		this.fall = fall;
		this.slide = slide;
		this.hit = hit;
	}
	
	public Image getNormal() {
		 return normal;
	}
	public Image getJump() {
		 return jump;
	}
	public Image getDoubleJump() {
		 return doubleJump;
	}
	public Image getFall() {
		 return fall;
	}
	public Image getSlide() {
		 return slide;
	}
	public Image getHit() {
		 return hit;
	}
	
} 