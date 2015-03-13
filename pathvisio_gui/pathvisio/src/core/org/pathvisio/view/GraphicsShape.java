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
package org.pathvisio.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PathwayElementEvent;
import org.pathvisio.model.ShapeType;
import org.pathvisio.preferences.GlobalPreference;
import org.pathvisio.preferences.PreferenceManager;
import org.pathvisio.view.LinAlg.Point;

/**
 * This is an {@link Graphics} class representing shapelike forms,
 * and provides implementation for containing 8 handles placed in a
 * (rotated) rectangle around the shape and a rotation handle
 */
public abstract class GraphicsShape extends Graphics implements LinkProvider, Adjustable {

	private static final double M_ROTATION_HANDLE_POSITION = 20.0;

	//Side handles
	Handle handleN;
	Handle handleE;
	Handle handleS;
	Handle handleW;
	//Corner handles
	Handle handleNE;
	Handle handleSE;
	Handle handleSW;
	Handle handleNW;
	//Rotation handle
	Handle handleR;

	Handle[] handles = new Handle[] {};

	public GraphicsShape(VPathway canvas, PathwayElement o)
	{
		super(canvas, o);
	}

	protected void createHandles()
	{
		if (gdata.getShapeType() != null && !gdata.getShapeType().isResizeable()
				&& !gdata.getShapeType().isRotatable())
		{
			return; // no resizing, no handles
		}
		else if (gdata.getShapeType() != null && !gdata.getShapeType().isResizeable()
				&& gdata.getShapeType().isRotatable())
		{
			handleR = new Handle(Handle.Freedom.ROTATION, this, this);
			handleR.setCursorHint(Cursor.MOVE_CURSOR);
			handles = new Handle[]
			{
					handleR
			};
		}
		else if (this instanceof State)
		{
			handleNE = new Handle(Handle.Freedom.FREER, this, this);
			handleSE = new Handle(Handle.Freedom.FREE, this, this);
			handleSW = new Handle(Handle.Freedom.FREER, this, this);
			handleNW = new Handle(Handle.Freedom.FREE, this, this);
			handleNE.setCursorHint(Cursor.NE_RESIZE_CURSOR);
			handleSE.setCursorHint(Cursor.SE_RESIZE_CURSOR);
			handleSW.setCursorHint(Cursor.SW_RESIZE_CURSOR);
			handleNW.setCursorHint(Cursor.NW_RESIZE_CURSOR);
			handles = new Handle[]
  				{
  					handleNE, handleSE,
  					handleSW, handleNW,
  				};				
		}
		else
		{
			handleN = new Handle(Handle.Freedom.Y, this, this);
			handleE = new Handle(Handle.Freedom.X, this, this);
			handleS = new Handle(Handle.Freedom.Y, this, this);
			handleW = new Handle(Handle.Freedom.X, this, this);

			handleNE = new Handle(Handle.Freedom.FREER, this, this);
			handleSE = new Handle(Handle.Freedom.FREE, this, this);
			handleSW = new Handle(Handle.Freedom.FREER, this, this);
			handleNW = new Handle(Handle.Freedom.FREE, this, this);

			handleN.setCursorHint(Cursor.N_RESIZE_CURSOR);
			handleE.setCursorHint(Cursor.E_RESIZE_CURSOR);
			handleS.setCursorHint(Cursor.S_RESIZE_CURSOR);
			handleW.setCursorHint(Cursor.W_RESIZE_CURSOR);
			handleNE.setCursorHint(Cursor.NE_RESIZE_CURSOR);
			handleSE.setCursorHint(Cursor.SE_RESIZE_CURSOR);
			handleSW.setCursorHint(Cursor.SW_RESIZE_CURSOR);
			handleNW.setCursorHint(Cursor.NW_RESIZE_CURSOR);
            
			if(this instanceof GeneProduct || 
				this instanceof Label || !gdata.getShapeType().isRotatable())
			{
                // No rotation handle for these objects
				handles = new Handle[]
				{
					handleN, handleNE, handleE, handleSE,
					handleS, handleSW, handleW,	handleNW,
				};
			}
			else
			{
				handleR = new Handle(Handle.Freedom.ROTATION, this, this);
				handleR.setCursorHint(Cursor.MOVE_CURSOR);

				handles = new Handle[]
				{
					handleN, handleNE, handleE, handleSE,
					handleS, handleSW, handleW,	handleNW,
					handleR
				};
			}
		}
		setHandleLocation();
	}

	protected void setVScaleRectangle(Rectangle2D r) {
		gdata.setMWidth(mFromV(r.getWidth()));
		gdata.setMHeight(mFromV(r.getHeight()));
		gdata.setMLeft(mFromV(r.getX()));
		gdata.setMTop(mFromV(r.getY()));
	}

