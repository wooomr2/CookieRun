package panels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import ingame.CookieImg;

public class SelectPanel extends JPanel {
	
	private Image bgImg = new ImageIcon("img/select/selectBg.png").getImage();	
	
	private JButton[] chButton = new JButton[4];
	
	private JButton StartBtn = new JButton(new ImageIcon("img/select/StartBtn.png"));
	
	private CookieImg cookieimg;
	
	public CookieImg getCookieImg() {
		return cookieimg;
	}
	
	private int index;
	
	public SelectPanel(Object o) {
		
		setLayout(null);
		
		//캐릭터 버튼 배치
		for(int i=0; i<4; i++) {
			chButton[i] = new JButton(new ImageIcon("img/select/selectCh"+i+".png"));
			chButton[i].setBounds(90+150*i, 100, 150, 200);
			chButton[i].setBorderPainted(false);
			chButton[i].setContentAreaFilled(false);
			chButton[i].setFocusPainted(false);
			chButton[i].addMouseListener(new BtnMouseAdapter());
			add(chButton[i]);
		}
		
		//시작 버튼
		StartBtn.addMouseListener((MouseListener) o);
		StartBtn.setBounds(255, 330, 290, 80);
		StartBtn.setBorderPainted(false);
		StartBtn.setContentAreaFilled(false);
		StartBtn.setFocusPainted(false);
		add(StartBtn);
		
	}
	
	private class BtnMouseAdapter extends MouseAdapter {
		
		@Override
		public void mousePressed(MouseEvent e) {
			
			JButton b = (JButton)e.getSource();
			if(b == chButton[0]) index = 0;
			else if(b == chButton[1]) index = 1;
			else if(b == chButton[2]) index = 2;
			else if(b == chButton[3]) index = 3;
			
			for(int i=0; i<4; i++) {
				chButton[i].setIcon(new ImageIcon("img/select/selectCh"+i+".png"));
			}

			chButton[index].setIcon(new ImageIcon("img/select/selectedCh"+index+".png"));
			
			cookieimg = new CookieImg(
					new ImageIcon("img/cookieimg/cookie"+index+"/normal.gif").getImage(),
					new ImageIcon("img/cookieimg/cookie"+index+"/jump.gif").getImage(),
					new ImageIcon("img/cookieimg/cookie"+index+"/doublejump.gif").getImage(),
					new ImageIcon("img/cookieimg/cookie"+index+"/fall.png").getImage(),
					new ImageIcon("img/cookieimg/cookie"+index+"/slide.gif").getImage(),
					new ImageIcon("img/cookieimg/cookie"+index+"/hit.gif").getImage());
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension d = getSize();
		g.drawImage(bgImg, 0, 0, d.width,d.height, null);
	}

}