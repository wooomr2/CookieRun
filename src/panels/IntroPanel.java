package panels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class IntroPanel extends JPanel {
	
	Image introImg = new ImageIcon("img/intro/intro.png").getImage();	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension d = getSize();
		g.drawImage(introImg, 0, 0, d.width,d.height, null);
	}

}