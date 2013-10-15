package com.vvdeng.miner.staff.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

public class UIUtil {
	public static Dimension getScreenSize(JFrame f) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();//
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(
				f.getGraphicsConfiguration());
		return new Dimension((int) screen.getWidth(), (int) screen.getHeight()
				- insets.bottom);
	}

	public static void setCenterPositoin(Component owner, Component component) {
		Dimension ownerDimension = owner.getSize();
		Dimension componentDimension = component.getSize();
		int xCenter = owner.getLocation().x
				+ (ownerDimension.width - componentDimension.width) / 2;
		int yCenter = owner.getLocation().y
				+ (ownerDimension.height - componentDimension.height) / 2;
		component.setLocation(xCenter, yCenter);
	}
	public static JButton createToolBarButton(String name,Icon icon,ActionListener listener) {
		JButton button = new JButton(name,icon);
		button.addActionListener(listener);
		button.setVerticalTextPosition(JButton.BOTTOM);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setOpaque(true);
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);
		return button;
	}
	public static JButton makeButton(String name, ImageIcon icon,
			ActionListener listener) {
		JButton button = new JButton(name, icon);
		button.addActionListener(listener);
		return button;
	}

	public static Point calPosition(Container c) {
		int x = c.getX(), y = c.getY();
		while (c.getParent() != null) {
			c = c.getParent();
			x += c.getX();
			y += c.getY();
		}
		System.out.println("x=" + x + " y=" + y);
		return new Point(x, y);
	}
}
