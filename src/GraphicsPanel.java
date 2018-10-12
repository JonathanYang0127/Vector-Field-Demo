import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
//reference to battlecode
import javax.swing.Timer;

public class GraphicsPanel extends JPanel {
	final int WIDTH = 800, HEIGHT = 600;// starting values for width and height in pixels
	public int xrects = 30, yrects = 30, bufferx = 10, buffery = 10, topheight = 50;
	double widthrect, heightrect;
	private JButton runButton, setRects, startButton, endButton;
	private JButton leftButton, rightButton, automatic, stop;
	private JButton wallButton, barrier, resetparticles;
	public boolean[][] grid;
	int pathwayindex = -1, pathwaysize = 0;
	public ArrayList<ArrayList<Integer>> xpathway, ypathway;
	ArrayList<Integer> used;
	public boolean usedcheck = false;
	public Timer autotimer;
	public boolean[][] particles, mark;
	public boolean stepmark[][];
	public boolean particleplaced = false, move = false;
	public boolean reset = true, tempmove = false;
	public int[] dirx = { 1, 1, 0, -1, -1, -1, 0, 1 };
	public int[] diry = { 0, 1, 1, 1, 0, -1, -1, -1 };
	public int[][] store;
	public int mode, endx, endy, prevx, prevy;
	public int goalnumber = 0;
	public int lastbutton = 0;

	public GraphicsPanel() {
		this.setLayout(null);
		grid = new boolean[xrects][yrects];
		mark = new boolean[xrects][yrects];
		particles = new boolean[xrects][yrects];
		stepmark = new boolean[xrects][yrects];
		store = new int[xrects][yrects];
		for (int x = 0; x < xrects; x++) {
			for (int y = 0; y < yrects; y++) {
				store[x][y] = -1;
			}
		}
		endx = -1;
		endy = -1;
		prevx = -1;
		prevy = -1;
		setPreferredSize(new Dimension(WIDTH, HEIGHT + topheight + 30));
		widthrect = (double) (WIDTH - 2 * bufferx) / (double) xrects;
		heightrect = (double) (HEIGHT - 2 * buffery) / (double) yrects;
		initializeButtons();
		initializeMouse();
		initializeTimer();
		repaint();
	}

