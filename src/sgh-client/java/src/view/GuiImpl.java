package view;

import javax.swing.*;

import javafx.stage.PopupWindow.AnchorLocation;
import utilities.Pair;

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
	private volatile List<Pair<Float, String>> wateringsList;
	private volatile List<String> warningsList;
	private volatile Date lastUpdateFromServer;

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

	public GuiImpl() {

		this.umidityValuesList = new LinkedList<>();
		this.wateringsList = new LinkedList<>();
		this.warningsList = new LinkedList<>();

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

		//constr.gridwidth = GridBagConstraints.RELATIVE;
		//constr.gridheight = GridBagConstraints.RELATIVE;
		//constr.fill = GridBagConstraints.HORIZONTAL;
		//constr.weightx = 3.0;
		//constr.weighty = 3.0;

		constr.insets = new Insets(10, 10, 70, 10);
		constr.gridx = 0;
		constr.gridy = 0;
		jPanel.add(this.manualModeLabel, constr);
		constr.gridx = 1;
		constr.gridy = 0;
		jPanel.add(this.isWateringLabel, constr);
		constr.gridx = 2;
		constr.gridy = 0;
		jPanel.add(this.currentStateLabel, constr);

		constr.anchor = GridBagConstraints.LINE_START;
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

		constr.gridx = 1;
		constr.gridy = 3;
		constr.insets = new Insets(40, 10, 10, 10);
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

		this.viewUpdate();
	}

	private <E> void manageViewListsProperties(final JList<E> jList) {

		final int lastIndex = this.umidityJList.getModel().getSize() - 1;

		if (lastIndex >= 0) {
			// jList.ensureIndexIsVisible(lastIndex);
			jList.setSelectedIndex(lastIndex);
		}
	}

	private void manageListsData() {
		this.umidityValuesList.stream().map(i -> i.getX().toString() + " on " + i.getY().toString())
				.filter(i -> !this.umidityJList.getModel().toString().contains(i)).iterator()
				.forEachRemaining(i -> this.viewUmidityList.addElement(i));

		this.wateringsList.stream().map(i -> "Duration: " + i.getX() + " s  on " + i.getY().toString())
				.filter(i -> !this.wateringsJList.getModel().toString().contains(i)).iterator()
				.forEachRemaining(i -> this.viewWateringsList.addElement(i));

		this.warningsList.stream().filter(i -> !this.warningsJList.getModel().toString().contains(i)).iterator()
				.forEachRemaining(i -> this.viewWarningsList.addElement(i));

		this.manageViewListsProperties(this.umidityJList);
		this.manageViewListsProperties(this.wateringsJList);
		this.manageViewListsProperties(this.warningsJList);

	}

	private void manageViewStatusLabels() {
		if (this.manualMode) {
			this.manualModeLabel.setText("MANUAL MODE: ON");
		} else {
			this.manualModeLabel.setText("MANUAL MODE: OFF");
		}

		if (this.isWatering) {
			this.isWateringLabel.setText("WATERING STATUS: ON");
		} else {
			this.isWateringLabel.setText("WATERING STATUS: OFF");
		}

		if (this.currentState != null) {
			this.currentStateLabel.setText("SGHR STATUS: " + this.currentState);
		}

		if (this.lastUpdateFromServer != null) {
			this.lastUpdateFromServerLabel.setText("LAST UPDATE FROM SERVER ON: " + this.lastUpdateFromServer);
		} else {
			this.lastUpdateFromServerLabel.setText("LAST UPDATE FROM SERVER ON: ---");
		}
		

	}

	@Override
	public void viewUpdate() {
		this.manageListsData();
		this.manageViewStatusLabels();

	}

	@Override
	public synchronized void setManualMode(final boolean manualMode) {
		this.manualMode = manualMode;
	}

	@Override
	public synchronized void setWatering(final boolean isWatering) {
		this.isWatering = isWatering;
	}

	@Override
	public synchronized void setCurrentState(final String currentState) {
		this.currentState = currentState;
	}

	@Override
	public synchronized void setUmidityValuesList(final List<Pair<Float, String>> umidityValuesList) {
		this.umidityValuesList = umidityValuesList;
	}

	@Override
	public synchronized void setWateringsList(final List<Pair<Float, String>> wateringsList) {
		this.wateringsList = wateringsList;
	}

	@Override
	public synchronized void setWarningsList(final List<String> warningsList) {
		this.warningsList = warningsList;
	}

	@Override
	public synchronized void setLastUpdateFromServer(final Date lastUpdateFromServer) {
		this.lastUpdateFromServer = lastUpdateFromServer;
	}

}
