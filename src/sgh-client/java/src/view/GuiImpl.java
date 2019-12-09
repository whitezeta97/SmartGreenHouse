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

	private volatile List<Pair<Float, String>> umidityValuesList;
	private volatile List<Pair<Long, String>> wateringsList;
	private volatile List<String> warningsList;

	private DefaultListModel<String> viewUmidityList;
	private JList<String> umidityJList;
	private DefaultListModel<String> viewWateringsList;
	private JList<String> wateringsJList;
	private DefaultListModel<String> viewWarningsList;
	private JList<String> warningsJList;

	public GuiImpl() {

		this.umidityValuesList = new LinkedList<>();
		this.wateringsList = new LinkedList<>();
		this.warningsList = new LinkedList<>();

		this.viewUmidityList = new DefaultListModel<>();
		this.umidityJList = new JList<String>(this.viewUmidityList);
		
		this.viewWateringsList = new DefaultListModel<>();
		this.wateringsJList = new JList<String>(this.viewWateringsList);
		
		this.viewWarningsList = new DefaultListModel<>();
		this.warningsJList = new JList<String>(this.viewWarningsList);

		JScrollPane jScrollPaneUmidityList = new JScrollPane(this.umidityJList);
		jScrollPaneUmidityList.setAutoscrolls(true);
		JScrollPane jScrollPaneWateringsList = new JScrollPane(this.wateringsJList);
		jScrollPaneWateringsList.setAutoscrolls(true);
		JScrollPane jScrollPaneWarningsList = new JScrollPane(this.warningsJList);
		jScrollPaneWarningsList.setAutoscrolls(true);

		final BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(10);
		borderLayout.setVgap(10);

		JPanel jPanel = new JPanel(borderLayout);

		jPanel.add(jScrollPaneWarningsList, BorderLayout.EAST);
		jPanel.add(jScrollPaneUmidityList, BorderLayout.CENTER);
		jPanel.add(jScrollPaneWateringsList, BorderLayout.WEST);
		jPanel.add(new JList<>(), BorderLayout.SOUTH);
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

		this.umidityValuesList.stream().map(i -> i.getX().toString() + " at " + i.getY().toString())
				.filter(i -> !this.umidityJList.getModel().toString().contains(i)).iterator()
				.forEachRemaining(i -> this.viewUmidityList.addElement(i));

		int lastIndex = this.umidityJList.getModel().getSize() - 1;
		if (lastIndex >= 0) {
			this.umidityJList.ensureIndexIsVisible(lastIndex);
			this.umidityJList.setSelectedIndex(lastIndex);
		}

		this.wateringsList.stream().map(i -> "duration:" + i.getX() / 60 + "s  " + i.getY().toString())
				.filter(i -> !this.wateringsJList.getModel().toString().contains(i)).iterator()
				.forEachRemaining(i -> this.viewWateringsList.addElement(i));

		lastIndex = this.wateringsJList.getModel().getSize() - 1;
		if (lastIndex >= 0) {
			this.wateringsJList.ensureIndexIsVisible(lastIndex);
			this.wateringsJList.setSelectedIndex(lastIndex);
		}

		this.warningsList.stream().filter(i -> !this.warningsJList.getModel().toString().contains(i)).iterator()
				.forEachRemaining(i -> this.viewWarningsList.addElement(i));

		lastIndex = this.warningsJList.getModel().getSize() - 1;
		if (lastIndex >= 0) {
			this.warningsJList.ensureIndexIsVisible(lastIndex);
			this.warningsJList.setSelectedIndex(lastIndex);
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
