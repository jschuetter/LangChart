import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;

import static java.nio.file.Files.newDirectoryStream;

public class Driver extends JFrame implements ActionListener {
    //Menu toolbar
    JMenuBar menuBar;
    JButton menuHome, menuOpen, menuCreate;
    final int MENU_ICON_W = 25, MENU_ICON_H = 25;

    //Main view panel & layout
    JPanel parentPnl;
    CardLayout pnlLayout = new CardLayout();

    //Language select panel
    JPanel langPnl, langChoicePnl;
    JButton newLangBtn;
    ArrayList<JButton> langBtns;
    //see PopulateLangPnl below

    //Chart select panel
    JPanel chartPnl;
    //See PopulateChartPnl below

    JFileChooser filePicker;
    FileNameExtensionFilter filetypes = new FileNameExtensionFilter("Image files", "txt", "csv");

    Driver() {
        this.setTitle("LangChart");
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(700,500);

        //Instantiate toolbar
        {
            menuBar = new JMenuBar();
            //menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));
            menuHome = new JButton();
            menuHome.setIcon(getScaledIcon("resources/home.png", MENU_ICON_W, MENU_ICON_H));
            menuHome.addActionListener(this);
            menuBar.add(menuHome);

//        menuOpen = new JButton();
//        menuOpen.setIcon(getScaledIcon("resources/open.png", MENU_ICON_W, MENU_ICON_H));
//        menuOpen.addActionListener(this);
//        menuBar.add(menuOpen);

            menuCreate = new JButton();
            menuCreate.setIcon(getScaledIcon("resources/create.png", MENU_ICON_W, MENU_ICON_H));
            menuCreate.addActionListener(this);
            menuBar.add(menuCreate);

            this.add(menuBar, BorderLayout.NORTH);
        }

        //Instantiate panels
        {
            parentPnl = new JPanel();
            parentPnl.setLayout(pnlLayout);

            langPnl = new JPanel();
            langPnl.setLayout(new BoxLayout(langPnl, BoxLayout.Y_AXIS));
            PopulateLangPnl();
            parentPnl.add(langPnl, "Langs");

            chartPnl = new JPanel();
            parentPnl.add(chartPnl, "Charts");

            this.add(parentPnl, BorderLayout.CENTER);
            pnlLayout.show(parentPnl, "Langs");
        }
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

        } else if (src == menuCreate) {

        } else if (src == newLangBtn) {

        }
    }

    void PopulateLangPnl() {
        langChoicePnl = new JPanel();
        langChoicePnl.setLayout(new FlowLayout());
        //ArrayList<String> langs = new ArrayList<>();
        langBtns = new ArrayList<>();
        Path chartsPath = FileSystems.getDefault().getPath("charts");
        try (DirectoryStream<Path> langStream = newDirectoryStream(chartsPath)) {
            for (Path langEntry: langStream) {
                //langs.add(langEntry.getFileName().toString());
                langBtns.add(new JButton(langEntry.getFileName().toString()));
                langChoicePnl.add(langBtns.getLast());
            }
        } catch (IOException e) {
            System.out.println("Directory read of dir 'charts' failed in PopulateLangPnl()");
            //throw new RuntimeException(e);
        }
        langPnl.add(langChoicePnl);

        newLangBtn = new JButton("Add Language");
        newLangBtn.setIcon(getScaledIcon("resources/add.png", 10, 10));
        //newLangBtn.setMaximumSize(newLangBtn.getPreferredSize());
        newLangBtn.addActionListener(this);
        langPnl.add(newLangBtn);
    }
}
