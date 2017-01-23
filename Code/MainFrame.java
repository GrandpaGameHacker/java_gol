import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
/*
 * Wrapper for running the applet stand-alone.
 * Also brings in some menu gui functionality (fps control)
 * and algorthm changing
*/
public class MainFrame
{
	
	public static void main(String[] argv){
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		TheFrame mFrame = new TheFrame("Game Of Life::Version 1.1");
		mFrame.setBounds(0, 0,d.width, d.height);
		mFrame.setLocationRelativeTo(null);
		mFrame.setVisible(true);
	}
}

class TheFrame extends Frame
{
	private static final long serialVersionUID = 1L;
	GameOfLife gol;
	public TheFrame(String title)
	{
		super(title);
		
		MenuBar mb = new MenuBar();
	    setMenuBar(mb);
	    
	    Menu sysMenu = new Menu("System");
	    mb.add(sysMenu);
	    Menu fpsMenu = new Menu("FPS Limiting");
	    mb.add(fpsMenu);
	    
	    // A only way the user can exit the app.
	    MenuItem exit = new MenuItem("Exit");
	    sysMenu.add(exit);
	    exit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
					dispose();
					System.exit(0);
			}
	    	
	    });
	    
	    
	    // FPS control menu
	    MenuItem fps60,fps30,fps15,fps5;
	    fps60 = new MenuItem("60");
	    fps30 = new MenuItem("30");
	    fps15 = new MenuItem("15");
	    fps5 = new MenuItem("5");
	    fpsMenu.add(fps5);
	    fpsMenu.add(fps15);
	    fpsMenu.add(fps30);
	    fpsMenu.add(fps60);
	    
	    fps60.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				setFPS(60);
			}
	    	
	    });
	    
	    fps30.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				setFPS(30);
			}
	    });
	    
	    fps15.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				setFPS(15);
			}
	    });
	    
	    fps5.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				setFPS(5);
			}
	    });
	    
	    Menu customMenu = new Menu("Custom Algorthim");
	    MenuItem setAlgorithm = new MenuItem("Set Algorthim");
	    MenuItem randAlgorithm = new MenuItem("Random Algorithm");
	    mb.add(customMenu);
	    customMenu.add(setAlgorithm);
	    customMenu.add(randAlgorithm);
	    setAlgorithm.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String rule = JOptionPane.showInputDialog("Type in format 012345678/012345678");
				gol.processRule(rule);
			}
	    	
	    });
	    
	    randAlgorithm.addActionListener(new ActionListener(){
	    	@Override
	    	public void actionPerformed(ActionEvent e){
	    		gol.randomRule();
	    	}
	    });
	    
	    //Borderless..
		setUndecorated(true);
	    gol = new GameOfLife();
	    add(gol, BorderLayout.CENTER);
	    gol.init();
	    pack();
	}
	
	private void setFPS(int fps){
		if(gol.rThread.isPlaying){
			gol.playPressed();
			gol.fps = fps;
			gol.playPressed();
		}
		else{
			gol.fps = fps;
		}
	}
}
