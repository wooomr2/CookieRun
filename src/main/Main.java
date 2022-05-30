package main;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import panels.GamePanel;
import panels.IntroPanel;
import panels.SelectPanel;

public class Main extends listenAdapter {

	private JFrame frame;
	private CardLayout cardLayout = new CardLayout(0,0);
	
	private IntroPanel introPanel = new IntroPanel();
	private SelectPanel selectPanel = new SelectPanel(this);
	private GamePanel gamePanel = new GamePanel(frame, cardLayout, this);
	
	
	// Launch the application //
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public Main() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 500);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().setLayout(cardLayout);
		frame.getContentPane().add(introPanel, "intro");
		frame.getContentPane().add(selectPanel, "select");
		frame.getContentPane().add(gamePanel, "game");
		
		introPanel.addMouseListener(this);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getSource() == introPanel) {
			try {
				Thread.sleep(300);
			} catch(InterruptedException e1) {e1.printStackTrace();} 
			cardLayout.show(frame.getContentPane(), "select");
			selectPanel.requestFocus();
		}
		else if(e.getComponent().toString().contains("StartBtn")) {
			if(selectPanel.getCookieImg() == null) {
				JOptionPane.showMessageDialog(null, "캐릭터를 선택하세요");
			}
			else {
				cardLayout.show(frame.getContentPane(), "game");
				gamePanel.requestFocus();
				gamePanel.gameSet(selectPanel.getCookieImg());
				gamePanel.gameStart();
			}
		}

	}
}