	private void initializeTimer() {
		// TODO Auto-generated method stub
		autotimer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (lastbutton == 0) {
					updateMovement();
				} else {
					backMovement();
				}
				if (tempmove == false || (pathwayindex == -1 && lastbutton == 1)) {
					lastbutton = 0;
					autotimer.stop();
				}
				repaint();
			}
		});
	}

	private void initializeMouse() {
		// TODO Auto-generated method stub
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				if ((e.getX() > bufferx && e.getX() < ((int) widthrect * xrects + bufferx))
						&& (e.getY() > topheight + buffery
								&& e.getY() < ((int) heightrect) * yrects + buffery + topheight)) {
					reset = false;
					int tx = (int) ((double) (e.getX() - bufferx) / widthrect),
							ty = (int) ((double) (e.getY() - buffery - topheight) / heightrect);
					if (prevx != tx || prevy != ty) {
						if (mode == 0) {
							grid[tx][ty] = !grid[tx][ty];
						} else if (mode == 1 && grid[tx][ty] == false) {
							endx = tx;
							endy = ty;
						} else if (mode == 2 && move == false && store[tx][ty] != -1) {
							particles[tx][ty] = !particles[tx][ty];
						}
						prevx = tx;
						prevy = ty;
					}
					repaint();
				}
			}
		});
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if ((e.getX() > bufferx && e.getX() < ((int) widthrect * xrects + bufferx))
						&& (e.getY() > topheight + buffery
								&& e.getY() < ((int) heightrect) * yrects + buffery + topheight)) {
					reset = false;
					int tx = (int) ((double) (e.getX() - bufferx) / widthrect),
							ty = (int) ((double) (e.getY() - buffery - topheight) / heightrect);
					if (mode == 0) {
						grid[tx][ty] = !grid[tx][ty];
					} else if (mode == 1 && grid[tx][ty] == false) {
						endx = tx;
						endy = ty;
					} else if (mode == 2 && move == false && store[tx][ty] != -1) {
						particles[tx][ty] = !particles[tx][ty];
					}
					prevx = tx;
					prevy = ty;
					repaint();
				}
			}
		});
	}

	private void initializeButtons() {
		// TODO Auto-generated method stub
		runButton = new JButton();
		setRects = new JButton();
		runButton.setText("Run");
		setRects.setText("Reset");
		runButton.setBounds(700, 15, 80, 33);
		setRects.setBounds(600, 15, 80, 33);
		startButton = new JButton();
		endButton = new JButton();
		startButton.setText("Paricle");
		endButton.setText("Goal");
		startButton.setBounds(300, 15, 120, 33);
		endButton.setBounds(160, 15, 120, 33);
		startButton.setBackground(Color.WHITE);
		endButton.setBackground(Color.WHITE);
		startButton.setOpaque(true);
		endButton.setOpaque(true);
		startButton.setBorderPainted(false);
		endButton.setBorderPainted(false);
		barrier = new JButton();
		barrier.setText("Barrier");
		barrier.setBounds(20, 15, 120, 33);
		barrier.setBackground(Color.GRAY);
		barrier.setOpaque(true);
		barrier.setBorderPainted(false);
		leftButton = new JButton();
		rightButton = new JButton();
		leftButton.setText("<");
		rightButton.setText(">");
		leftButton.setBounds(WIDTH - bufferx - 85, HEIGHT + topheight - 8, 40, 33);
		rightButton.setBounds(WIDTH - bufferx - 45, HEIGHT + topheight - 8, 40, 33);
		automatic = new JButton();
		stop = new JButton();
		automatic.setText("Automatic");
		stop.setText("Stop");
		automatic.setBounds(bufferx, HEIGHT + topheight - 8, 95, 33);
		stop.setBounds(bufferx + 100, HEIGHT + topheight - 8, 95, 33);
		resetparticles = new JButton();
		resetparticles.setText("Reset Particles");
		resetparticles.setBounds(bufferx + 200, HEIGHT + topheight - 8, 120, 33);

		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (endx != -1) {
					generateVectorField();
					startButton.setBackground(Color.GREEN);
					endButton.setBackground(Color.WHITE);
					barrier.setBackground(Color.WHITE);
					prevx = -1;
					prevy = -1;
					mode = 2;
					pathwayindex = -1;
					pathwaysize = 0;
					xpathway = new ArrayList<ArrayList<Integer>>();
					ypathway = new ArrayList<ArrayList<Integer>>();
					for (int x = 0; x < xrects; x++) {
						for (int y = 0; y < yrects; y++) {
							xpathway.add(new ArrayList<Integer>());
							ypathway.add(new ArrayList<Integer>());
						}
					}
				}
			}
		});
		setRects.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int x = 0; x < xrects; x++) {
					for (int y = 0; y < yrects; y++) {
						grid[x][y] = false;
					}
				}
				endx = -1;
				endy = -1;
				prevx = -1;
				prevy = -1;
				goalnumber = 0;
				reset = true;
				move = false;
				lastbutton = 0;
				for (int x = 0; x < xrects; x++) {
					for (int y = 0; y < yrects; y++) {
						mark[x][y] = false;
						store[x][y] = -1;
						particles[x][y] = false;
					}
				}
				mode = 0;
				startButton.setBackground(Color.WHITE);
				barrier.setBackground(Color.GRAY);
				repaint();
			}

		});
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mode == 2) {
					startButton.setBackground(Color.GREEN);
				}
				repaint();
			}
		});
		endButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mode == 2) {
					return;
				}
				mode = 1;
				endButton.setBackground(Color.GRAY);
				barrier.setBackground(Color.WHITE);
				repaint();
			}
		});
		barrier.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mode == 2) {
					return;
				}
				mode = 0;
				barrier.setBackground(Color.GRAY);
				endButton.setBackground(Color.WHITE);
				repaint();
			}
		});
		rightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				autotimer.stop();
				updateMovement();
				repaint();
			}
		});
		leftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				autotimer.stop();
				backMovement();
				repaint();
			}
		});
		resetparticles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				move = false;
				lastbutton = 0;
				goalnumber = 0;
				xpathway = new ArrayList<ArrayList<Integer>>();
				ypathway = new ArrayList<ArrayList<Integer>>();
				for (int x = 0; x < xrects; x++) {
					for (int y = 0; y < yrects; y++) {
						particles[x][y] = false;
						xpathway.add(new ArrayList<Integer>());
						ypathway.add(new ArrayList<Integer>());
					}
				}
				pathwayindex = -1;
				pathwaysize = 0;
				repaint();
			}
		});
		automatic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				autotimer.start();
				repaint();
			}
		});
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				autotimer.stop();
				repaint();
			}
		});
		this.add(runButton);
		this.add(setRects);
		this.add(startButton);
		this.add(endButton);
		this.add(barrier);
		this.add(resetparticles);
		this.add(leftButton);
		this.add(rightButton);
		this.add(automatic);
		this.add(stop);
	}

	private void backMovement() {
		if (mode != 2 || pathwayindex == -1) {
			return;
		}
		used = new ArrayList<Integer>();
		move = true;
		tempmove = true;
		lastbutton = 1;
		ArrayList<Integer> xtemp = new ArrayList<Integer>();
		ArrayList<Integer> ytemp = new ArrayList<Integer>();
		for (int x = 0; x < xrects; x++) {
			for (int y = 0; y < yrects; y++) {
				if (!(x == endx && y == endy) && particles[x][y] == true) {
					xtemp.add(x);
					ytemp.add(y);
				}
			}
		}
		ArrayList<Integer> xans = new ArrayList<Integer>();
		ArrayList<Integer> yans = new ArrayList<Integer>();
		for (int k = 0; k < xtemp.size(); k++) {
			int x = xtemp.get(k), y = ytemp.get(k);
			if (xpathway.get(yrects * x + y).get(pathwayindex) != -1) {
				particles[x][y] = false;
				xans.add(xpathway.get(yrects * x + y).get(pathwayindex));
				yans.add(ypathway.get(yrects * x + y).get(pathwayindex));
			} else {
				particles[x][y] = false;
				xans.add(x);
				yans.add(y);
			}
		}
		if (particles[endx][endy] == true && xpathway.get(yrects * endx + endy).get(pathwayindex) != -1) {
			int goalbackdir = xpathway.get(yrects * endx + endy).get(pathwayindex);
			int count = 0;
			while (goalbackdir != 0) {
				if (goalbackdir % 2 == 1) {
					xans.add(endx + dirx[count]);
					yans.add(endy + diry[count]);
					goalnumber--;
				}
				count++;
				goalbackdir /= 2;
			}
		}
		for (int k = 0; k < xans.size(); k++) {
			particles[xans.get(k)][yans.get(k)] = true;
		}
		if (goalnumber == 0) {
			particles[endx][endy] = false;
		}
		pathwayindex = pathwayindex != -1 ? pathwayindex - 1 : pathwayindex;
	}

	private void updateMovement() {
		if (mode != 2) {
			return;
		}
		move = true;
		lastbutton = 0;
		for (int x = 0; x < xrects; x++) {
			for (int y = 0; y < yrects; y++) {
				stepmark[x][y] = false;
			}
		}
		ArrayList<Integer> xtemp = new ArrayList<Integer>();
		ArrayList<Integer> ytemp = new ArrayList<Integer>();
		for (int x = 0; x < xrects; x++) {
			for (int y = 0; y < yrects; y++) {
				if (particles[x][y] == true && !(x == endx && y == endy)) {
					xtemp.add(x);
					ytemp.add(y);
				}
			}
		}
		ArrayList<Integer> xans = new ArrayList<Integer>();
		ArrayList<Integer> yans = new ArrayList<Integer>();
		if (pathwayindex == pathwaysize - 1) {
			tempmove = false;
			for (int i = 0; i < xtemp.size(); i++) {
				int x = xtemp.get(i);
				int y = ytemp.get(i);
				if (!(x == endx && y == endy) && particles[x][y] == true) {
					if (x - dirx[store[x][y]] == endx && y - diry[store[x][y]] == endy) {
						if (xpathway.get(yrects * endx + endy).size() != pathwaysize + 1) {
							xpathway.get(yrects * endx + endy).add((int) Math.pow(2, store[x][y]));
							ypathway.get(yrects * endx + endy).add((int) Math.pow(2, store[x][y]));
						} else {
							xpathway.get(yrects * endx + endy).set(xpathway.get(yrects * endx + endy).size() - 1,
									xpathway.get(yrects * endx + endy)
											.get(xpathway.get(yrects * endx + endy).size() - 1)
											+ (int) Math.pow(2, store[x][y]));
						}
						particles[x - dirx[store[x][y]]][y - diry[store[x][y]]] = true;
						particles[x][y] = false;
						goalnumber++;
						tempmove = true;
					} else if (particles[x - dirx[store[x][y]]][y - diry[store[x][y]]] == false) {
						particles[x - dirx[store[x][y]]][y - diry[store[x][y]]] = true;
						xpathway.get(yrects * (x - dirx[store[x][y]]) + y - diry[store[x][y]]).add(x);
						ypathway.get(yrects * (x - dirx[store[x][y]]) + y - diry[store[x][y]]).add(y);
						particles[x][y] = false;
						tempmove = true;
					}
				}
			}
			if (tempmove == true) {
				pathwayindex++;
				pathwaysize++;
				for (int x = 0; x < xrects; x++) {
					for (int y = 0; y < yrects; y++) {
						if (xpathway.get(yrects * x + y).size() != pathwaysize) {
							xpathway.get(yrects * x + y).add(-1);
							ypathway.get(yrects * x + y).add(-1);
						}
					}
				}
			}
		} else {
			pathwayindex++;
			for (int i = 0; i < xtemp.size(); i++) {
				int x = xtemp.get(i);
				int y = ytemp.get(i);
				if (!(x == endx && y == endy) && !(x - dirx[store[x][y]] == endx && y - diry[store[x][y]] == endy)
						&& particles[x][y] == true
						&& particles[x - dirx[store[x][y]]][y - diry[store[x][y]]] == false) {
					particles[x - dirx[store[x][y]]][y - diry[store[x][y]]] = true;
					particles[x][y] = false;
					tempmove = true;
				} else if (x - dirx[store[x][y]] == endx && y - diry[store[x][y]] == endy) {
					particles[x - dirx[store[x][y]]][y - diry[store[x][y]]] = true;
					particles[x][y] = false;
					tempmove = true;
					goalnumber++;
				}
			}
		}
	}

	private boolean valid(int a, int b) {
		return a < xrects && b < yrects && a >= 0 && b >= 0 && !grid[a][b] && !mark[a][b];
	}

	private void generateVectorField() {
		// TODO Auto-generated method stub
		mark[endx][endy] = true;
		ArrayDeque<Integer> xcurr = new ArrayDeque<Integer>();
		ArrayDeque<Integer> ycurr = new ArrayDeque<Integer>();
		xcurr.add(endx);
		ycurr.add(endy);
		while (!xcurr.isEmpty()) {
			int x1 = xcurr.removeLast(), y1 = ycurr.removeLast();
			for (int i = 0; i < 8; i++) {
				if (valid(x1 + dirx[i], y1 + diry[i])) {
					xcurr.push(x1 + dirx[i]);
					ycurr.push(y1 + diry[i]);
					store[x1 + dirx[i]][y1 + diry[i]] = i;
					mark[x1 + dirx[i]][y1 + diry[i]] = true;
				}
			}
		}
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.black);
		for (int x = bufferx; x <= WIDTH - bufferx; x += widthrect) {
			g2d.fillRect(x, buffery + topheight, 1, ((int) heightrect) * yrects);
		}
		for (int y = buffery; y <= HEIGHT - buffery; y += heightrect) {
			g2d.fillRect(bufferx, y + topheight, ((int) widthrect) * xrects, 1);
		}
		for (int x = 0; x < xrects; x++) {
			for (int y = 0; y < yrects; y++) {
				if (grid[x][y] == true && !(x == endx && y == endy)) {
					g2d.fillRect((int) (bufferx + ((int) widthrect) * x),
							(int) (buffery + topheight + ((int) heightrect) * y), (int) widthrect, (int) heightrect);
				}
			}
		}
		if (endx != -1) {
			g2d.setColor(Color.RED);
			g2d.fillRect((int) (bufferx + ((int) widthrect) * endx) + 1,
					(int) (buffery + topheight + ((int) heightrect) * endy) + 1, (int) widthrect - 1,
					(int) heightrect - 1);
		}
		for (int x = 0; x < xrects; x++) {
			for (int y = 0; y < yrects; y++) {
				if (particles[x][y] == true) {
					g2d.setColor(Color.GREEN);
					g2d.fillRect((int) (bufferx + ((int) widthrect) * x) + 1,
							(int) (buffery + topheight + ((int) heightrect) * y) + 1, (int) widthrect - 1,
							(int) heightrect - 1);
				}
			}
		}
		for (int x = 0; x < xrects; x++) {
			for (int y = 0; y < yrects; y++) {
				if (store[x][y] != -1) {
					g2d.setColor(Color.BLACK);
					g2d.drawString(Integer.toString((store[x][y] + 4) % 8),
							(int) (bufferx + ((int) widthrect) * x + widthrect / 2),
							(int) (buffery + topheight + ((int) heightrect) * y + heightrect / 2 + 2));
				}
			}
		}
	}
}