	protected void vMoveBy(double vdx, double vdy)
	{
		gdata.setMLeft(gdata.getMLeft()  + mFromV(vdx));
		gdata.setMTop(gdata.getMTop() + mFromV(vdy));
	}

	public Handle[] getHandles()
	{
		return handles;
	}

	/**
	 * Translate the given point to internal coordinate system
	 * (origin in center and axis direction rotated with this objects rotation
	 * @param MPoint p
	 */
	private Point mToInternal(Point p)
	{
		Point pt = mRelativeToCenter(p);
		Point pr = LinAlg.rotate(pt, gdata.getRotation());
		return pr;
	}

	/**
	 * Translate the given coordinates to external coordinate system (of the
	 * drawing canvas)
	 * @param x
	 * @param y
	 */
	private Point mToExternal(double x, double y)
	{
		Point p = new Point(x, y);
		Point pr = LinAlg.rotate(p, -gdata.getRotation());
		pr.x += gdata.getMCenterX();
		pr.y += gdata.getMCenterY();
		return pr;
	}

	/**
	 * Get the coordinates of the given point relative
	 * to this object's center
	 * @param p
	 */
	private Point mRelativeToCenter(Point p)
	{
		return p.subtract(new Point(gdata.getMCenterX(), gdata.getMCenterY()));
	}

	/**
	 * Set the rotation of this object
	 * @param angle angle of rotation in radians
	 */
	public void setRotation(double angle)
	{
		if(angle < 0) gdata.setRotation(angle + Math.PI*2);
		else if(angle > Math.PI*2) gdata.setRotation (angle - Math.PI*2);
		else gdata.setRotation(angle);
	}

	public void adjustToHandle(Handle h, double vnewx, double vnewy)
	{
		//Rotation
		if 	(h == handleR)
		{
			Point cur = mRelativeToCenter(new Point(mFromV(vnewx), mFromV(vnewy)));

			double rotation = Math.atan2(cur.y, cur.x);
			if (PreferenceManager.getCurrent().getBoolean(GlobalPreference.SNAP_TO_ANGLE) ||
					canvas.isSnapToAngle())
			{
				//Snap the rotation angle
				double snapStep = PreferenceManager.getCurrent().getInt(
						GlobalPreference.SNAP_TO_ANGLE_STEP) * Math.PI / 180;
				rotation = Math.round (rotation / snapStep) * snapStep;
			}
			setRotation (rotation);
			return;
		}

		// Transformation
		Point iPos = mToInternal(new Point(mFromV(vnewx), mFromV(vnewy)));

		double idx = 0;
		double idy = 0;
		double idw = 0;
		double idh = 0;
		double halfh = gdata.getMHeight () / 2;
		double halfw = gdata.getMWidth () / 2;

		if	(h == handleN || h == handleNE || h == handleNW)
		{
			idh = -(iPos.y + halfh);
			idy = -idh / 2;
		}
		if	(h == handleS || h == handleSE || h == handleSW )
		{
			idh = (iPos.y - halfh);
			idy = idh / 2;
		}
		if	(h == handleE || h == handleNE || h == handleSE) {
			idw = (iPos.x - halfw);
			idx = idw / 2;
		}
		if	(h == handleW || h == handleNW || h== handleSW) {
			idw = -(iPos.x + halfw);
			idx = -idw / 2;
		};

		double neww = gdata.getMWidth() + idw;
		double newh = gdata.getMHeight() + idh;

		//In case object had negative width, switch handles
		if(neww < 0)
		{
			setHorizontalOppositeHandle(h);
			neww = -neww;
		}
		if(newh < 0)
		{
			setVerticalOppositeHandle(h);
			newh = -newh;
		}

		gdata.setMWidth(neww);
		gdata.setMHeight(newh);
		Point vcr = LinAlg.rotate(new Point (idx, idy), -gdata.getRotation());
		gdata.setMCenterX (gdata.getMCenterX() + vcr.x);
		gdata.setMCenterY (gdata.getMCenterY() + vcr.y);

	}

	private void setHorizontalOppositeHandle(Handle h)
	{
		Handle opposite = null;
		if(h == handleE) opposite = handleW;
		else if(h == handleW) opposite = handleE;
		else if(h == handleNE) opposite = handleNW;
		else if(h == handleSE) opposite = handleSW;
		else if(h == handleNW) opposite = handleNE;
		else if(h == handleSW) opposite = handleSE;
		else opposite = h;
		canvas.setPressedObject(opposite);
	}

	private void setVerticalOppositeHandle(Handle h)
	{
		Handle opposite = null;
		if(h == handleN) opposite = handleS;
		else if(h == handleS) opposite = handleN;
		else if(h == handleNE) opposite = handleSE;
		else if(h == handleSE) opposite = handleNE;
		else if(h == handleNW) opposite = handleSW;
		else if(h == handleSW) opposite = handleNW;
		else opposite = h;
		canvas.setPressedObject(opposite);
	}

