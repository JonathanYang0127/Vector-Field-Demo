import javax.swing.JButton;
import javax.swing.JFrame;


@SuppressWarnings("serial")
public class GraphicsFrame extends JFrame {
	
	public static void main(String[] args) {
		new GraphicsFrame();
	}
	
	public GraphicsFrame() {
		super("Vector Field Demo");
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		this.add(new GraphicsPanel());
		this.pack();
		this.setVisible(true);
	}
	
	
}