import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.List;


import static java.nio.file.Files.*;

public class Driver extends JFrame implements ActionListener {
    //Menu toolbar
    JMenuBar menuBar;
    JButton menuHome, menuOpen, menuCreate;
    final int MENU_ICON_W = 25, MENU_ICON_H = 25;
    final String LANG_DIR = "./charts";

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
    JPanel chartPnl, chartChoicePnl;
    JButton newChartBtn, manageChartsBtn, chartSelectBtn;
    Vector<String> chartNames;
    JList<String> chartTable;
    JLabel chartChoiceLbl;
    HashMap<String, Path> chartPathMap;
    //See PopulateChartPnl below

    //Chart creation panel
    //JPanel newChartDialog;
    JPanel newChartPnl;
    JTextField newChartNameText, newChartRowsText, newChartColumnsText;
    JTextField[][] chartField;
    int chartW, chartH;
    //See AddChart below

//    JFileChooser filePicker;
//    FileNameExtensionFilter filetypes = new FileNameExtensionFilter("Image files", "txt", "csv");

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
            menuHome.setIcon(GetScaledIcon("resources/home.png", MENU_ICON_W, MENU_ICON_H));
            menuHome.addActionListener(this);
            menuBar.add(menuHome);

//        menuOpen = new JButton();
//        menuOpen.setIcon(getScaledIcon("resources/open.png", MENU_ICON_W, MENU_ICON_H));
//        menuOpen.addActionListener(this);
//        menuBar.add(menuOpen);

            menuCreate = new JButton();
            menuCreate.setIcon(GetScaledIcon("resources/create.png", MENU_ICON_W, MENU_ICON_H));
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

            chartPnl = new JPanel(new BorderLayout());
            chartPathMap = new HashMap<>();
            parentPnl.add(chartPnl, "Charts");

            newChartPnl = new JPanel();
            newChartPnl.setLayout(new BoxLayout(newChartPnl, BoxLayout.Y_AXIS));
            parentPnl.add(newChartPnl, "Add");

