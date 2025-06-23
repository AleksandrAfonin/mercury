package ru.mycompany.awt;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class GeneralWindow extends Frame {
    private GeneralWindow generalWindow;
    private Frame frame;
    private Label sitesLabel;
    private Label accountsLabel;
    private Label imageLabel;
    private Choice choiceSites;
    private Choice choiceAccounts;
    private Choice choiceImage;
    private Button addSiteButton;
    private Button removeSiteButton;
    private Button startButton;
    private Button stopButton;
    private Button upButton;
    private Button downButton;
    private Button leftButton;
    private Button rightButton;
    private TextArea textArea;
    private List list;
    private Manager manager;
    private BufferedImage srcImage;
    private BufferedImage image;
    private BufferedImage subImage;
    private Canvas canvas;
    private int imageX;
    private int imageY;



    public GeneralWindow(){
        initialization(800, 600);
    }

    public GeneralWindow(int width, int height){
        initialization(width, height);
    }

    private void initialization(int width, int height){
        imageX = 0;
        imageY = 0;
        generalWindow = this;
        SQLiteProvider sqLiteProvider = SQLiteProvider.getInstance();
        frame = new Frame("Mercury");
        frame.setLayout(null);// null
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
        });

        choiceAccounts = new Choice();
        choiceAccounts.setSize(200, 1);
        choiceAccounts.setLocation(300, 100);
        getAccounts(sqLiteProvider);

        imageLabel = new Label("Скриншот");
        imageLabel.setSize(100, 20);
        imageLabel.setLocation(300, 150);

        choiceImage = new Choice();
        choiceImage.setSize(200, 1);
        choiceImage.setLocation(300, 170);
        choiceImage.addItem("");
        File pathImages = new File(new File(".", "image"), "im");
        File[] files = pathImages.listFiles();
        for (File fl : files) {
            choiceImage.addItem(fl.getName());
        }

        choiceImage.addItemListener(e -> {
            if (!choiceImage.getSelectedItem().isBlank()){
                File pathImage = new File(new File(new File(".", "image"), "im"), choiceImage.getSelectedItem());
                try {
                    srcImage = ImageIO.read(pathImage);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                int w = srcImage.getWidth(null) * 10;
                int h = srcImage.getHeight(null) * 10;
                image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics graphics = image.createGraphics();
                graphics.drawImage(srcImage.getScaledInstance(w, h, BufferedImage.TYPE_INT_RGB), 0, 0, null);

                if (image.getWidth() < imageX + 300){
                    imageX = image.getWidth() - 300;
                    if (imageX < 0){
                        imageX = 0;
                    }
                }
                if (image.getHeight() < imageY + 300){
                    imageY = image.getHeight() - 300;
                    if (imageY < 0){
                        imageY = 0;
                    }
                }
                showSubimage();
                setEnableTrue();
            }else{
                Graphics gr = canvas.getGraphics();
                gr.clearRect(0, 0, 301, 301);
                setEnableFalse();
            }
        });

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
                if (manager != null){
                    stopButton.setLabel("Stoping");
                    manager.setStop(true);
                }
            }
        });

        upButton = new Button("Up");
        upButton.setSize(50, 30);
        upButton.setLocation(400, 510);
        upButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                imageY -= 100;
                if (imageY < 0){
                    imageY = 0;
                }
                showSubimage();
            }
        });

        downButton = new Button("Down");
        downButton.setSize(50, 30);
        downButton.setLocation(450, 510);
        downButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                imageY += 100;
                if (imageY > image.getHeight() - 300){
                    imageY = image.getHeight() - 300;
                    if (imageY < 0){
                        imageY = 0;
                    }
                }
                showSubimage();
            }
        });

        leftButton = new Button("Left");
        leftButton.setSize(50, 30);
        leftButton.setLocation(500, 510);
        leftButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                imageX -= 100;
                if (imageX < 0){
                    imageX = 0;
                }
                showSubimage();
            }
        });

        rightButton = new Button("Right");
        rightButton.setSize(50, 30);
        rightButton.setLocation(550, 510);
        rightButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                imageX += 100;
                if (imageX > image.getWidth() - 300){
                    imageX = image.getWidth() - 300;
                    if (imageX < 0){
                        imageX = 0;
                    }
                }
                showSubimage();
            }
        });

        setEnableFalse();

        canvas = new Canvas();
        canvas.setSize(301, 301);
        canvas.setLocation(300, 200);
        canvas.setBackground(Color.GRAY);
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                canvas.setEnabled(false);
                int x = (e.getX() + imageX) / 10;
                int y = (e.getY() + imageY) / 10;
                String fullName = choiceImage.getSelectedItem();
                String[] strings = fullName.split("\\.")[0].split("_");
                System.out.println(strings[0] + " " + strings[1] + " " + strings[2] + " " + strings[3]);
                String[] wh = strings[3].split("x");
                int w = Integer.parseInt(wh[0]);
                int h = Integer.parseInt(wh[1]);
                if (srcImage.getWidth() < x + w || srcImage.getHeight() < y + h){
                    return;
                }
                BufferedImage img = srcImage.getSubimage(x, y, w, h);
                SQLiteProvider.getInstance().insertSprite(img, strings);
                File pathImage = new File(new File(new File(".", "image"), "im"), fullName);
                pathImage.delete();
                choiceImage.remove(fullName);
                Graphics gr = canvas.getGraphics();
                gr.clearRect(0, 0, 301, 301);
                setEnableFalse();

                File path = new File(new File(new File(".", "sprites"), "pic"), "sprite.png");
                try {
                    ImageIO.write(img, "png", path);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                canvas.setEnabled(true);
            }
        });

        frame.add(choiceSites);
        frame.add(choiceAccounts);
        frame.add(choiceImage);
        frame.add(sitesLabel);
        frame.add(accountsLabel);
        frame.add(imageLabel);
        frame.add(list);
        frame.add(addSiteButton);
        frame.add(removeSiteButton);
        frame.add(startButton);
        frame.add(stopButton);
        frame.add(canvas);
        frame.add(upButton);
        frame.add(downButton);
        frame.add(leftButton);
        frame.add(rightButton);
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

    private void setEnableTrue(){
        upButton.setEnabled(true);
        downButton.setEnabled(true);
        leftButton.setEnabled(true);
        rightButton.setEnabled(true);
    }

    private void setEnableFalse(){
        upButton.setEnabled(false);
        downButton.setEnabled(false);
        leftButton.setEnabled(false);
        rightButton.setEnabled(false);
    }

    private void showSubimage(){
        if (image == null){
            return;
        }
        subImage = image.getSubimage(imageX, imageY, 300, 300);
        Graphics gr = canvas.getGraphics();
        gr.drawImage(subImage, 0, 0, null);
        for (int i = 0; i < 310; i += 10) {
            gr.drawLine(i, 0, i, 300);
            gr.drawLine(0, i, 300, i);
        }
        gr.dispose();
    }
}
