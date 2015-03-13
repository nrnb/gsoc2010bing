// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.pathvisio.gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.pathvisio.ApplicationEvent;
import org.pathvisio.gex.BackpageExpression;
import org.pathvisio.preferences.GlobalPreference;
import org.pathvisio.preferences.PreferenceManager;
import org.pathvisio.util.Resources;

import com.mammothsoftware.frwk.ddb.DropDownButton;

/**
 * the mainPanel for the standalone (non-applet) version of PathVisio.
 */
public class MainPanelStandalone extends MainPanel
{
	protected JMenuBar menuBar;

	private StandaloneActions standaloneActions = null;
	private List<File> recent;
	public static GlobalPreference[] mostRecentArray = new GlobalPreference[] {
    	GlobalPreference.MOST_RECENT_1, GlobalPreference.MOST_RECENT_2,
    	GlobalPreference.MOST_RECENT_3, GlobalPreference.MOST_RECENT_4,
    	GlobalPreference.MOST_RECENT_5, GlobalPreference.MOST_RECENT_6,
    	GlobalPreference.MOST_RECENT_7, GlobalPreference.MOST_RECENT_8,
    	GlobalPreference.MOST_RECENT_9, GlobalPreference.MOST_RECENT_10,
    };

	@Override
	protected void addMenuActions(JMenuBar mb) {
		JMenu fileMenu = new JMenu("File");

		addToMenu(standaloneActions.newAction, fileMenu);
		addToMenu(standaloneActions.openAction, fileMenu);

		recentPathwaysMenu = new JMenu("Open Recent");
		initRecentPathwayList();
		fileMenu.add (recentPathwaysMenu);
		addToMenu(actions.standaloneSaveAction, fileMenu);
		addToMenu(actions.standaloneSaveAsAction, fileMenu);
		fileMenu.addSeparator();
		addToMenu(actions.importAction, fileMenu);
		addToMenu(actions.exportAction, fileMenu);
		fileMenu.addSeparator();
		addToMenu(actions.exitAction, fileMenu);

		JMenu editMenu = new JMenu("Edit");
		addToMenu(actions.undoAction, editMenu);
		addToMenu(actions.copyAction, editMenu);
		addToMenu(actions.pasteAction, editMenu);
		addToMenu(standaloneActions.searchAction, editMenu);
		editMenu.addSeparator();
		addToMenu(standaloneActions.preferencesAction, editMenu);

		JMenu selectionMenu = new JMenu("Selection");
		for(Action a : actions.layoutActions) addToMenu(a, selectionMenu);

		editMenu.add (selectionMenu);

		JMenu dataMenu = new JMenu("Data");
		addToMenu (standaloneActions.selectGeneDbAction, dataMenu);
		addToMenu (standaloneActions.selectMetaboliteDbAction, dataMenu);

		JMenu viewMenu = new JMenu("View");
		JMenu zoomMenu = new JMenu("Zoom");
		viewMenu.add(zoomMenu);
		for(Action a : actions.zoomActions) addToMenu(a, zoomMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(actions.aboutAction);
		helpMenu.add(standaloneActions.pluginManagerAction);
		helpMenu.add(standaloneActions.helpAction);

		mb.add(fileMenu);
		mb.add(editMenu);
		mb.add(dataMenu);
		mb.add(viewMenu);
		mb.add(helpMenu);
	}

	private final PvDesktop desktop;

	public MainPanelStandalone(PvDesktop desktop)
	{
		super(desktop.getSwingEngine(), null);
		this.desktop = desktop;

		standaloneActions = new StandaloneActions(desktop);
	}

	@Override
	public void createAndShowGUI()
	{
		super.createAndShowGUI();

		SearchPane searchPane = new SearchPane(swingEngine);
		sidebarTabbedPane.addTab ("Search", searchPane);

		// backpage hook for showing expression data.
		bpt.addBackpageHook(new BackpageExpression(desktop.getGexManager()));
	}

	@Override
	public void applicationEvent(ApplicationEvent e) {
		super.applicationEvent(e);
		if(e.getType() == ApplicationEvent.PATHWAY_OPENED) {
			putInRecentPathwayList(swingEngine.getEngine().getActivePathway().getSourceFile());
			refreshRecentPathwaysMenu();
		}
		if(e.getType() == ApplicationEvent.PATHWAY_SAVE) {
			putInRecentPathwayList(swingEngine.getEngine().getActivePathway().getSourceFile());
			refreshRecentPathwaysMenu();
		}
	}

	JMenu recentPathwaysMenu;

	private void refreshRecentPathwaysMenu()
	{
		PreferenceManager prefs = PreferenceManager.getCurrent();

		recentPathwaysMenu.removeAll();
		int added = 0;
		recentPathwaysMenu.setMnemonic(KeyEvent.VK_R);
		for (int i = 0; i < 10; i++)
		{
			if (!prefs.get(mostRecentArray[i]).equals ("" + null))
			{
				final File file = prefs.getFile(mostRecentArray[i]);
				JMenuItem menuItem = new JMenuItem(file.getName(), KeyEvent.VK_0 + i);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(menuItem.getMnemonic(), InputEvent.CTRL_DOWN_MASK));
				menuItem.setToolTipText(file.getAbsolutePath());
				menuItem.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (swingEngine.canDiscardPathway())
						{
							swingEngine.openPathway(file);
						}
					}
				});
				recentPathwaysMenu.add(menuItem);
				added++;
			}
		}
		recentPathwaysMenu.setEnabled (added != 0);
	}

	@Override
	protected void addToolBarActions(final SwingEngine swingEngine, JToolBar tb)
	{
		tb.setLayout(new WrapLayout(1, 1));

		addToToolbar(standaloneActions.newAction);
		addToToolbar(standaloneActions.openAction);
		addToToolbar(actions.standaloneSaveAction);
		tb.addSeparator();
		addToToolbar(actions.copyAction);
		addToToolbar(actions.pasteAction);

		tb.addSeparator();

		addToToolbar(actions.undoAction);

		tb.addSeparator();

		addToToolbar(new JLabel("Zoom:", JLabel.LEFT));
		JComboBox combo = new JComboBox(actions.zoomActions);
		combo.setMaximumSize(combo.getPreferredSize());
		combo.setEditable(true);
		combo.setSelectedIndex(5); // 100%
		combo.addActionListener(new ZoomComboListener());
		addToToolbar(combo, TB_GROUP_SHOW_IF_VPATHWAY);

		tb.addSeparator();

		String submenu = "line";

		for(Action[] aa : actions.newElementActions) {
			if(aa.length == 1) {
				addToToolbar(aa[0]);
			} else { //This is the line/receptor sub-menu
				String icon = "newlinemenu.gif";
				String tooltip = "Select a line to draw";
				
				if(submenu.equals("receptors")) { //Next one is receptors
					icon = "newlineshapemenu.gif";
					tooltip = "Select a receptor/ligand to draw";
				} else {
					submenu = "receptors";
				}
				DropDownButton lineButton = new DropDownButton(new ImageIcon(
						Resources.getResourceURL(icon)));
				lineButton.setToolTipText(tooltip);
				
				for(Action a : aa) {
					lineButton.addComponent(new JMenuItem(a));
				}
				addToToolbar(lineButton, TB_GROUP_SHOW_IF_EDITMODE);
				//lineButton.setEnabled(false);
			}
		}
		
		// define the drop-down menu for shapes 
		String icon = "newitems.gif";
		String tooltip = "Select a shape to draw";
		DropDownButton shapeButton = new DropDownButton(
				new ImageIcon(Resources.getResourceURL(icon)));
		shapeButton.setToolTipText(tooltip);		
		itemsDropDown = shapeButton;
		
		int numItemsPerRow = 6;
		addLabel(shapeButton, "Basic shapes");
		addButtons(actions.newShapeActions, shapeButton, numItemsPerRow);		

		addLabel(shapeButton, "Cellular components");
		addButtons(actions.newCellularComponentActions, shapeButton, numItemsPerRow);

		addLabel(shapeButton, "MIM shapes");
		addButtons(actions.newMIMShapeActions, shapeButton, numItemsPerRow);
		
		// addMenuItems() can be used to display item with label as memu items
		//addMenuItems(actions.newMolecularInteractionMapActions, shapeButton);
		
		addToToolbar(shapeButton, TB_GROUP_SHOW_IF_EDITMODE);
		//shapeButton.setEnabled(false);

		//tb.addSeparator();
		
		// define the drop-down menu for interactions
		icon = "newinteractionmenu.gif";
		tooltip = "Select a interaction to draw";
		DropDownButton lineButton = new DropDownButton(
				new ImageIcon(Resources.getResourceURL(icon)));
		lineButton.setToolTipText(tooltip);		
		//itemsDropDown = lineButton;
		
		numItemsPerRow = 6;		
		addLabel(lineButton, "Basic interactions");
		addButtons(actions.newInteractionActions, lineButton, numItemsPerRow);

		addLabel(lineButton, "Receptor/ligand");
		addButtons(actions.newRLInteractionActions, lineButton, numItemsPerRow);
		
		addLabel(lineButton, "MIM interactions");
		addButtons(actions.newMIMInteractionActions, lineButton, numItemsPerRow);		
		
		addToToolbar(lineButton, TB_GROUP_SHOW_IF_EDITMODE);
		//lineButton.setEnabled(false);

		tb.addSeparator();
		

		addToToolbar(actions.layoutActions);
	}

	public void putInRecentPathwayList(File pwf)
	{
		PreferenceManager prefs = PreferenceManager.getCurrent();
        recent.remove(pwf);
		recent.add(0,pwf);
		if(recent.size() > 10) recent.remove(recent.size()-1);

	    for (int i = 0; i < recent.size(); ++i)
	    {

	    	prefs.setFile(mostRecentArray[i], recent.get(i));
	    }
	}

	public void initRecentPathwayList()
	{
		PreferenceManager prefs = PreferenceManager.getCurrent();
		recent = new LinkedList<File>();
		for (int i = 0; i < 10; ++i)
		{
			if (!prefs.get(mostRecentArray[i]).equals ("" + null))
			{
				recent.add(prefs.getFile(mostRecentArray[i]));
			}
		}
		refreshRecentPathwaysMenu();
	}
}

