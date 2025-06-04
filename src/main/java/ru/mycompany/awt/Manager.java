package ru.mycompany.awt;

import org.openqa.selenium.WebDriverException;

import java.util.ArrayList;
import java.util.List;

public class Manager implements Runnable{
  private GeneralWindow generalWindow;
  private boolean isStop;
  private List<String> sites;

  public Manager(List<String> sites, GeneralWindow generalWindow){
    this.generalWindow = generalWindow;
    this.sites = sites;
    this.isStop = true;
  }

  public boolean isStop() {
    return isStop;
  }

  public void setStop(boolean stop) {
    isStop = stop;
  }

  public void run() throws WebDriverException {
//     = Arrays.asList(
//            "seosprings",
//            "goldenclicks",//+
//            "seojump"---
//            "seogold",//+
//            "profitcentr24",//+
//            "seobux",
//            "prodvisots",
//            "seo24"====
//            "soofast",
//            "profitcentr",
//            "seofast"
//            "seoclub",
//            "wmrfast"
//            "sarseo"
//    );
    List<Task> tasks = new ArrayList<>();
    for (String site : sites){
      tasks.add(new Task(site));
    }
    while (true){
      for (Task task : tasks){
        Thread performanceTask = new Thread(task);
        performanceTask.start();
          try {
              performanceTask.join();
          } catch (InterruptedException e) {
              throw new RuntimeException(e);
          }
          if (isStop){
          System.out.println("stoped");
          generalWindow.setLabelButton();
          return;
        }
      }
    }
  }
}

