package panels;

import java.awt.AlphaComposite;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ingame.Background;
import ingame.Cookie;
import ingame.CookieImg;
import ingame.Field;
import ingame.Jelly;
import ingame.Tackle;
import main.Main;
import util.Util;

public class GamePanel extends JPanel {

	private int gameSpeed = 5;
	private int distance = 0;
	private int mapLength = 0;
	private int runPage = 0; // 한 화면 이동할때마다 체력을 깎기 위한 변수
	private int resultScore = 0; // 결과점수를 수집하는 변수
	private int nowField = 2000; // 발판의 높이를 저장.
	
	private boolean isFadeOut = false;
	private boolean escKeyOn = false;
	private boolean downKeyOn = false;
	private boolean redScreen = false;
	
	private int[] sizeArr;
	private int[][] colorArr;

//	private List<Integer> mapLengthList = new ArrayList<>(); // 누적맵길이 리스트
	private List<Jelly> jellyList = new ArrayList<>(); // 젤리 리스트
	private List<Field> fieldList = new ArrayList<>(); // 발판 리스트
	private List<Tackle> tackleList = new ArrayList<>(); // 장애물 리스트

	private Background b11; // 배경1-1 오브젝트
	private Background b12; // 배경1-2 오브젝트
	private Background b21; // 배경2-1 오브젝트
	private Background b22; // 배경2-2 오브젝트

	private ImageIcon lifeBar; //점프 슬라이드 관련
	private ImageIcon redBg;
	private ImageIcon jumpButtonIconUp;
	private ImageIcon jumpButtonIconDown;
	private ImageIcon slideIconUp;
	private ImageIcon slideIconDown;
	private Image jumpBtn; 
	private Image slideBtn;
	
	private Image normalImg; // 쿠키 상태별 이미지
	private Image jumpImg;
	private Image doubleJumpImg;
	private Image fallImg;
	private Image slideImg;
	private Image hitImg;

	private Cookie c1; // 쿠키 오브젝트
	private int face; // 쿠키의 정면
	private int foot; // 쿠키의 발

	
	private JButton escButton;

	private Color FadeOutColor = new Color(0, 0, 0, 0);

	private Image buffImage; // 더블버퍼 이미지
	private Graphics buffg; // 더블버퍼 g
	private AlphaComposite alphaComposite; // 투명도 관련 오브젝트

	// 외부 클래스
	private JFrame frame;
	private CardLayout cardLayout;
	private Main main;

