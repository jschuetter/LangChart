import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;


import static java.nio.file.Files.*;

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
    JButton newLangBtn, manageLangsBtn;
    ArrayList<JButton> langBtns;
    final int LANG_ICON_W = 50, LANG_ICON_H = 50;
    JLabel langChoiceLbl;
    HashMap<JButton, Path> langDirMap;
    String newLangTitle;
    ImageIcon newLangIcon;
    File newLangIconFile;
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
            langPnl.setLayout(new BoxLayout(langPnl, BoxLayout.PAGE_AXIS));
            langDirMap = new HashMap<>();
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
            PopulateLangPnl();
            pnlLayout.show(parentPnl, "Langs");

        } else if (src == menuCreate) {

        } else if (src == newLangBtn) {
            AddLangDir();
        } else if (langDirMap.containsKey(src)) {

        }
    }

    //Updates langPnl with current data from directory storing folders of charts for each language (./charts)
    void PopulateLangPnl() {
        //Font titleFont = Font.getFont("Serif Bold Italic");
        langPnl.removeAll();
        langChoiceLbl = new JLabel("Choose A Language");
        langChoiceLbl.setFont(new Font(Font.SERIF, Font.ITALIC, 25));
        langChoiceLbl.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        langPnl.add(langChoiceLbl);

        langChoicePnl = new JPanel();
        //GridBagLayout langChoiceLayout = new GridBagLayout();
        langChoicePnl.setLayout(new FlowLayout());
        langChoicePnl.setAlignmentX(Component.CENTER_ALIGNMENT);
        //ArrayList<String> langs = new ArrayList<>();
        langBtns = new ArrayList<>();
        Path chartsPath = FileSystems.getDefault().getPath("charts");
        try (DirectoryStream<Path> langStream = newDirectoryStream(chartsPath)) {
            for (Path langEntry: langStream) {
                //langs.add(langEntry.getFileName().toString());

                //Check for directory
                BasicFileAttributes attr = Files.readAttributes(langEntry, BasicFileAttributes.class);
                if (!attr.isDirectory()) continue;
                JButton newBtn = new JButton(langEntry.getFileName().toString());

                langBtns.add(newBtn);
                langDirMap.put(newBtn, langEntry);
                String dirImgPath = FindDirImg(langEntry);
                newBtn.setIcon(getScaledIcon(dirImgPath, LANG_ICON_W, LANG_ICON_H));
                newBtn.setHorizontalTextPosition(JButton.CENTER);
                langChoicePnl.add(newBtn);
            }
        } catch (IOException e) {
            System.out.println("Directory read of dir 'charts' failed in PopulateLangPnl()");
            //throw new RuntimeException(e);
        }
        langPnl.add(langChoicePnl);

        JPanel langBtnPnl = new JPanel(new FlowLayout());
        langBtnPnl.setAlignmentX(Component.CENTER_ALIGNMENT);
        newLangBtn = new JButton("Add Language");
        newLangBtn.setIcon(getScaledIcon("resources/add.png", 10, 10));
        newLangBtn.addActionListener(this);
        langBtnPnl.add(newLangBtn);

        manageLangsBtn = new JButton("Manage languages");
        manageLangsBtn.setIcon(getScaledIcon("resources/manage.png", 10, 10));
        manageLangsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser deletePicker = new JFileChooser("./charts") {
                    @Override
                    public void approveSelection() {
                        int confirmValue = JOptionPane.showConfirmDialog(this,
                                "Are you sure you want to delete this directory?", "Confirm delete", JOptionPane.YES_NO_OPTION);
                        if (confirmValue == JOptionPane.YES_OPTION) {
                            try {
                                for (File f: this.getSelectedFiles()) {
                                    DirectoryStream<Path> stream = Files.newDirectoryStream(f.toPath());
                                    for (Path entry : stream) {
                                        Files.delete(entry);
                                    }
                                    Files.delete(f.toPath());
                                }
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }

                            PopulateLangPnl();
                            this.cancelSelection();
                        }
                    }
                };
                deletePicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                deletePicker.setMultiSelectionEnabled(true);
                deletePicker.showDialog(langPnl, "Delete");
            }
        });
        langBtnPnl.add(manageLangsBtn);

        langPnl.add(langBtnPnl);

        langPnl.repaint();
        langPnl.revalidate();
    }

    //Finds and returns first image file in directory
    //Helper function used when populating language panel
    String FindDirImg(Path p) {
        //Setup DirectoryStream filter
        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                //BasicFileAttributes attr = Files.readAttributes(p, BasicFileAttributes.class);
                return entry.toString().endsWith(".png") || entry.toString().endsWith(".jpg");
            }
        };

        //Walk through directory looking for image files
        try {
            DirectoryStream<Path> stream = newDirectoryStream(p, filter);
            for (Path entry: stream) {
//                System.out.println("Found " + entry.toString());
                return entry.toString();
            }
        } catch (IOException e) {
            System.out.println("Was unable to open directory stream at path " + p.toString());
            throw new RuntimeException(e);
        }

        return null;
    }

    //Creates a popup to prompt the user to create a new language directory
    void AddLangDir() {
        JFrame newLangPopup = new JFrame("Add a language");
        newLangPopup.setSize(400, 150);
        newLangPopup.setResizable(false);
        newLangPopup.setLayout(new BorderLayout());
        newLangPopup.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel newLangInputPnl = new JPanel();
        newLangInputPnl.setLayout(new BoxLayout(newLangInputPnl, BoxLayout.Y_AXIS));

        JPanel newLangTitlePnl = new JPanel(new FlowLayout());
        JLabel newLangTitleLbl = new JLabel("Language name:");
        JLabel newLangTitleErrLbl = new JLabel("");
        JTextField newLangTitleText = new JTextField();
        newLangTitleText.setPreferredSize(new Dimension(150, 25));
        newLangTitlePnl.add(newLangTitleLbl);
        newLangTitlePnl.add(newLangTitleText);
        newLangTitlePnl.add(newLangTitleErrLbl);
        newLangInputPnl.add(newLangTitlePnl);

        JPanel newLangIconPnl = new JPanel(new FlowLayout());
        JLabel newLangIconLbl = new JLabel("Choose icon (optional):");
        JButton newLangIconBtn = new JButton("Select file");
        newLangIconBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser filePicker = new JFileChooser();
                filePicker.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "png"));
                filePicker.showDialog(newLangPopup, "Select");
                newLangIconFile = filePicker.getSelectedFile();
            }
        });

        newLangIconPnl.add(newLangIconLbl);
        newLangIconPnl.add(newLangIconBtn);
        newLangInputPnl.add(newLangIconPnl);

        newLangPopup.add(newLangInputPnl);

        JPanel newLangBtnPnl = new JPanel(new FlowLayout());
        JButton cancelBtn, okBtn;
        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newLangPopup.setVisible(false);
                newLangPopup.dispose();
            }
        });

        okBtn = new JButton("OK");
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (newLangTitleText.getText() == null) newLangTitleErrLbl.setText("Language name must be valid!");
                else {
                    //Create directory
                    newLangTitle = newLangTitleText.getText();
                    File newDir = new File("./charts/" + newLangTitle);
                    if (!newDir.mkdir()) System.out.println("Making directory " + newDir.getName() + " failed.");

                    //Copy image file
                    if (newLangIconFile != null) {
                        newLangIcon = new ImageIcon(newLangIconFile.getPath());
                        try {
                            copy(newLangIconFile.toPath(),newDir.toPath().resolve(newLangIconFile.getName()));
                        } catch (Exception ex) {
                            //System.out.println("Failed to copy image for new directory with title " + newLangTitle);
                            throw new RuntimeException(ex);
                        }
                    }
                    //else newLangIcon = null;
                    PopulateLangPnl();
                    newLangPopup.setVisible(false);
                    newLangPopup.dispose();
                }
            }
        });

        newLangBtnPnl.add(cancelBtn);
        newLangBtnPnl.add(okBtn);
        newLangPopup.add(newLangBtnPnl, BorderLayout.SOUTH);

        newLangPopup.setVisible(true);
    }
}