            this.add(parentPnl, BorderLayout.CENTER);
            pnlLayout.show(parentPnl, "Langs");
        }
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new Driver();
    }

    ImageIcon GetScaledIcon(String path, int w, int h) {
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
        }  else if (src == newLangBtn) {
            AddLangDir();
//        } else if (langDirMap.containsKey(src)) {
//            PopulateChartPnl(langDirMap.get(src));
//            pnlLayout.show(parentPnl, "Charts");
        } else if (src == newChartBtn || src == menuCreate) {
            AddChart();
        } else if (src == chartSelectBtn) {
            StudyChart(chartTable.getSelectedValuesList());
        } else {
            try {
                if (langDirMap.containsKey(src)) {
                    PopulateChartPnl(langDirMap.get(src));
                    pnlLayout.show(parentPnl, "Charts");
                }
            } catch (Exception ex) {
                //Do nothing
            }

        }
    }

    //Updates langPnl with current data from directory storing folders of charts for each language (./charts)
    void PopulateLangPnl() {
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
        Path langsPath = FileSystems.getDefault().getPath("charts");
        try (DirectoryStream<Path> langStream = newDirectoryStream(langsPath)) {
            for (Path langEntry: langStream) {
                //langs.add(langEntry.getFileName().toString());

                //Check for directory
                BasicFileAttributes attr = Files.readAttributes(langEntry, BasicFileAttributes.class);
                if (!attr.isDirectory()) continue;
                JButton newBtn = new JButton(langEntry.getFileName().toString());

                langBtns.add(newBtn);
                langDirMap.put(newBtn, langEntry);
                String dirImgPath = FindDirImg(langEntry);
                newBtn.setIcon(GetScaledIcon(dirImgPath, LANG_ICON_W, LANG_ICON_H));
                newBtn.setHorizontalTextPosition(JButton.CENTER);
                newBtn.addActionListener(this);
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
        newLangBtn.setIcon(GetScaledIcon("resources/add.png", 10, 10));
        newLangBtn.addActionListener(this);
        langBtnPnl.add(newLangBtn);

        manageLangsBtn = new JButton("Manage languages");
        manageLangsBtn.setIcon(GetScaledIcon("resources/manage.png", 10, 10));
        manageLangsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser deletePicker = new JFileChooser(LANG_DIR) {
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

    //Updates chartPnl with current data from directory
    void PopulateChartPnl(Path langDir) {
        chartPnl.removeAll();
        chartChoiceLbl = new JLabel("Select A Chart", SwingConstants.CENTER);
        chartChoiceLbl.setFont(new Font(Font.SERIF, Font.ITALIC, 25));
        chartPnl.add(chartChoiceLbl, BorderLayout.NORTH);

        chartChoicePnl = new JPanel();
        //GridBagLayout langChoiceLayout = new GridBagLayout();
        chartChoicePnl.setLayout(new BorderLayout());
        chartChoicePnl.setAlignmentX(Component.CENTER_ALIGNMENT);

        chartNames = new Vector<>();
        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                //BasicFileAttributes attr = Files.readAttributes(p, BasicFileAttributes.class);
                return entry.toString().endsWith(".txt") || entry.toString().endsWith(".csv");
            }
        };
        try (DirectoryStream<Path> chartStream = newDirectoryStream(langDir, filter)) {
            for (Path chartEntry: chartStream) {
                String chartName = chartEntry.getFileName().toString().replaceFirst("[.][^.]+$", "");
                //JButton newBtn = new JButton(chartName);
                //newBtn.addActionListener(this);
                chartNames.add(chartName);
                chartPathMap.put(chartName, chartEntry);
                //chartChoicePnl.add(newBtn);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        chartTable = new JList<>(chartNames);
        chartTable.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        chartTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        chartTable.setPreferredSize(new Dimension(500, 100));
        chartTable.setToolTipText("Hold Ctrl or Shift to select multiple");
        chartChoicePnl.add(chartTable, BorderLayout.CENTER);

        chartSelectBtn = new JButton("Select chart(s)");
        chartSelectBtn.addActionListener(this);
        chartSelectBtn.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        chartChoicePnl.add(chartSelectBtn, BorderLayout.SOUTH);
        chartPnl.add(chartChoicePnl, BorderLayout.CENTER);

        JPanel chartBtnPnl = new JPanel(new FlowLayout());
        chartBtnPnl.setAlignmentX(Component.CENTER_ALIGNMENT);
        newChartBtn = new JButton("Add chart");
        newChartBtn.setIcon(GetScaledIcon("resources/add.png", 10, 10));
        newChartBtn.addActionListener(this);
        chartBtnPnl.add(newChartBtn);

        manageChartsBtn = new JButton("Manage charts");
        manageChartsBtn.setIcon(GetScaledIcon("resources/manage.png", 10, 10));
        manageChartsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser deletePicker = new JFileChooser(LANG_DIR) {
                    @Override
                    public void approveSelection() {
                        int confirmValue = JOptionPane.showConfirmDialog(this,
                                "Are you sure you want to delete this file?", "Confirm delete", JOptionPane.YES_NO_OPTION);
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

                            PopulateChartPnl(langDir);
                            this.cancelSelection();
                        }
                    }
                };
                deletePicker.setMultiSelectionEnabled(true);
                deletePicker.showDialog(chartPnl, "Delete");
            }
        });
        chartBtnPnl.add(manageChartsBtn);

        chartPnl.add(chartBtnPnl, BorderLayout.SOUTH);

        chartPnl.repaint();
        chartPnl.revalidate();
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

    //Opens the chart creation interface and saves a new chart
    void AddChart(){
        newChartPnl.removeAll();

        JPanel newChartPopup = new JPanel();
        newChartPopup.setLayout(new BoxLayout(newChartPopup, BoxLayout.Y_AXIS));
        //newChartPopup.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JLabel newChartPopupTitleLbl = new JLabel("Chart dimensions:", JLabel.CENTER);
        newChartPopupTitleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        newChartPopup.add(newChartPopupTitleLbl);

        JPanel chartInfoPnl2 = new JPanel(new FlowLayout());
        newChartRowsText = new JTextField("4", 3);
        chartInfoPnl2.add(newChartRowsText);
        chartInfoPnl2.add(new JLabel("rows x", JLabel.RIGHT));
        newChartColumnsText = new JTextField("3", 3);
        chartInfoPnl2.add(newChartColumnsText);
        chartInfoPnl2.add(new JLabel("columns", JLabel.RIGHT));
        newChartPopup.add(chartInfoPnl2);

        JLabel newChartErrLbl = new JLabel();
        newChartPopup.add(newChartErrLbl);



        //boolean goodDimensions = false;
        while (JOptionPane.showConfirmDialog(newChartPnl, newChartPopup, "Chart options",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)
                        == JOptionPane.OK_OPTION)
        {

                //Test input values
                try {
                    chartH = Integer.parseInt(newChartRowsText.getText());
                    chartW = Integer.parseInt(newChartColumnsText.getText());
                    if (chartW > 0 && chartH > 0) {
                        //Open chart creation interface
                        chartField = new JTextField[chartH][chartW];
                        JPanel chartFieldPnl = new JPanel(new GridLayout(chartH, chartW));

                        for (int i = 0; i < chartH; i++) {
                           for (int j = 0; j < chartW; j++) {
                               chartField[i][j] = new JTextField();
                               //Disable commas in text entries (would disrupt .csv formatting)
                               chartField[i][j].getInputMap(JComponent.WHEN_FOCUSED).put(
                                       KeyStroke.getKeyStroke("typed ,"), "none");
                               chartFieldPnl.add(chartField[i][j]);
                           }
                        }
                        newChartPnl.add(chartFieldPnl);

                        JPanel newChartBtnPnl = new JPanel(new FlowLayout());
                        JButton newChartConfirmBtn, newChartCancelBtn;
                        newChartCancelBtn = new JButton("Cancel");
                        newChartCancelBtn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                PopulateLangPnl();
                                pnlLayout.show(parentPnl, "Langs");
                            }
                        }); //Return to most recent chart page
                        newChartBtnPnl.add(newChartCancelBtn);

                        newChartConfirmBtn = new JButton("Save chart");
                        newChartConfirmBtn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                CreateSaveChartDialog();
                            }
                        });
                        newChartBtnPnl.add(newChartConfirmBtn);
                        newChartPnl.add(newChartBtnPnl);

                        pnlLayout.show(parentPnl, "Add");
                        newChartPnl.repaint();
                        newChartPnl.revalidate();
                        break;

                    } else {
                        int breakMe = 1/0; //Call catch statement
                    }
                } catch (Exception e) {
                    newChartErrLbl.setText("Invalid dimensions! (must be positive integers)");
                    System.out.println(e);
                }
        }
    }
    
    void CreateSaveChartDialog() {
        //Ask user where to save file
        JFrame saveChartPopup = new JFrame("Save chart");
        saveChartPopup.setSize(400, 200);
        saveChartPopup.setLayout(new BoxLayout(saveChartPopup.getContentPane(), BoxLayout.Y_AXIS));
        JPanel saveChartPnl1 = new JPanel(new FlowLayout());
        JPanel saveChartPnl2 = new JPanel(new FlowLayout());
        
        saveChartPnl1.add(new JLabel("Chart name:"));
        newChartNameText = new JTextField("", 10);
        saveChartPnl1.add(newChartNameText);
        JLabel newChartNameErrLbl = new JLabel();
        saveChartPnl1.add(newChartNameErrLbl);
        saveChartPopup.add(saveChartPnl1);
        
        saveChartPnl2.add(new JLabel("Choose language:"));
        JLabel saveChartSelectedLbl = new JLabel();
        JButton saveChartDirBtn = new JButton("Select folder");
        saveChartDirBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser filePicker = new JFileChooser(LANG_DIR);
                filePicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                filePicker.showDialog(newChartPnl, "Select");
                saveChartSelectedLbl.setText(filePicker.getSelectedFile().getName());

                String parentDir = filePicker.getSelectedFile().getParentFile().getName();
                String validDir = new File(LANG_DIR).getName();
                if (!Objects.equals(parentDir, validDir))
                    saveChartSelectedLbl.setText("Invalid directory! (must be in /charts/ folder)");
            }
        });
        saveChartPnl2.add(saveChartDirBtn);
        saveChartPnl2.add(saveChartSelectedLbl);
        saveChartPopup.add(saveChartPnl2);

        JPanel saveChartBtnPnl = new JPanel(new FlowLayout());
        JButton cancelBtn, saveBtn, addLangBtn;
        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveChartPopup.setVisible(false);
                saveChartPopup.dispose();
            }
        });
        saveBtn = new JButton("Save");
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!newChartNameText.getText().isEmpty()) {
                    File chartPath = new File(LANG_DIR + "/" + saveChartSelectedLbl.getText() + "/" +
                            newChartNameText.getText()+".txt");
                    try {
                        if (chartPath.createNewFile()) {
                            FileWriter saveFile = new FileWriter(chartPath);
                            for (int i = 0; i < chartH; i++) {
                                for (int j = 0; j < chartW; j++) {
                                    saveFile.append(chartField[i][j].getText());
                                    saveFile.append(',');
                                }
                                saveFile.append('\n');
                            }
                            saveFile.close();

                            saveChartPopup.setVisible(false);
                            saveChartPopup.dispose();

                            PopulateChartPnl(Path.of(LANG_DIR + "/" + saveChartSelectedLbl.getText()));
                            pnlLayout.show(parentPnl, "Charts");
                        } else {
                            newChartNameErrLbl.setText("Chart already exists!");
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    newChartNameErrLbl.setText("Please enter a valid chart name!");
                }
            }
        });
        addLangBtn = new JButton(GetScaledIcon("resources/add.png", 10, 10));
        addLangBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddLangDir();
            }
        });

        saveChartBtnPnl.add(cancelBtn);
        saveChartBtnPnl.add(saveBtn);
        saveChartBtnPnl.add(addLangBtn);
        saveChartPopup.add(saveChartBtnPnl);

        saveChartPopup.setVisible(true);
    }

    void StudyChart(List<String> charts) {

    }
//    
//    JFileChooser GetFileChooser(File startDir) {
//        JFileChooser filePicker = new JFileChooser(startDir) {
//            @Override
//            public void approveSelection() {
//                int confirmValue = JOptionPane.showConfirmDialog(this,
//                        "Are you sure you want to delete this directory?", "Confirm delete", JOptionPane.YES_NO_OPTION);
//                if (confirmValue == JOptionPane.YES_OPTION) {
//                    try {
//                        for (File f: this.getSelectedFiles()) {
//                            DirectoryStream<Path> stream = Files.newDirectoryStream(f.toPath());
//                            for (Path entry : stream) {
//                                Files.delete(entry);
//                            }
//                            Files.delete(f.toPath());
//                        }
//                    } catch (IOException ex) {
//                        throw new RuntimeException(ex);
//                    }
//
//                    PopulateLangPnl();
//                    this.cancelSelection();
//                }
//            }
//    }
}
