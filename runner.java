import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import net.sourceforge.tess4j.Tesseract;

public class runner {
	public static List<Rectangle> a;
	public static boolean[] empty;
	public static String location;
	public static BufferedImage Mat2BufferedImage(Mat matrix)throws Exception {        
	    MatOfByte mob=new MatOfByte();
	    Imgcodecs.imencode(".jpg", matrix, mob);
	    byte ba[]=mob.toArray();

	    BufferedImage bi=ImageIO.read(new ByteArrayInputStream(ba));
	    return bi;
	} 
	public static Sudoku getInput() throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		JFrame f = new JFrame("Input");
		JLabel locationl =new JLabel("File Location:");
	    locationl.setBounds(20, 20, 80, 30);
	    final JTextField locationt = new JTextField();  
	    locationt.setBounds(100, 20, 400, 30);
	    JButton b = new JButton("Enter"); 
	    b.setBounds(260, 125, 80, 30);
	    f.add(locationt);
	    f.add(locationl);
	    f.add(b);
	    f.setSize(600,200);    
	    f.setLayout(null);    
	    f.setVisible(true);
	    while(f.isVisible()) {
	    	if (locationt.getText()!=null) {
			    b.addActionListener(new ActionListener() {
			    	public void actionPerformed(ActionEvent e) {
			    		f.setVisible(false);
			        }
			    });
	    	}
	    }
    	location = locationt.getText();
		Mat image = Imgcodecs.imread(location);
		Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
        Tesseract tessInst = new Tesseract();
        tessInst.setDatapath("C://Tess4J");
        tessInst.setTessVariable("tessedit_char_whitelist", "0123456789");
        BufferedImage img = Mat2BufferedImage(image);
        a = tessInst.getSegmentedRegions(img, 4);
        for(Rectangle rect: a) {
        	Imgproc.rectangle(image, new Point(rect.getX(), rect.getY()), new Point(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight()), new Scalar(255, 255, 255), 20);
        }
        img = Mat2BufferedImage(image);
        int x=0;
        int y=0;
        Integer[][] input = new Integer[(int)Math.sqrt(a.size())][(int)Math.sqrt(a.size())];
        empty = new boolean[a.size()];
        int c=0;
        for(Rectangle rect: a) {
        	String p = tessInst.doOCR(img,rect);
        	if(x>=(int)Math.sqrt(a.size())) {
        		x=0;
        		y++;
        	}
        	if(p.length()>0) {
        		try {
        			input[y][x] = Integer.parseInt(p.substring(0,1));
        		}
        		catch (Exception e) {}
        	}
        	else {
        		empty[c] = true;
        	}
        	x++;
        	c++;
        }
        return new Sudoku(input);
	}
	public static void print(Sudoku s) {
		Integer[][] p = s.getSolution();
		System.out.println();
		for(int n=0; n<(4*p	.length)+1; n++) {
			System.out.print("=");
		}
		for(int y=0; y<p.length; y++) {
			System.out.println();
			System.out.print("|");
			for(int x=0; x<p.length; x++) {
				if(p[y][x]==null) {
					System.out.print("   |");
				}
				else {
					System.out.print(" "+p[y][x]+" |");
				}
			}
			System.out.println();
			for(int n=0; n<(4*p.length)+1; n++) {
				System.out.print("=");
			}
		}
	}
	public static void mark(Sudoku s) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat image = Imgcodecs.imread(location);
        int x=0;
        int y=0;
        int c=0;
        Integer[][] solution = s.getSolution();
		for(Rectangle rect: a) {
        	if(x>=(int)Math.sqrt(a.size())) {
        		x=0;
        		y++;
        	}
        	if(empty[c]==true) {
        		Imgproc.putText(image,solution[y][x].toString(), new Point(rect.getX() + rect.getWidth()/4, rect.getY() + rect.getHeight()*0.75), 1, 2, new Scalar(0, 0, 0));
        	}
        	x++;
        	c++;
        }
		Imgcodecs.imwrite("./output/output.jpg", image);
		HighGui.imshow("Output", image);
		HighGui.waitKey(0);
	}
	public static void main(String[] a) throws Exception {
		Sudoku s = getInput();
		print(s);
		s.possible();
		print(s);
		mark(s);
	}
}