	public GamePanel(JFrame frame, CardLayout cardLayout, Object o) {
		this.frame = frame;
		this.cardLayout = cardLayout;
		this.main = (Main) o;
		
		// 일시정지 버튼
				escButton = new JButton("back");
				escButton.setBounds(350, 200, 100, 30);
				escButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						remove(escButton);
						escKeyOn = false;
					}
				});
	}

	public void gameSet(CookieImg cookieImg) {
		setLayout(null);
		setFocusable(true);
		setListener();
		setObject();
		setCookieImg(cookieImg);
		initMap(1);
	}

	public void gameStart() {	
		runRepaint();
		mapMove();
		fall();
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		// 더블버퍼는 그림을 미리그려놓고 화면에 출력한다.
		// 더블버퍼 관련
		if (buffg == null) {
			buffImage = createImage(this.getWidth(), this.getHeight());
			if (buffImage == null) {
				System.out.println("더블 버퍼링용 오프 스크린 생성 실패");
			} else {
				buffg = buffImage.getGraphics();
			}
		}

		Graphics2D g2 = (Graphics2D) buffg;

		super.paintComponent(buffg);

		// 배경이미지를 그린다
		buffg.drawImage(b11.getImage(), b11.getX(), 0, b11.getWidth(), b11.getHeight() * 5 / 4, null);
		buffg.drawImage(b12.getImage(), b12.getX(), 0, b12.getWidth(), b12.getHeight() * 5 / 4, null);
		buffg.drawImage(b21.getImage(), b21.getX(), 0, b21.getWidth(), b21.getHeight() * 5 / 4, null);
		buffg.drawImage(b22.getImage(), b22.getX(), 0, b22.getWidth(), b22.getHeight() * 5 / 4, null);

		// 스테이지 넘어갈시 페이드아웃 효과
		if (isFadeOut) {
			buffg.setColor(FadeOutColor);
			buffg.fillRect(0, 0, this.getWidth(), this.getHeight());
		}

		// 발판 그리기
		for (int i = 0; i < fieldList.size(); i++) {

			Field tempField = fieldList.get(i);

			if (tempField.getX() > -90 && tempField.getX() < 810) { // x값이 -90~810인 객체들만 그린다.

				buffg.drawImage(tempField.getImage(), tempField.getX(), tempField.getY(), tempField.getWidth(),
						tempField.getHeight(), null);
			}
		}

		// 젤리 그리기
		for (int i = 0; i < jellyList.size(); i++) {

			Jelly tempJelly = jellyList.get(i);

			if (tempJelly.getX() > -90 && tempJelly.getX() < 810) {

				alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
						(float) tempJelly.getAlpha() / 255);
				g2.setComposite(alphaComposite);

				buffg.drawImage(tempJelly.getImage(), tempJelly.getX(), tempJelly.getY(), tempJelly.getWidth(),
						tempJelly.getHeight(), null);

				// alpha값을 되돌린다
				alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 255 / 255);
				g2.setComposite(alphaComposite);
			}
		}

		// 장애물 그리기
		for (int i = 0; i < tackleList.size(); i++) {

			Tackle tempTackle = tackleList.get(i);

			if (tempTackle.getX() > -90 && tempTackle.getX() < 810) {

				buffg.drawImage(tempTackle.getImage(), tempTackle.getX(), tempTackle.getY(), tempTackle.getWidth(),
						tempTackle.getHeight(), null);
			}
		}

		if (c1.isInvincible()) { // 무적상태일 경우
			// 쿠키의 alpha값을 받아온다
			alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) c1.getAlpha() / 255);
			g2.setComposite(alphaComposite);

			// 쿠키를 그린다
			buffg.drawImage(c1.getImage(), c1.getX() - 110, c1.getY() - 170,
					normalImg.getWidth(null) * 8 / 10, normalImg.getHeight(null) * 8 / 10, null);

			// alpha값을 되돌린다
			alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 255 / 255);
			g2.setComposite(alphaComposite);

		} else { // 무적상태가 아닐 경우

			// 쿠키를 그린다
			buffg.drawImage(c1.getImage(), c1.getX() - 110, c1.getY() - 170,
					normalImg.getWidth(null) * 8 / 10, normalImg.getHeight(null) * 8 / 10, null);
		}

		// 피격시 붉은 화면
		if (redScreen) {

			alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 125 / 255);
			g2.setComposite(alphaComposite);

			buffg.drawImage(redBg.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);

			alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 255 / 255);
			g2.setComposite(alphaComposite);
		}

		// 점수를 그린다
		Util.drawFancyString(g2, Integer.toString(resultScore), 600, 58, 30, Color.WHITE);

		// 체력게이지를 그린다
		buffg.drawImage(lifeBar.getImage(), 20, 30, null);
		buffg.setColor(Color.BLACK);
		buffg.fillRect(84 + (int) (470 * ((double) c1.getHealth() / 1000)), 65,
				1 + 470 - (int) (470 * ((double) c1.getHealth() / 1000)), 21);

		// 버튼을 그린다
		buffg.drawImage(jumpBtn, 0, 360, 132, 100, null);
		buffg.drawImage(slideBtn, 650, 360, 132, 100, null);

		if (escKeyOn) { // escKey를 누를경우 화면을 흐리게 만든다

			// alpha값을 반투명하게 만든다
			alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 100 / 255);
			g2.setComposite(alphaComposite);

			buffg.setColor(Color.BLACK);

			buffg.fillRect(0, 0, 850, 550);

			// alpha값을 되돌린다
			alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 255 / 255);
			g2.setComposite(alphaComposite);
		}

		// 버퍼이미지를 화면에 출력
		g.drawImage(buffImage, 0, 0, this);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	private void runRepaint() {
		// 리페인트 전용 쓰레드
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					repaint();

					if (escKeyOn) { // escKey를 누를경우 리페인트를 멈춘다
						while (escKeyOn) {
							try {
								Thread.sleep(10);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					try {
						Thread.sleep(10);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void setListener() {
		addKeyListener(new KeyAdapter() { // 키 리스너 추가

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { // escKey를 눌렀을 때
					if (!escKeyOn) {
						escKeyOn = true;
						add(escButton);
						repaint(); // 화면을 어둡게 하기위한 리페인트
					} else {
						remove(escButton);
						escKeyOn = false;
					}
				}

				if (!escKeyOn) {
					if (e.getKeyCode() == KeyEvent.VK_SPACE) {// 스페이스 키를 누르고 더블점프가 2가 아닐때
						jumpBtn = jumpButtonIconDown.getImage();
						if (c1.getCountJump() < 2) {
							jump(); // 점프 메서드 가동
						}
					}
					if (e.getKeyCode() == KeyEvent.VK_DOWN) { // 다운키를 눌렀을 때
						slideBtn = slideIconDown.getImage();
						downKeyOn = true; // downKeyOn 변수를 true로

						if (c1.getImage() != slideImg // 쿠키이미지가 슬라이드 이미지가 아니고
								&& !c1.isJump() // 점프 중이 아니며
								&& !c1.isFall()) { // 낙하 중도 아닐 때

							c1.setImage(slideImg); // 이미지를 슬라이드이미지로 변경

						}
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_DOWN) { // 다운키를 뗐을 때
					slideBtn = slideIconUp.getImage();
					downKeyOn = false; // downKeyOn 변수를 false로

					if (c1.getImage() != normalImg // 쿠키이미지가 기본이미지가 아니고
							&& !c1.isJump() // 점프 중이 아니며
							&& !c1.isFall()) { // 낙하 중도 아닐 때

						c1.setImage(normalImg); // 이미지를 기본이미지로 변경
					}
				}

				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					jumpBtn = jumpButtonIconUp.getImage();
				}
			}
		});
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void setObject() {
		// 체력바
		lifeBar = new ImageIcon("img/object/common/lifeBar2.png");
		// 피격시 붉은화면
		redBg = new ImageIcon("img/object/common/redBg.png");
		// 점프아이콘
		jumpButtonIconUp = new ImageIcon("img/object/common/jump1.png");
		jumpButtonIconDown = new ImageIcon("img/object/common/jump2.png");
		// 슬라이드아이콘
		slideIconUp = new ImageIcon("img/object/common/slide1.png");
		slideIconDown = new ImageIcon("img/object/common/slide2.png");

		jumpBtn = jumpButtonIconUp.getImage();
		slideBtn = slideIconUp.getImage();

		// 쿠키 인스턴스 생성 / 기본 자료는 클래스안에 내장 되어 있기 때문에 이미지만 넣었다.
		c1 = new Cookie(normalImg);
		// 쿠키의 정면 위치 / 쿠키의 x값과 높이를 더한 값
		face = c1.getX() + c1.getWidth();

		// 쿠키의 발밑 위치 / 쿠키의 y값과 높이를 더한 값
		foot = c1.getY() + c1.getHeight();
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	private void setCookieImg(CookieImg cookieimg) {
		// 쿠키 상태별 이미지
		normalImg = cookieimg.getNormal(); // 기본모션
		jumpImg = cookieimg.getJump(); // 점프모션
		doubleJumpImg = cookieimg.getDoubleJump(); // 더블점프모션
		fallImg = cookieimg.getFall(); // 낙하모션(더블 점프 후)
		slideImg = cookieimg.getSlide(); // 슬라이드 모션
		hitImg = cookieimg.getHit(); // 부딛히는 모션
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void initMap(int stage) {

		jellyList.clear();
		fieldList.clear();
		tackleList.clear();

		b11 = new Background(stage, 1, 1);
		b12 = new Background(stage, 1, 2);
		b21 = new Background(stage, 2, 1);
		b22 = new Background(stage, 2, 2);

		String mapsrc = "img/map/map" + stage + ".png";

		try {
			sizeArr = Util.getSize(mapsrc); // 맵 사이즈를 배열에 저장
			colorArr = Util.getPic(mapsrc); // 맵 픽셀값을 배열에 저장
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		int mapLength = sizeArr[0];
		int mapHeight = sizeArr[1];

		for (int i = 0; i < mapLength; i++) {
			for (int j = 0; j < mapHeight; j++) {
				switch (colorArr[i][j]) {
				case 16776960:
					jellyList.add(new Jelly(1, i * 40, j * 40, 30, 30, 255, 1234));
					break;
				case 13158400:
					jellyList.add(new Jelly(2, i * 40, j * 40, 30, 30, 255, 2345));
					break;
				case 9868800:
					jellyList.add(new Jelly(3, i * 40, j * 40, 30, 30, 255, 3456));
					break;
				case 16737280:
					jellyList.add(new Jelly("HP", i * 40, j * 40, 30, 30, 255, 4567));
					break;
				}
			}
		}

		for (int i = 0; i < mapLength; i += 2) { // 발판은 4칸을 차지하는 공간이기 때문에 2,2사이즈로 반복문을 돌린다.
			for (int j = 0; j < mapHeight; j += 2) {
				if (colorArr[i][j] == 0) { // 0(검은색), 좌표에 40 곱하고, 사이즈 80x80
					fieldList.add(new Field(stage, 1, i * 40, j * 40, 80, 80));
				} else if (colorArr[i][j] == 6579300) { // 6579300(회색), 좌표에 40 곱하고, 사이즈 80x80
					fieldList.add(new Field(stage, 2, i * 40, j * 40, 80, 80));
				}
			}
		}

		for (int i = 0; i < mapLength; i += 2) {
			for (int j = 0; j < mapHeight; j += 2) {
				if (colorArr[i][j] == 16711680) { // 16711680(빨간색) 80x80
					tackleList.add(new Tackle(stage, 1, i * 40, j * 40, 80, 80, 0));

				} else if (colorArr[i][j] == 16711830) { // 16711830(분홍) 80x160
					tackleList.add(new Tackle(stage, 2, i * 40, j * 40, 80, 160, 0));

				} else if (colorArr[i][j] == 16711935) { // 16711830(핫핑크) 80x240
					tackleList.add(new Tackle(stage, 3, i * 40, j * 40, 80, 240, 0));
				}
			}
		}
		this.mapLength = mapLength;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	private void mapMove() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				int stage = 2;

				while (true) {
					
					if (runPage > 800) { // 800픽셀 이동 마다 체력이 10씩 감소한다 (추후 맵길이에 맟추어 감소량 조절)
						c1.setHealth(c1.getHealth() - 10);
						runPage = 0;
					}

					runPage += gameSpeed; // 화면이 이동하면 runPage에 이동한 만큼 저장된다.
					distance += gameSpeed;
					
					foot = c1.getY() + c1.getHeight(); // 캐릭터 발 위치 재스캔
					if (foot > 1999 || c1.getHealth() < 1) { //추락하거나 체력없을시 종료
						System.exit(0);
					}
					

					// FadeIn스레드가 종료할때까지 새 FadeOut스레드 생성x
					if (isFadeOut == false && distance == (mapLength - gameSpeed * 4) * 40) {
						isFadeOut = true;
						new Thread(new FadeOut()).start();
					}

					if (distance > mapLength * 40) {
						if (stage >= 5)
							break;
						initMap(stage);
						repaint();
						distance = 0;
						stage++;
						new Thread(new FadeIn()).start();
						; // FadeIn스레드 종료 후 isFadeOut = false로 변경
					}

					if (b11.getX() < -(b11.getWidth() - 5)) // 배경1-1 이 -(배경넓이)보다 작으면, 즉 화면밖으로 모두나가면 배경 1-2뒤에 붙음
						b11.setX(b11.getWidth());
					if (b12.getX() < -(b12.getWidth() - 5)) // 배경1-2 가 -(배경넓이)보다 작으면, 즉 화면밖으로 모두나가면 배경 1-1뒤에 붙음
						b12.setX(b12.getWidth());
					if (b21.getX() < -(b21.getWidth() - 5)) // 배경2-1 이 -(배경넓이)보다 작으면, 즉 화면밖으로 모두나가면 배경 2-2뒤에 붙음
						b21.setX(b21.getWidth());
					if (b22.getX() < -(b22.getWidth() - 5)) // 배경2-2 가 -(배경넓이)보다 작으면, 즉 화면밖으로 모두나가면 배경 2-1뒤에 붙음
						b22.setX(b22.getWidth());

					// 배경1의 x좌표를 -1, 배경2의 x좌표를 -2 이동(왼쪽으로 흐르는 효과)
					b11.setX(b11.getX() - gameSpeed / 3);
					b12.setX(b12.getX() - gameSpeed / 3);
					b21.setX(b21.getX() - gameSpeed * 2 / 3);
					b22.setX(b22.getX() - gameSpeed * 2 / 3);

					// 발판이동
					for (int i = 0; i < fieldList.size(); i++) {
						Field tempField = fieldList.get(i);

						if (tempField.getX() < -90)
							fieldList.remove(tempField); // x 좌표가 -90 미만이면 제거
						else
							tempField.setX(tempField.getX() - gameSpeed);
					}

					// 젤리이동
					for (int i = 0; i < jellyList.size(); i++) {
						Jelly tempJelly = jellyList.get(i);

						if (tempJelly.getX() < -90)
							fieldList.remove(tempJelly);
						else {
							tempJelly.setX(tempJelly.getX() - gameSpeed);
							if (tempJelly.getImage() == tempJelly.getEffectImage() && tempJelly.getAlpha() > 4) {
								tempJelly.setAlpha(tempJelly.getAlpha() - 5);
							}

							foot = c1.getY() + c1.getHeight(); // 캐릭터 발 위치 재스캔

							if ( // 캐릭터의 범위 안에 젤리가 있으면 아이템을 먹는다.
							c1.getImage() != slideImg
									&& tempJelly.getX() + tempJelly.getWidth() * 20 / 100 >= c1.getX()
									&& tempJelly.getX() + tempJelly.getWidth() * 80 / 100 <= face
									&& tempJelly.getY() + tempJelly.getWidth() * 20 / 100 >= c1.getY()
									&& tempJelly.getY() + tempJelly.getWidth() * 80 / 100 <= foot
									&& tempJelly.getImage() != tempJelly.getEffectImage()) {

								if (tempJelly.getImage() == tempJelly.getHPImage()) {
									if ((c1.getHealth() + 100) > 1000) {
										c1.setHealth(1000);
									} else {
										c1.setHealth(c1.getHealth() + 100);
									}
								}
								tempJelly.setImage(tempJelly.getEffectImage()); // 젤리의 이미지를 이펙트로 바꾼다
								resultScore = resultScore + tempJelly.getScore(); // 총점수에 젤리 점수를 더한다

							} else if ( // 슬라이딩 하는 캐릭터의 범위 안에 젤리가 있으면 아이템을 먹는다.
							c1.getImage() == slideImg
									&& tempJelly.getX() + tempJelly.getWidth() * 20 / 100 >= c1.getX()
									&& tempJelly.getX() + tempJelly.getWidth() * 80 / 100 <= face
									&& tempJelly.getY() + tempJelly.getWidth() * 20 / 100 >= c1.getY()
											+ c1.getHeight() * 1 / 3
									&& tempJelly.getY() + tempJelly.getWidth() * 80 / 100 <= foot
									&& tempJelly.getImage() != tempJelly.getEffectImage()) {

								if (tempJelly.getImage() == tempJelly.getHPImage()) {
									if ((c1.getHealth() + 100) > 1000) {
										c1.setHealth(1000);
									} else {
										c1.setHealth(c1.getHealth() + 100);
									}
								}
								tempJelly.setImage(tempJelly.getEffectImage()); // 젤리의 이미지를 이펙트로 바꾼다
								resultScore = resultScore + tempJelly.getScore(); // 총점수에 젤리 점수를 더한다

							}

						}
					}

					// 장애물 이동
					for (int i = 0; i < tackleList.size(); i++) {
						Tackle tempTackle = tackleList.get(i);

						if (tempTackle.getX() < -90)
							fieldList.remove(tempTackle);
						else {
							tempTackle.setX(tempTackle.getX() - gameSpeed);

							face = c1.getX() + c1.getWidth(); // 캐릭터 정면 위치 재스캔
							foot = c1.getY() + c1.getHeight(); // 캐릭터 발 위치 재스캔

							if ( // 무적상태가 아니고 슬라이드 중이 아니며 캐릭터의 범위 안에 장애물이 있으면 부딛힌다
							!c1.isInvincible() && c1.getImage() != slideImg
									&& tempTackle.getX() + tempTackle.getWidth() / 2 >= c1.getX()
									&& tempTackle.getX() + tempTackle.getWidth() / 2 <= face
									&& tempTackle.getY() + tempTackle.getHeight() / 2 >= c1.getY()
									&& tempTackle.getY() + tempTackle.getHeight() / 2 <= foot) {

								hit(); // 피격 + 무적 쓰레드 메서드

							} else if ( // 슬라이딩 아닐시 공중장애물
							!c1.isInvincible() && c1.getImage() != slideImg
									&& tempTackle.getX() + tempTackle.getWidth() / 2 >= c1.getX()
									&& tempTackle.getX() + tempTackle.getWidth() / 2 <= face
									&& tempTackle.getY() <= c1.getY()
									&& tempTackle.getY() + tempTackle.getHeight() * 95 / 100 > c1.getY()) {

								hit(); // 피격 + 무적 쓰레드 메서드

							} else if ( // 무적상태가 아니고 슬라이드 중이며 캐릭터의 범위 안에 장애물이 있으면 부딛힌다
							!c1.isInvincible() && c1.getImage() == slideImg
									&& tempTackle.getX() + tempTackle.getWidth() / 2 >= c1.getX()
									&& tempTackle.getX() + tempTackle.getWidth() / 2 <= face
									&& tempTackle.getY() + tempTackle.getHeight() / 2 >= c1.getY()
											+ c1.getHeight() * 2 / 3
									&& tempTackle.getY() + tempTackle.getHeight() / 2 <= foot) {

								hit(); // 피격 + 무적 쓰레드 메서드

							} else if ( // 슬라이딩시 공중장애물
							!c1.isInvincible() && c1.getImage() == slideImg
									&& tempTackle.getX() + tempTackle.getWidth() / 2 >= c1.getX()
									&& tempTackle.getX() + tempTackle.getWidth() / 2 <= face
									&& tempTackle.getY() < c1.getY() && tempTackle.getY()
											+ tempTackle.getHeight() * 95 / 100 > c1.getY() + c1.getHeight() * 2 / 3) {

								hit(); // 피격 + 무적 쓰레드 메서드
							}
						}
					}

					// 쿠키가 밟을 발판을 계산하는 코드
					int tempField; // 발판위치를 계속 스캔하는 지역변수
					int tempNowField; // 캐릭터와 발판의 높이에 따라 저장되는 지역변수, 결과를 nowField에 저장한다

					// 쿠키가 무적상태라면 낙사 하지 않기 때문에 400으로 세팅 / 무적이 아니라면 2000(낙사지점);
					if (c1.isInvincible()) {
						tempNowField = 400;
					} else {
						tempNowField = 2000;
					}

					for (int i = 0; i < fieldList.size(); i++) { // 발판의 개수만큼 반복

						int tempX = fieldList.get(i).getX(); // 발판의 x값

						if (tempX > c1.getX() - 60 && tempX <= face) { // 발판이 캐릭 범위 안이라면

							tempField = fieldList.get(i).getY(); // 발판의 y값을 tempField에 저장한다

							foot = c1.getY() + c1.getHeight(); // 캐릭터 발 위치 재스캔

							// 발판위치가 tempNowField보다 높고, 발바닥 보다 아래 있다면
							// 즉, 캐릭터 발 아래에 제일 높이 있는 발판이라면 tempNowField에 저장한다.
							if (tempField < tempNowField && tempField >= foot) {

								tempNowField = tempField;

							}
						}
					}

					nowField = tempNowField; // 결과를 nowField에 업데이트 한다.

					if (escKeyOn) { // escKey를 누르면 게임이 멈춘다
						while (escKeyOn) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		}).start();
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void hit() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				c1.setInvincible(true); // 쿠키를 무적상태로 전환

				System.out.println("피격무적시작");

				redScreen = true; // 피격 붉은 이펙트 시작

				c1.setHealth(c1.getHealth() - 100); // 쿠키의 체력을 100 깎는다

				c1.setImage(hitImg); // 쿠키를 부딛힌 모션으로 변경

				c1.setAlpha(80); // 쿠키의 투명도를 80으로 변경

				try { // 0.5초 대기
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				redScreen = false; // 피격 붉은 이펙트 종료

				try { // 0.5초 대기
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (c1.getImage() == hitImg) { // 0.5초 동안 이미지가 바뀌지 않았다면 기본이미지로 변경

					c1.setImage(normalImg);

				}

				for (int j = 0; j < 11; j++) { // 2.5초간 캐릭터가 깜빡인다. (피격후 무적 상태를 인식)

					if (c1.getAlpha() == 80) { // 이미지의 알파값이 80이면 160으로

						c1.setAlpha(160);

					} else { // 아니면 80으로

						c1.setAlpha(80);

					}
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				c1.setAlpha(255); // 쿠키의 투명도를 정상으로 변경

				c1.setInvincible(false);
				System.out.println("피격무적종료");
			}
		}).start();
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void fall() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {

					foot = c1.getY() + c1.getHeight(); // 캐릭터 발 위치 재스캔

					// 발바닥이 발판보다 위에 있으면 작동
					if (!escKeyOn // 일시중지가 발동 안됐을 때
							&& foot < nowField // 공중에 있으며
							&& !c1.isJump() // 점프 중이 아니며
							&& !c1.isFall()) { // 떨어지는 중이 아닐 때

						c1.setFall(true); // 떨어지는 중으로 전환
						System.out.println("낙하");

						if (c1.getCountJump() == 2) { // 더블점프가 끝났을 경우 낙하 이미지로 변경
							c1.setImage(fallImg);
						}

						long t1 = Util.getTime(); // 현재시간을 가져온다
						long t2;
						int set = 1; // 처음 낙하량 (0~10) 까지 테스트해보자

						while (foot < nowField) { // 발이 발판에 닿기 전까지 반복

							t2 = Util.getTime() - t1; // 지금 시간에서 t1을 뺀다

							int fallY = set + (int) ((t2) / 40); // 낙하량을 늘린다.

							foot = c1.getY() + c1.getHeight(); // 캐릭터 발 위치 재스캔

							if (foot + fallY >= nowField) { // 발바닥+낙하량 위치가 발판보다 낮다면 낙하량을 조정한다.
								fallY = nowField - foot;
							}

							c1.setY(c1.getY() + fallY); // Y좌표에 낙하량을 더한다

							if (c1.isJump()) { // 떨어지다가 점프를 하면 낙하중지
								break;
							}

							if (escKeyOn) {
								long tempT1 = Util.getTime();
								long tempT2 = 0;
								while (escKeyOn) {
									try {
										Thread.sleep(10);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
								tempT2 = Util.getTime() - tempT1;
								t1 = t1 + tempT2;
							}

							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}
						c1.setFall(false);

						if (downKeyOn // 다운키를 누른상태고
								&& !c1.isJump() // 점프 상태가 아니고
								&& !c1.isFall() // 낙하 상태가 아니고
								&& c1.getImage() != slideImg) { // 쿠키 이미지가 슬라이드 이미지가 아닐 경우

							c1.setImage(slideImg); // 쿠키 이미지를 슬라이드로 변경

						} else if (!downKeyOn // 다운키를 누른상태가 아니고
								&& !c1.isJump() // 점프 상태가 아니고
								&& !c1.isFall() // 낙하 상태가 아니고
								&& c1.getImage() != normalImg) { // 쿠키 이미지가 기본 이미지가 아닐 경우

							c1.setImage(normalImg);
						}

						if (!c1.isJump()) { // 발이 땅에 닿고 점프 중이 아닐 때 더블점프 카운트를 0으로 변경
							c1.setCountJump(0);
						}
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void jump() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				c1.setCountJump(c1.getCountJump() + 1); // 점프 횟수 증가

				int nowJump = c1.getCountJump(); // 이번점프가 점프인지 더블점프인지 저장

				c1.setJump(true); // 점프중으로 변경

				if (c1.getCountJump() == 1) { // 점프 횟수가 1이라면

					System.out.println("점프");
					c1.setImage(jumpImg);

				} else if (c1.getCountJump() == 2) { // 점프 횟수가 2라면

					System.out.println("더블점프");
					c1.setImage(doubleJumpImg);

				}

				long t1 = Util.getTime(); // 현재시간을 가져온다
				long t2;
				int set = 8; // 점프 계수 설정(0~20) 등으로 바꿔보자
				int jumpY = 1; // 1이상으로만 설정하면 된다.(while문 조건 때문)

				while (jumpY >= 0) { // 상승 높이가 0일때까지 반복

					t2 = Util.getTime() - t1; // 지금 시간에서 t1을 뺀다

					jumpY = set - (int) ((t2) / 40); // jumpY 를 세팅한다.

					c1.setY(c1.getY() - jumpY); // Y값을 변경한다.

					if (nowJump != c1.getCountJump()) { // 점프가 한번 더되면 첫번째 점프는 멈춘다.
						break;
					}

					if (escKeyOn) {
						long tempT1 = Util.getTime();
						long tempT2 = 0;
						while (escKeyOn) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						tempT2 = Util.getTime() - tempT1;
						t1 = t1 + tempT2;
					}

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				if (nowJump == c1.getCountJump()) { // 점프가 진짜 끝났을 때를 확인
					c1.setJump(false); // 점프상태를 false로 변경
				}

			}
		}).start();
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private class FadeOut implements Runnable {
		public void run() {

			for (int j = 0; j <= 255; j += 2) {
				FadeOutColor = new Color(0, 0, 0, j);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class FadeIn implements Runnable {
		public void run() {
			for (int k = 255; k >= 0; k -= 5) {
				FadeOutColor = new Color(0, 0, 0, k);
				try {
					Thread.sleep(10);
					repaint();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			isFadeOut = false;
		}
	}

}