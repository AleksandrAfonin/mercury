package ru.mycompany.awt;

import ru.mycompany.examples.User;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class GeneralWindow extends Frame {
    private GeneralWindow generalWindow;
    private Frame frame;
    private Label sitesLabel;
    private Label accountsLabel;
    private Choice choiceSites;
    private Choice choiceAccounts;
    private Button addSiteButton;
    private Button removeSiteButton;
    private Button startButton;
    private Button stopButton;
    private TextArea textArea;
    private List list;
    private Manager manager;



    public GeneralWindow(){
        initialization(800, 600);
    }

    private void initialization(int width, int height){
        generalWindow = this;
        SQLiteProvider sqLiteProvider = SQLiteProvider.getInstance();
        frame = new Frame("Mercury");
        frame.setLayout(null);
        frame.setSize(width, height);
        frame.setMinimumSize(new Dimension(width, height));
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        sitesLabel = new Label("Сайты");
        sitesLabel.setSize(100, 20);
        sitesLabel.setLocation(20, 80);
        accountsLabel = new Label("Аккаунт");
        accountsLabel.setSize(100, 20);
        accountsLabel.setLocation(300, 80);

        choiceSites = new Choice();
        choiceSites.setSize(200, 1);
        choiceSites.setLocation(20, 100);
        choiceSites.addItem("");
        for (String item : sqLiteProvider.getNameSites()){
            choiceSites.addItem(item);
        }
        choiceSites.addItemListener(e -> {
            getAccounts(sqLiteProvider);
            System.out.println(choiceSites.getSelectedItem());
        });

        choiceAccounts = new Choice();
        choiceAccounts.setSize(200, 1);
        choiceAccounts.setLocation(300, 100);
        getAccounts(sqLiteProvider);

        textArea = new TextArea("", 10, 50, TextArea.SCROLLBARS_NONE);
        textArea.setSize(200, 200);
        textArea.setLocation(20, 200);

        list = new List();
        list.setSize(200, 200);
        list.setLocation(20, 200);

        addSiteButton = new Button("Add site");
        addSiteButton.setSize(100, 30);
        addSiteButton.setLocation(20, 400);
        addSiteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!choiceSites.getSelectedItem().isBlank()){
                    list.add(choiceSites.getSelectedItem());
                    System.out.println(list.getItemCount());
                }
            }
        });

        removeSiteButton = new Button("Remove");
        removeSiteButton.setSize(100, 30);
        removeSiteButton.setLocation(120, 400);
        removeSiteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = list.getSelectedIndex();
                if (index > -1){
                    list.remove(index);
                    System.out.println(list.getItemCount());
                }
            }
        });

        startButton = new Button("Start");
        startButton.setSize(100, 30);
        startButton.setLocation(20, 430);
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int count = list.getItemCount();
                if (count == 0){
                    return;
                }
                startButton.setEnabled(false);
                stopButton.setLabel("Stop");
                java.util.List<String> sites = Arrays.asList(list.getItems());
                manager = new Manager(sites, generalWindow);
                manager.setStop(false);
                new Thread(manager).start();
            }
        });

        stopButton = new Button("Stop");
        stopButton.setSize(100, 30);
        stopButton.setLocation(120, 430);
        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                stopButton.setLabel("Stoping");
                manager.setStop(true);
            }
        });

        frame.add(choiceSites);
        frame.add(choiceAccounts);
        frame.add(sitesLabel);
        frame.add(accountsLabel);
        frame.add(list);
        frame.add(addSiteButton);
        frame.add(removeSiteButton);
        frame.add(startButton);
        frame.add(stopButton);
        frame.setVisible(true);
    }

    private void getAccounts(SQLiteProvider sqLiteProvider){
        choiceAccounts.removeAll();
        for (User user : sqLiteProvider.getAccountsList(choiceSites.getSelectedItem())){
            choiceAccounts.addItem(user.getLogin());
        }
    }

    public void setLabelButton(){
        stopButton.setLabel("Stoped");
        startButton.setEnabled(true);
    }
}
