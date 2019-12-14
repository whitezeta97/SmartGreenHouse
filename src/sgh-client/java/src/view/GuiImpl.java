package view;

import javax.swing.*;

import controller.Controller;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Semaphore;

public class GuiImpl extends Thread implements Gui {
	private static final int SLEEP_TIME = 60;

	private DefaultListModel<String> viewUmidityList;
	private DefaultListModel<String> viewWateringsList;
	private DefaultListModel<String> viewWarningsList;

	private JList<String> umidityJList;
	private JList<String> wateringsJList;
	private JList<String> warningsJList;

	private JLabel manualModeLabel;
	private JLabel isWateringLabel;
	private JLabel currentStateLabel;

	private JLabel lastUpdateFromServerLabel;

	private final Font labelFont = new Font("Calibri", Font.BOLD, 21);
	private final Font listsTitleFont = new Font("Calibri", Font.BOLD, 18);
	private final Font listsFont = new Font("Calibri", Font.BOLD, 17);

	private final Controller controller;
	private volatile Semaphore mutex;

	public GuiImpl(final Controller controller, Semaphore mutex) {

		this.controller = controller;
		this.mutex = mutex;

		this.viewUmidityList = new DefaultListModel<>();
		this.umidityJList = new JList<String>(this.viewUmidityList);
		this.umidityJList.setFont(this.listsFont);

		this.viewWateringsList = new DefaultListModel<>();
		this.wateringsJList = new JList<String>(this.viewWateringsList);
		this.wateringsJList.setFont(this.listsFont);

		this.viewWarningsList = new DefaultListModel<>();
		this.warningsJList = new JList<String>(this.viewWarningsList);
		this.warningsJList.setFont(this.listsFont);

		this.manualModeLabel = new JLabel();
		this.manualModeLabel.setFont(labelFont);
		this.isWateringLabel = new JLabel();
		this.isWateringLabel.setFont(labelFont);
		this.currentStateLabel = new JLabel("SGH STATUS: ---");
		this.currentStateLabel.setFont(labelFont);
		this.lastUpdateFromServerLabel = new JLabel("LAST UPDATE FORM SERVER:");
		this.lastUpdateFromServerLabel.setFont(labelFont);

		JScrollPane jScrollPaneUmidityList = new JScrollPane(this.umidityJList);
		jScrollPaneUmidityList.setAutoscrolls(true);
		JScrollPane jScrollPaneWateringsList = new JScrollPane(this.wateringsJList);
		jScrollPaneWateringsList.setAutoscrolls(true);
		JScrollPane jScrollPaneWarningsList = new JScrollPane(this.warningsJList);
		jScrollPaneWarningsList.setAutoscrolls(true);

		final GridBagLayout gridLayout = new GridBagLayout();
		final JPanel jPanel = new JPanel(gridLayout);
		final GridBagConstraints constr = new GridBagConstraints();

		// constr.gridx = 0;
		// constr.gridy = 0;
		constr.fill = GridBagConstraints.BOTH;
		constr.weightx = 1.0;
		constr.weighty = 1.0;

		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(10, 10, 30, 10);
		constr.gridx = 0;
		constr.gridy = 0;
		this.manualModeLabel.setHorizontalAlignment(JLabel.CENTER);
		jPanel.add(this.manualModeLabel, constr);
		constr.gridx = 1;
		constr.gridy = 0;
		this.isWateringLabel.setHorizontalAlignment(JLabel.CENTER);
		jPanel.add(this.isWateringLabel, constr);
		constr.gridx = 2;
		constr.gridy = 0;
		this.currentStateLabel.setHorizontalAlignment(JLabel.CENTER);
		jPanel.add(this.currentStateLabel, constr);

		constr.weightx = 0.0;
		constr.weighty = 0.0;
		constr.anchor = GridBagConstraints.FIRST_LINE_END;
		constr.insets = new Insets(10, 10, 0, 10);
		constr.gridx = 0;
		constr.gridy = 1;
		final JLabel warningsListTitle = new JLabel("Warnings list:");
		warningsListTitle.setFont(this.listsTitleFont);
		warningsListTitle.setForeground(Color.red);
		jPanel.add(warningsListTitle, constr);

		constr.gridx = 1;
		constr.gridy = 1;
		final JLabel umidityListTitle = new JLabel("Umidity list:");
		umidityListTitle.setFont(this.listsTitleFont);
		jPanel.add(umidityListTitle, constr);

		constr.gridx = 2;
		constr.gridy = 1;
		final JLabel wateringsListTitle = new JLabel("Waterings list:");
		wateringsListTitle.setFont(this.listsTitleFont);
		wateringsListTitle.setForeground(Color.blue);
		jPanel.add(wateringsListTitle, constr);

		constr.weightx = 1.0;
		constr.weighty = 1.0;
		constr.anchor = GridBagConstraints.CENTER;
		constr.insets = new Insets(0, 10, 10, 10);
		constr.gridx = 0;
		constr.gridy = 2;
		jPanel.add(jScrollPaneWarningsList, constr);
		constr.gridx = 1;
		constr.gridy = 2;
		jPanel.add(jScrollPaneUmidityList, constr);
		constr.gridx = 2;
		constr.gridy = 2;
		jPanel.add(jScrollPaneWateringsList, constr);

		constr.gridx = 0;
		constr.gridy = 3;
		constr.gridwidth = 3;
		constr.insets = new Insets(40, 10, 10, 10);
		this.lastUpdateFromServerLabel.setHorizontalAlignment(JLabel.CENTER);
		jPanel.add(this.lastUpdateFromServerLabel, constr);

		jPanel.setVisible(true);

		final JFrame jf = new JFrame();
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
		jf.setTitle("Smart Green House Front-end");
		jf.setVisible(true);

		this.start();
	}

