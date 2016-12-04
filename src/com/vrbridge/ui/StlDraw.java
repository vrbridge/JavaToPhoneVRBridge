package com.vrbridge.ui;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.vrbridge.geom.Matrix3d;
import com.vrbridge.geom.Model3Dim;
import com.vrbridge.geom.Tuple3d;
import com.vrbridge.io.ComputerClient;
import com.vrbridge.io.PhoneInterface;

public class StlDraw extends JFrame implements KeyListener {
	private Model3Dim model;
    private double scale = 20;
    private double alfa = 0;
    private double beta = 0;
    private BufferedImage image = null;
    // Phone
    private static final boolean sendToPhone = true;
    private int phoneW = -1;
    private int phoneH = -1;
    //private BufferedImage phoneImage;
    //private int[] phonePixels = null;
    private PhoneInterface phoneIntr = null;
	//private Model3Dim phoneModel;

	private JPanel panel = new JPanel() {
    	@Override
    	protected void paintComponent(Graphics g) {
    		super.paintComponent(g);
            int w = this.getWidth();
            int h = this.getHeight();
            paintViewport(g, w, h);
    	}
    };

    public StlDraw() throws Exception {
        StlLoader f = new StlLoader(new File("Roman.stl"));
        Tuple3d[] coords =  f.getCoordArray();
        Tuple3d[] normals = f.getNormArray();
        int count = coords.length / 3;
        List<Tuple3d> coordList = new ArrayList<Tuple3d>();
        List<Tuple3d> normalList = new ArrayList<Tuple3d>();
        double minX = -20;
        double maxX = 20;
        double minY = -20;
        double maxY = 20;
        double minZ = -10;
        double maxZ = 20;
		double cr1 = Math.PI * -11.0 / 180.0;
		double cr2 = Math.PI * 2.0 / 180.0;
		Matrix3d crm = Matrix3d.mult(new Matrix3d(  // Outer matrix
				1, 0, 0,
				0, Math.cos(cr2), Math.sin(cr2),
				0, -Math.sin(cr2), Math.cos(cr2)), 
				new Matrix3d(  // Inner matrix
						Math.cos(cr1), 0, Math.sin(cr1),
						0, 1, 0,
						-Math.sin(cr1), 0, Math.cos(cr1)));
        for (int n = 0; n < count; n++) {
        	boolean masked = false;
        	List<Tuple3d> subList = new ArrayList<Tuple3d>();
        	for (int i = 0; i < 3; i++) {
        		Tuple3d t = new Tuple3d(coords[n * 3 + i]); 
        		t = crm.mult(t);
        		t.z -= 7;
        		subList.add(t);
        		if (t.x < minX || t.x > maxX || t.y < minY || t.y > maxY || 
        				t.z < minZ || t.z > maxZ) {
        			masked = true;
        		}
        	}
        	if (!masked) {
        		coordList.addAll(subList);
        		Tuple3d norm = normals[n];
        		normalList.add(crm.mult(new Tuple3d(norm.x, norm.y, norm.z)));
        	}
        }
        this.model = new Model3Dim(normalList.toArray(new Tuple3d[normalList.size()]), 
        		coordList.toArray(new Tuple3d[coordList.size()]));
        this.model = this.model.asIOData().asModel();
        if (sendToPhone) {
			String host = "192.168.1.64";
			ComputerClient cc = new ComputerClient(host, 33233);
			phoneIntr = cc.makeWrapper(PhoneInterface.class);
			int[] scrRes = phoneIntr.getScreenResolution();
			phoneW = scrRes[0];
			phoneH = scrRes[1];
			//phoneImage = new BufferedImage(phoneW, phoneH, BufferedImage.TYPE_INT_RGB);
			//phonePixels = ((DataBufferInt)phoneImage.getRaster().getDataBuffer()).getData();
			//phoneModel = new Model3Dim(model.getNormals(), model.getCoords());
			phoneIntr.setModel(model.asIOData());
        }
        System.out.println("Number of triangles: " + normalList.size());
        this.getContentPane().add(panel);
        this.setSize(1000, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(this);
        this.panel.addKeyListener(this);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
	}

	protected void paintViewport(Graphics g, int w, int h) {
    	if (image == null || image.getWidth() != w || image.getHeight() != h) {
    		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    	}
        long t1 = System.currentTimeMillis();
        int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
        model.draw(pixels, w, h, alfa, beta, scale);
        System.out.println("Inner time: " + (System.currentTimeMillis() - t1) + " ms.");
		g.drawImage(image, 0, 0, w, h, null);
		if (sendToPhone) {
			try {
				/*phoneModel.draw(phonePixels, phoneW, phoneH, alfa, beta, scale);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(phoneImage, "JPG", os);
				os.close();
				byte[] img = os.toByteArray();
				phoneIntr.showImage(img);*/
		        long t2 = System.currentTimeMillis();
				phoneIntr.changeView(alfa, beta, scale);
		        System.out.println("Phone time: " + (System.currentTimeMillis() - t2) + " ms.");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		char key = e.getKeyChar();
	    if (key == 'a') {
	    	alfa += Math.PI / 45;
	    } else if (key == 'd') {
	    	alfa -= Math.PI / 45;
	    } else if (key == 'q') {
	    	scale *= 1.1;
	    } else if (key == 'e') {
	    	scale /= 1.1;
	    } else if (key == 'w') {
	    	beta += Math.PI / 45;
	    } else if (key == 's') {
	    	beta -= Math.PI / 45;
	    }
	    panel.repaint();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	public static void main(String[] args) throws Exception {
        new StlDraw().setVisible(true);
	}
	
	private static Tuple3d norm(Tuple3d p1, Tuple3d p2, Tuple3d p3) {
		Tuple3d v1 = Tuple3d.mult(p2, -1);
		v1.add(p1);
		Tuple3d v2 = Tuple3d.mult(p3, -1);
		v2.add(p1);
		Tuple3d norm = Tuple3d.cross(v1, v2);
    	norm.scale(1 / norm.len());
    	return norm;
	}
	
}
