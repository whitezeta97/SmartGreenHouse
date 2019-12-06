package view;

import javax.swing.*;

import model.Model;
import model.ModelImpl;

import java.awt.*;

public class Gui {

	public Gui() {

		new ModelImpl();

		final BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(10);
		borderLayout.setVgap(10);

		JPanel jPanel = new JPanel(borderLayout);

		final DefaultListModel<String> umidityList = new DefaultListModel<>();

		umidityList.addElement("FVADLKAKDK");
		JList<String> umidityJList = new JList(umidityList);
		
		umidityList.addElement("ADMAWMDAWMDKLAWMKLD");
		

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
		jf.setVisible(true);

		this.viewUpdate();
	}

	private void viewUpdate() {
		// Set<Integer> disabled = model.allDisabled();
		// this.buttons.forEach(jb ->
		// jb.setEnabled(!disabled.contains(buttons.indexOf(jb))));
		// this.buttons.forEach(jb -> jb.setText(buttons.indexOf(jb) ==
		// model.getPosition() ? "*" : " "));
	}

}
