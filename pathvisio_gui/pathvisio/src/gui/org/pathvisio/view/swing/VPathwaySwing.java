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
package org.pathvisio.view.swing;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

import org.pathvisio.gui.swing.MainPanel;
import org.pathvisio.gui.swing.dnd.PathwayImportHandler;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.preferences.GlobalPreference;
import org.pathvisio.preferences.PreferenceManager;
import org.pathvisio.view.Handle;
import org.pathvisio.view.VElementMouseEvent;
import org.pathvisio.view.VElementMouseListener;
import org.pathvisio.view.VPathway;
import org.pathvisio.view.VPathwayElement;
import org.pathvisio.view.VPathwayEvent;
import org.pathvisio.view.VPathwayListener;
import org.pathvisio.view.VPathwayWrapper;

/**
 * swing-dependent implementation of VPathway.
 */
public class VPathwaySwing extends JPanel implements VPathwayWrapper,
MouseMotionListener, MouseListener, KeyListener, VPathwayListener, VElementMouseListener, MouseWheelListener {

	VPathway child;

	public JScrollPane container;

	public VPathwaySwing(JScrollPane parent) {
		super();
		if (parent == null)
			throw new IllegalArgumentException("parent is null");
		this.container = parent;
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		addMouseWheelListener(this);

		setFocusable(true);
		setRequestFocusEnabled(true);
		setTransferHandler(new PathwayImportHandler());

		setDoubleBuffered(
				PreferenceManager.getCurrent().getBoolean(
						GlobalPreference.ENABLE_DOUBLE_BUFFERING));
	}

	public void setChild(VPathway c) {
		child = c;
		child.addVPathwayListener(this);
		child.addVElementMouseListener(this);
	}

	public VPathway getChild() {
		return child;
	}

	public Rectangle getVBounds() {
		return getBounds();
	}

	public Dimension getVSize() {
		return getPreferredSize();
	}

	public Dimension getViewportSize() {
		if (container instanceof JScrollPane) {
			return ((JScrollPane) container).getViewport().getExtentSize();
		}
		return getSize();
	}

	public void redraw() {
		repaint();
	}

	protected void paintComponent(Graphics g)
	{
		child.draw((Graphics2D) g);
	}

	public void redraw(Rectangle r) {
		repaint(r);
	}

	public void setVSize(Dimension size) {
		setPreferredSize(size);
		revalidate();
	}

	public void setVSize(int w, int h) {
		setVSize(new Dimension(w, h));
	}

	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2) {
			child.mouseDoubleClick(new SwingMouseEvent(e));
		}
	}

	public void mouseEntered(MouseEvent e) {
		//requestFocus(); //TODO: test if this is needed in applet.
		child.mouseEnter(new SwingMouseEvent(e));
	}

	public void mouseExited(MouseEvent e) {
		child.mouseExit(new SwingMouseEvent(e));

	}

	private int vPreviousX;
	private int vPreviousY;
	private boolean isPanning;
	
	public void mousePressed(MouseEvent e) {
		requestFocus();
		if(e.getButton() != MouseEvent.BUTTON2){
			child.mouseDown(new SwingMouseEvent(e));
		} else {
			vPreviousX = e.getX();
			vPreviousY = e.getY();
			isPanning = true;
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
	}

	public void mouseReleased(MouseEvent e) {
		if(e.getButton() != MouseEvent.BUTTON2){
			child.mouseUp(new SwingMouseEvent(e));
		} else {
			isPanning = false;
			setCursor(Cursor.getDefaultCursor());
		}
	}

	public void keyPressed(KeyEvent e) {
		child.keyPressed(new SwingKeyEvent(e));
	}

	public void keyReleased(KeyEvent e) {
		child.keyReleased(new SwingKeyEvent(e));
	}

	public void keyTyped(KeyEvent e) {
		// TODO: find out how to handle this one
	}

	public void mouseDragged(MouseEvent e) {
		Rectangle r = container.getViewport().getViewRect();
		final int stepSize = 10;
		int newx = (int)r.getMinX();
		int newy = (int)r.getMinY();
		// scroll when dragging out of view
		if (e.getX() > r.getMaxX())
		{
			newx += stepSize;
		}
		if (e.getX() < r.getMinX())
		{
			newx = Math.max (newx - stepSize, 0);
		}
		if (e.getY() > r.getMaxY())
		{
			newy += stepSize;
		}
		if (e.getY() < r.getMinY())
		{
			newy = Math.max (newy - stepSize, 0);
		}
		container.getViewport().setViewPosition(
				new Point (newx, newy)
		);
		if(!isPanning){
			child.mouseMove(new SwingMouseEvent(e));
		} else {
		    int vdx = e.getX() - vPreviousX;
			int vdy = e.getY() - vPreviousY;
			
			newx = container.getViewport().getViewPosition().x - vdx;
			newy = container.getViewport().getViewPosition().y - vdy;

			
		    if(newx < 0 || newy < 0 || !container.getHorizontalScrollBar().isVisible() || !container.getVerticalScrollBar().isVisible()){
				vPreviousX = e.getX();
				vPreviousY = e.getY();
				child.allMoveBy(vdx, vdy);	
			} else {
				container.getViewport().setViewPosition(new Point (newx, newy));
			}
			
			
			//int dx = vPreviousX - e.getX();
			//int dy = vPreviousY - e.getY();
			//moveView(dx,dy) ;			
		}
	}

	private void moveView(int dx, int dy) {
        // declare the movement to the component
        //this.movePosition(dx, dy);
		
		JViewport jviewport = container.getViewport();

		// calculate the new viewport position
        Point newPos = jviewport.getViewPosition();
        newPos.translate(dx, dy);
 
        // reset the viewport if it is going to overrun the edge
        if (newPos.x <= 0 || newPos.x + jviewport.getWidth() >= jviewport.getView().getWidth()
                || newPos.y <= 0 || newPos.y + jviewport.getHeight() >= jviewport.getView().getHeight()) {
            Point centre = new Point(this.getWidth() / 2, this.getHeight() / 2);
            jviewport.setViewPosition(centre);
            container.repaint() ;
        } else {
            // move the viewport to do the necessary redraw
            jviewport.setViewPosition(newPos);
        }
    }
	
	public void mouseMoved(MouseEvent e) {
		child.mouseMove(new SwingMouseEvent(e));
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		
	    int notches = e.getWheelRotation();
	    double dx,dy;
	    if(notches < 0){
	    	child.setPctZoom(child.getPctZoom() * 21 / 20);
	    	dx = - e.getPoint().x / 20.0;
	    	dy = - e.getPoint().y / 20.0;
	    } else {
	    	child.setPctZoom(child.getPctZoom() * 20 / 21);
	    	dx = e.getPoint().x / 21.0;
	    	dy = e.getPoint().y / 21.0;
	    }
	    
	    int newx = (int)Math.round(container.getViewport().getViewPosition().x - dx);
	    int newy = (int)Math.round(container.getViewport().getViewPosition().y - dy);
	    
	    if(newx < 0 || newy < 0 || !container.getHorizontalScrollBar().isVisible() || !container.getVerticalScrollBar().isVisible()){
	    	child.allMoveBy(dx, dy);
	    } else {
	    	container.getViewport().setViewPosition(new Point (newx, newy));
	    }
	    
		int comboIndex = 10;
		DecimalFormat df = new DecimalFormat("###.#");
		((JComboBox)((MainPanel)container.getParent().getParent()).getToolBar().getComponentAtIndex(comboIndex)).setSelectedItem(df.format(child.getPctZoom())+"%");
	}
	
	public void registerKeyboardAction(KeyStroke k, Action a) {
		super.registerKeyboardAction(a, k, WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		//super.registerKeyboardAction(a, k, WHEN_IN_FOCUSED_WINDOW);
	}

	public VPathway createVPathway() {
		setChild(new VPathway(this));
		return child;
	}

	public void vPathwayEvent(VPathwayEvent e) {
		switch(e.getType()) {
		case VPathwayEvent.MODEL_LOADED:
			if(e.getSource() == child) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						container.setViewportView(VPathwaySwing.this);
						double[] mSize = child.getPathwayModel().getMappInfo().getMBoardSize();
						int w = (int)child.vFromM(mSize[0]);
						int h = (int)child.vFromM(mSize[1]);
						setVSize(w, h);
						VPathwaySwing.this.requestFocus();
					}
				});
			}
			break;
		case VPathwayEvent.ELEMENT_HOVER:
			showToolTip(e);
			break;
		}
	}

	public void pasteFromClipboard() {
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		TransferHandler handler = getTransferHandler();
		handler.importData(this, clip.getContents(this));
	}

	List<PathwayElement> lastCopied;

	public void copyToClipboard(Pathway source, List<PathwayElement> copyElements) {
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		clip.setContents(new PathwayTransferable(source, copyElements),
				(PathwayImportHandler)getTransferHandler());
		((PathwayImportHandler)getTransferHandler()).obtainedOwnership();
	}

	Set<ToolTipProvider> toolTipProviders = new HashSet<ToolTipProvider>();

	public void addToolTipProvider(ToolTipProvider p) {
		toolTipProviders.add(p);
	}

	public void showToolTip(final VPathwayEvent e) {
		if(toolTipProviders.size() == 0) return;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				List<VPathwayElement> elements = e.getAffectedElements();
				if(elements.size() > 0) {
					PathwayToolTip tip = new PathwayToolTip(elements);

					if(!tip.hasContent()) return;

					final JWindow w = new JWindow();
					w.setLayout(new BorderLayout());
					w.add(tip, BorderLayout.CENTER);

					Point p = e.getMouseEvent().getLocation();
					SwingUtilities.convertPointToScreen(p, VPathwaySwing.this);
					w.setLocation(p);

					//For some reason the mouse listener only works when
					//adding it directly to the scrollpane (the first child component).
					tip.getComponent(0).addMouseListener(new MouseAdapter() {
						public void mouseExited(MouseEvent e) {
							//Don't dispose if on scrollbars
							if(e.getComponent().contains(e.getPoint()))
								return;
							//Don't dispose if mouse is down (usually when scrolling)
							if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0)
								return;
							w.dispose();
						}
					});

					w.setVisible(true);
					w.pack();
				}
			}
		});
	}

	class PathwayToolTip extends JPanel {
		private boolean hasContent;

		public PathwayToolTip(List<VPathwayElement> elements) {
			applyToolTipStyle(this);
			setLayout(new BorderLayout());
			DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("pref"));
			for(ToolTipProvider p : toolTipProviders) {
				Component c = p.createToolTipComponent(this, elements);
				if(c != null) {
					hasContent = true;
					builder.append(c);
					builder.nextLine();
				}
			}

			JPanel contents = builder.getPanel();
			applyToolTipStyle(contents);
			JScrollPane scroll = new JScrollPane(contents, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

			int w = contents.getPreferredSize().width +
				scroll.getVerticalScrollBar().getPreferredSize().width + 5;
			int h = contents.getPreferredSize().height +
				scroll.getHorizontalScrollBar().getPreferredSize().height + 5;
			w = Math.min(400, w);
			h = Math.min(500, h);
			setPreferredSize(new Dimension(w, h));
			add(scroll, BorderLayout.CENTER);
		}

		public boolean hasContent() {
			return hasContent;
		}

	}

	/**
	 * Apply the tooltip style (e.g. colors) to the given component.
	 */
	public static void applyToolTipStyle(JComponent c) {
		c.setForeground((Color)UIManager.get("ToolTip.foreground"));
		c.setBackground((Color)UIManager.get("ToolTip.background"));
		c.setFont((Font)UIManager.get("ToolTip.font"));
	}

	public void scrollTo(Rectangle r)
	{
		if (container instanceof JScrollPane)
			((JScrollPane)container).getViewport().scrollRectToVisible(r);
	}

	public void vElementMouseEvent(VElementMouseEvent e) {
		//Change mouse cursor based on underlying object
		if(	e.getElement() instanceof Handle) {
			if(e.getType() == VElementMouseEvent.TYPE_MOUSEENTER) {
				Handle h = (Handle)e.getElement();
				setCursor(Cursor.getPredefinedCursor(h.getCursorHint()));
			} else if(e.getType() == VElementMouseEvent.TYPE_MOUSEEXIT) {
				setCursor(Cursor.getDefaultCursor());
			}
		}
	}

	private boolean disposed = false;
	public void dispose()
	{
		assert (!disposed);
		if (container != null && container instanceof JScrollPane)
			((JScrollPane)container).remove(this);
		child.removeVPathwayListener(this);
		child.removeVElementMouseListener(this);

		removeMouseListener(this);
		removeMouseMotionListener(this);
		removeKeyListener(this);
		setTransferHandler(null);

		// clean up actions, inputs registered earlier for this component
		// (otherwise VPathway won't get GC'ed, because actions contain reference to VPathay)
		getActionMap().clear();
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).clear();

		child = null; // free VPathway for GC
		disposed = true;
	}
}
