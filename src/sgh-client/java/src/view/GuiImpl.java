package view;

import javax.swing.*;

import utilities.Pair;
import utilities.SghStates;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class GuiImpl implements Gui {

	private volatile boolean manualMode;
	private volatile boolean isWatering;
	private volatile String currentState;

	private volatile List<Pair<Float, String>> umidityValuesList = new LinkedList<>();
	private volatile List<Pair<Long, String>> wateringsList = new LinkedList<>();
	private volatile List<String> warningsList = new LinkedList<>();

	private final DefaultListModel<String> umidityList = new DefaultListModel<>();
	private final JList<String> umidityJList = new JList(umidityList);

	public GuiImpl() {

		final BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(10);
		borderLayout.setVgap(10);

		JPanel jPanel = new JPanel(borderLayout);

		jPanel.add(umidityJList, BorderLayout.NORTH);
		jPanel.add(new JList<>(), BorderLayout.SOUTH);
		jPanel.add(new JButton(" Center "), BorderLayout.CENTER);
		jPanel.add(new JButton(" East "), BorderLayout.EAST);
		jPanel.add(new JButton(" WestWest "), BorderLayout.WEST);
		jPanel.setVisible(true);

		JFrame jf = new JFrame();
		jf.getContentPane().add(jPanel);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		jf.setSize(screenSize.width / 2, screenSize.height / 2);
		jf.setLocation(screenSize.width / 4, screenSize.height / 4);
		// jf.pack();
		jf.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
		jf.setVisible(true);

		this.viewUpdate();
	}

	@Override
	public void viewUpdate() {

		if (this.umidityValuesList.size() > 0) {
			this.umidityJList.clearSelection();
			this.umidityList.clear();
			for (Pair<Float, String> elem : this.umidityValuesList) {
				this.umidityList.addElement(elem.getX().toString() + "   " + elem.getY().toString());
			}

		}

	}

	@Override
	public synchronized void setManualMode(boolean manualMode) {
		this.manualMode = manualMode;
	}

	@Override
	public synchronized void setWatering(boolean isWatering) {
		this.isWatering = isWatering;
	}

	@Override
	public synchronized void setCurrentState(String currentState) {
		this.currentState = currentState;
	}

	@Override
	public synchronized void setUmidityValuesList(List<Pair<Float, String>> umidityValuesList) {
		this.umidityValuesList = umidityValuesList;
	}

	@Override
	public synchronized void setWateringsList(List<Pair<Long, String>> wateringsList) {
		this.wateringsList = wateringsList;
	}

	@Override
	public synchronized void setWarningsList(List<String> warningsList) {
		this.warningsList = warningsList;
	}

}