	/**
	 * Sets the handles at the correct location;
	 * @param ignore the position of this handle will not be adjusted
	 */
	protected void setHandleLocation()
	{
		Point p;
		if (gdata.getShapeType() == null || gdata.getShapeType().isResizeable()) {
			
			if (handleN != null)
			{
				p = mToExternal(0, -gdata.getMHeight()/2);
				handleN.setMLocation(p.x, p.y);
				p = mToExternal(gdata.getMWidth()/2, 0);
				handleE.setMLocation(p.x, p.y);
				p = mToExternal(0,  gdata.getMHeight()/2);
				handleS.setMLocation(p.x, p.y);
				p = mToExternal(-gdata.getMWidth()/2, 0);
				handleW.setMLocation(p.x, p.y);
			}
			
			p = mToExternal(gdata.getMWidth()/2, -gdata.getMHeight()/2);
			handleNE.setMLocation(p.x, p.y);
			p = mToExternal(gdata.getMWidth()/2, gdata.getMHeight()/2);
			handleSE.setMLocation(p.x, p.y);
			p = mToExternal(-gdata.getMWidth()/2, gdata.getMHeight()/2);
			handleSW.setMLocation(p.x, p.y);
			p = mToExternal(-gdata.getMWidth()/2, -gdata.getMHeight()/2);
			handleNW.setMLocation(p.x, p.y);
		}
		if ((gdata.getShapeType() ==null || gdata.getShapeType().isRotatable()) && (handleR != null))
		{
			p = mToExternal(gdata.getMWidth()/2 + M_ROTATION_HANDLE_POSITION, 0);
			handleR.setMLocation(p.x, p.y);
		}

		for(Handle h : getHandles()) h.rotation = gdata.getRotation();
	}

	protected Shape calculateVOutline()
	{
		//Include rotation and stroke
		Area a = new Area(getShape(true, true));
		//Include link anchors
		if(showLinkAnchors) {
			for(LinkAnchor la : getLinkAnchors()) {
				a.add(new Area(la.getShape()));
			}
		}
		return a;
	}

	protected Shape getVShape(boolean rotate) {
		return getShape(rotate, false); //Get the shape without border
	}

	/**
	 * Returns the shape that should be drawn
	 * @parameter rotate whether to take into account rotation or not
	 * @parameter stroke whether to include the stroke or not
	 * @return
	 */
	protected Shape getShape(boolean rotate, boolean stroke)
	{
		if(stroke) {
			return getShape(rotate, DEFAULT_STROKE.getLineWidth());
		} else {
			return getShape(rotate, 0);
		}
	}

	public Shape getShape()
	{
		return getShape (false, 0);
	}

	/**
	 * Returns the shape that should be drawn
	 * @parameter rotate whether to take into account rotation or not
	 * @parameter sw the width of the stroke to include
	 * @return
	 */
	protected java.awt.Shape getShape(boolean rotate, float sw) {
		double x = getVLeft();
		double y = getVTop();
		double w = getVWidth();
		double h = getVHeight();
		double cx = getVCenterX();
		double cy = getVCenterY();

		java.awt.Shape s = null;

		if (gdata.getShapeType() == null || gdata.getShapeType() == ShapeType.NONE)
		{
			s = ShapeRegistry.getShape ("Rectangle", x, y, w, h);
		}
		else
		{
			s = ShapeRegistry.getShape (
					gdata.getShapeType().getName(),
					x, y, w, h);
		}

		if(rotate) {
			AffineTransform t = new AffineTransform();
			t.rotate(gdata.getRotation(), cx, cy);
			s = t.createTransformedShape(s);
		}

		if(sw > 0) {
			Stroke stroke = new BasicStroke(sw);
			s = stroke.createStrokedShape(s);
		}
		return s;
	}

	public void gmmlObjectModified(PathwayElementEvent e)
	{
		markDirty(); // mark everything dirty
		if (handles.length > 0) setHandleLocation();
	}

	List<LinkAnchor> linkAnchors = new ArrayList<LinkAnchor>();

	private static final int MIN_SIZE_LA = 25;
	private int numLinkanchorsH = -1;
	private int numLinkanchorsV = -1;

	public List<LinkAnchor> getLinkAnchors() {
		//Number of link anchors depends on the size of the object
		//If the width/height is large enough, there will be three link anchors per side,
		//Otherwise there will be only one link anchor per side
		String anchorsCnt = gdata.getDynamicProperty("NumberOfAnchors");
        int numAnchors = 3;
        if (anchorsCnt != null) {
            numAnchors = Integer.parseInt(anchorsCnt);
        }
        int numH = gdata.getMWidth() < MIN_SIZE_LA ? 1 : numAnchors;
		int numV = gdata.getMHeight() < MIN_SIZE_LA ? 1 : numAnchors;
		if(numH != numLinkanchorsH || numV != numLinkanchorsV) {
			createLinkAnchors(numH, numV);
		}
		return linkAnchors;
	}

