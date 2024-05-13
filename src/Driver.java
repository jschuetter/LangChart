import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Driver extends JFrame implements ActionListener {
    JMenuBar menuBar;
    JButton menuHome, menuOpen, menuCreate;
    final int MENU_ICON_W = 25, MENU_ICON_H = 25;

    JPanel parentPnl, langSelect, chartSelect;
    JFileChooser filePicker;
    FileNameExtensionFilter filetypes = new FileNameExtensionFilter("Image files", "txt", "csv");

    Driver() {
        this.setTitle("LangChart");
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(700,500);

        menuBar = new JMenuBar();
        //menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        menuHome = new JButton();
        menuHome.setIcon(getScaledIcon("resources/home.png", MENU_ICON_W, MENU_ICON_H));
        menuHome.addActionListener(this);
        menuBar.add(menuHome);

        menuOpen = new JButton();
        menuOpen.setIcon(getScaledIcon("resources/open.png", MENU_ICON_W, MENU_ICON_H));
        menuOpen.addActionListener(this);
        menuBar.add(menuOpen);

        menuCreate = new JButton();
        menuCreate.setIcon(getScaledIcon("resources/create.png", MENU_ICON_W, MENU_ICON_H));
        menuCreate.addActionListener(this);
        menuBar.add(menuCreate);

        this.add(menuBar, BorderLayout.NORTH);

        parentPnl = new JPanel();
        parentPnl.setLayout(new CardLayout());
        langSelect = new JPanel();

        parentPnl.add(langSelect, "langSelect");
        parentPnl.add(chartSelect, "chartSelect");

        this.setVisible(true);
    }

    public static void main(String[] args) {
        new Driver();
    }

    ImageIcon getScaledIcon (String path, int w, int h) {
        Image orig = new ImageIcon(path).getImage();
        Image scaled = orig.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        ImageIcon ret = new ImageIcon(scaled);
        return ret;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == menuHome) {

        } else if (src == menuOpen) {

        } else if (src == menuCreate) {

        }
    }
}






