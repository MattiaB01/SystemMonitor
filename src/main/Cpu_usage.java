package main;

import javax.swing.*;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Arc2D;
import java.io.File;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class Cpu_usage extends JPanel {

	private final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

	// Valori reali
	private double cpuTarget = 0;
	private double ramTarget = 0;

	// Valori animati
	private double cpuDisplay = 0;
	private double ramDisplay = 0;

	private final int cores = osBean.getAvailableProcessors();
	private final long totalRam = osBean.getTotalPhysicalMemorySize();
	File root = new File("C:\\");
	private long totalSpace = root.getTotalSpace()/1_000_000_000; // dimensione totale in byte
	private double usableSpace,spaceDisplay=0;

	public Cpu_usage() {

		// Lettura dati (1 secondo)
		new Timer(1000, e -> updateTargets()).start();

		// Animazione fluida (~60 FPS)
		new Timer(16, e -> animate()).start();

		setBackground(new Color(18, 18, 18));
		setLayout(null);

	}

	private void updateTargets() {
		double cpu = osBean.getSystemCpuLoad();
		cpuTarget = cpu < 0 ? 0 : cpu * 100;

		long total = osBean.getTotalPhysicalMemorySize();
		long free = osBean.getFreePhysicalMemorySize();
		ramTarget = ((double) (total - free) / total) * 100;

		long freeSpace = root.getFreeSpace()/1_000_000_000; // spazio libero in byte
		usableSpace = ((double)(totalSpace-freeSpace)/totalSpace) * 100; // spazio libero utilizzabile dall’utente
		Cpu_Model data = new Cpu_Model((int)Math.round(cpuTarget),(int)Math.round( ramTarget),(int)Math.round( usableSpace));
		try {
		Mqtt.sendMqtt(data);
		}
		catch (Exception e ) {
			
		}
	}

	private void animate() {
		cpuDisplay += (cpuTarget - cpuDisplay) * 0.15;
		ramDisplay += (ramTarget - ramDisplay) * 0.15;
		spaceDisplay += (usableSpace - spaceDisplay) * 0.15;
		
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		drawGauge(g2, 40, 20, 60, "CPU", cpuDisplay, cores);
		drawGauge(g2, 140, 20, 60, "RAM", ramDisplay, (int) (totalRam / 1_000_000_000));
		drawGauge(g2, 240, 20, 60, "DISK", spaceDisplay, (int) (totalSpace));
		
		

	}

	private void drawGauge(Graphics2D g2, int x, int y, int size, String label, double percent, int extra) {

		int stroke = 6;
		int arcSize = size - stroke;
		int arcX = x + stroke / 2;
		int arcY = y + stroke / 2;

		// Background arc
		g2.setStroke(new BasicStroke(stroke));
		g2.setColor(new Color(60, 60, 60));
		g2.draw(new Arc2D.Double(arcX, arcY, arcSize, arcSize, 90, -360, Arc2D.OPEN));

		// Foreground arc
		g2.setColor(getLoadColor(percent));
		g2.draw(new Arc2D.Double(arcX, arcY, arcSize, arcSize, 90, -360 * percent / 100, Arc2D.OPEN));

		// Percent text
		g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
		g2.setColor(Color.WHITE);
		String value = String.format("%.0f%%", percent);
		FontMetrics fm = g2.getFontMetrics();
		g2.drawString(value, x + (size - fm.stringWidth(value)) / 2, y + size / 2 + 8);

		// Label
		if (label.equals("CPU")) {
			g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
			g2.setColor(new Color(180, 180, 180));
			String labelText = extra > 0 ? label + " (" + extra + " core)" : label;
			g2.drawString(labelText, x + (size - g2.getFontMetrics().stringWidth(labelText)) / 2, y + size + 25);
		}

		else {
			g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
			g2.setColor(new Color(180, 180, 180));
			String labelText = extra > 0 ? label + " (" + extra + " GB)" : label;
			g2.drawString(labelText, x + (size - g2.getFontMetrics().stringWidth(labelText)) / 2, y + size+25 );

		}
	}

	private Color getLoadColor(double percent) {
		if (percent < 50)
			return new Color(0, 200, 120); // Verde
		else if (percent < 75)
			return new Color(255, 170, 0); // Arancione
		else
			return new Color(220, 60, 60); // Rosso
	}

	public static void main(String[] args) {
		try {
			Mqtt.config();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// JFrame
			JFrame frame = new JFrame("Dashboard");
			frame.setSize(350, 130);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			frame.setAlwaysOnTop(true);
			// Pannello dei gauge
			Cpu_usage panel = new Cpu_usage();
			frame.add(panel, BorderLayout.CENTER);

			// Pannello dei controlli
			panel.setLayout(null);

			// Soluzione per static context
			Image icon = new ImageIcon(Cpu_usage.class.getResource("cpu_icon_64x64.png")).getImage();
			frame.setIconImage(icon);
			frame.setIconImage(icon);

			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

			int x = screen.width - frame.getWidth() - 10;
			int y = screen.height - frame.getHeight() - 40;

			frame.setLocation(x, y);
			JButton btn = new JButton("X");
			btn.setFocusPainted(false); // rimuove il tratteggio focus
			btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
			// larghezza e altezza solo quanto serve al testo
			btn.setSize(15, 15);
			btn.setLocation(325,20); // posizione fissa
			btn.setBackground(Color.black);
			btn.setForeground(Color.black);
			// per assicurarsi che lo sfondo venga disegnato
			btn.setOpaque(true);
			// rimuove il tratteggio focus
			btn.setFocusPainted(false);
			panel.add(btn);

			// Azione del pulsante
			btn.addActionListener(e -> System.exit(0));
			frame.setUndecorated(true);

			// --- gestione drag della finestra ---
			final Point[] mouseDownCompCoords = { null };

			panel.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					mouseDownCompCoords[0] = e.getPoint();
				}

				public void mouseReleased(MouseEvent e) {
					mouseDownCompCoords[0] = null;
				}
			});

			panel.addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					Point currCoords = e.getLocationOnScreen();
					frame.setLocation(currCoords.x - mouseDownCompCoords[0].x, currCoords.y - mouseDownCompCoords[0].y);
				}
			});

			frame.setVisible(true);

		});
	}
}