	private void createLinkAnchors(int numH, int numV) {
		linkAnchors.clear();
		double deltaH = 2.0/(numH + 1);
		for(int i = 1; i <= numH; i++) {
			linkAnchors.add(new LinkAnchor(canvas, gdata, -1 + i * deltaH, -1));
			linkAnchors.add(new LinkAnchor(canvas, gdata, -1 + i * deltaH, 1));
		}
		double deltaV = 2.0/(numV + 1);
		for(int i = 1; i <= numV; i++) {
			linkAnchors.add(new LinkAnchor(canvas, gdata, -1, -1 + i * deltaV));
			linkAnchors.add(new LinkAnchor(canvas, gdata, 1, -1 + i * deltaV));
		}
		numLinkanchorsH = numH;
		numLinkanchorsV = numV;
	}

	boolean showLinkAnchors = false;

	public void showLinkAnchors() {
		if(!showLinkAnchors) {
			showLinkAnchors = true;
			markDirty();
		}
	}

	public void hideLinkAnchors() {
		if(showLinkAnchors) {
			showLinkAnchors = false;
			markDirty();
		}
	}

	public LinkAnchor getLinkAnchorAt(Point2D p) {
		for(LinkAnchor la : getLinkAnchors()) {
			if(la.getMatchArea().contains(p)) {
				return la;
			}
		}
		return null;
	}

	@Override protected void destroyHandles()
	{
		for(Handle h : handles) {
			h.destroy();
		}
		handles = new Handle[] {};
	}

	protected void doDraw(Graphics2D g2d) {
		if(showLinkAnchors) {
			for(LinkAnchor la : getLinkAnchors()) {
				la.draw((Graphics2D)g2d.create());
			}
		}
	}

	protected void drawTextLabel(Graphics2D g)
	{
		int margin = (int)vFromM(5);
		Rectangle area = getVShape(true).getBounds();
		String label = gdata.getTextLabel();
		if(label != null && !"".equals(label)) {
			//Split by newline, to enable multi-line labels
			String[] lines = label.split("\n");

			FontMetrics fm = g.getFontMetrics();
			int lh = fm.getHeight();
			int yoffset = area.y + fm.getAscent();
			switch (gdata.getValign())
			{
			case MIDDLE:
				yoffset += (area.height - (lines.length * lh)) / 2;
				break;
			case TOP:
				yoffset += margin;
				break;
			case BOTTOM:
				yoffset += area.height - margin - (lines.length * lh);
			}

			for(int i = 0; i < lines.length; i++) {
				if(lines[i].equals("")) continue; //Can't have attributed string with 0 length
				AttributedString ats = getVAttributedString(lines[i]);
				Rectangle2D tb = fm.getStringBounds(ats.getIterator(), 0, lines[i].length(), g);

				int xoffset = area.x;
				switch (gdata.getAlign())
				{
				case CENTER:
					xoffset += (int)(area.width / 2) - (int)(tb.getWidth() / 2);
					break;
				case LEFT:
					xoffset += margin;
					break;
				case RIGHT:
					xoffset += area.width - margin - tb.getWidth();
					break;
				}

				g.drawString(ats.getIterator(), xoffset,
						yoffset + (int)(i * tb.getHeight()));
			}

		}
	}

	AttributedString getVAttributedString(String text) {
		AttributedString ats = new AttributedString(text);
		if(gdata.isStrikethru()) {
			ats.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		}
		if(gdata.isUnderline()) {
			ats.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		}

		ats.addAttribute(TextAttribute.FONT, getVFont());
		return ats;
	}

	Font getVFont() {
		String name = gdata.getFontName();
		int style = getVFontStyle();
		int size = (int)getVFontSize();
		return new Font(name, style, size);
	}

	double getVFontSize()
	{
		return vFromM(gdata.getMFontSize());
	}

	protected void drawShape(Graphics2D g)
	{
		Color fillcolor = gdata.getFillColor();

		if (gdata.getShapeType() == null ||
			gdata.getShapeType() == ShapeType.NONE) return; // nothing to draw.

		java.awt.Shape shape = getShape(true, false);

		if (gdata.getShapeType() == ShapeType.BRACE ||
			gdata.getShapeType() == ShapeType.ARC)
		{
			// don't fill arcs or braces
			// TODO: this exception should disappear in the future,
			// when we've made sure all pathways on wikipathways have
			// transparent arcs and braces
		}
		else
		{
			// fill the rest
			if(!gdata.isTransparent())
			{
				g.setColor(fillcolor);
				g.fill(shape);
			}
		}
		g.setColor(getLineColor());
		g.draw(shape);
	}
}