	private <E> void manageViewListsProperties(final JList<E> jList) {

		final int lastIndex = this.umidityJList.getModel().getSize();

		if (lastIndex > 0) {
			jList.ensureIndexIsVisible(lastIndex);
			// jList.setSelectedIndex(lastIndex);
		}
	}

	private void manageListsData() {
		this.controller.getDataForView().getUmidityValuesList().stream()
				.map(i -> i.getX().toString() + " on " + i.getY().toString())
				.filter(i -> !this.umidityJList.getModel().toString().contains(i)).iterator()
				.forEachRemaining(i -> this.viewUmidityList.addElement(i));

		this.controller.getDataForView().getWateringsList().stream()
				.map(i -> "Duration: " + i.getX() + " s  on " + i.getY().toString())
				.filter(i -> !this.wateringsJList.getModel().toString().contains(i)).iterator()
				.forEachRemaining(i -> this.viewWateringsList.addElement(i));

		this.controller.getDataForView().getWarningsList().stream()
				.filter(i -> !this.warningsJList.getModel().toString().contains(i)).iterator()
				.forEachRemaining(i -> this.viewWarningsList.addElement(i));

		this.manageViewListsProperties(this.umidityJList);
		this.manageViewListsProperties(this.wateringsJList);
		this.manageViewListsProperties(this.warningsJList);

	}

	private void manageViewStatusLabels() {
		if (this.controller.getDataForView().isManualMode()) {
			this.manualModeLabel.setText("MANUAL MODE: ON");
		} else {
			this.manualModeLabel.setText("MANUAL MODE: OFF");
		}

		if (this.controller.getDataForView().isWatering()) {
			this.isWateringLabel.setText("WATERING STATUS: ON");
		} else {
			this.isWateringLabel.setText("WATERING STATUS: OFF");
		}

		if (this.controller.getDataForView().getCurrentState() != null) {
			this.currentStateLabel.setText("SGHR STATUS: " + this.controller.getDataForView().getCurrentState());
		}

		if (this.controller.getDataForView().getLastUpdateFromServer() != null) {
			this.lastUpdateFromServerLabel
					.setText("LAST UPDATE FROM SERVER: " + this.controller.getDataForView().getLastUpdateFromServer());
		} else {
			this.lastUpdateFromServerLabel.setText("LAST UPDATE FROM SERVER: ---");
		}

	}

	private void viewUpdate() {
		this.manageListsData();
		this.manageViewStatusLabels();

	}

	@Override
	public void run() {
		while (true) {
			try {
				this.mutex.acquire();
				this.viewUpdate();
				this.mutex.release();
			} catch (InterruptedException e) {
				this.mutex.release();
				e.printStackTrace();
			}

			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

}
