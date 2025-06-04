package ru.mycompany.examples;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Manager {

  public void start() throws InterruptedException, WebDriverException {
    List<String> sites = Arrays.asList(
//            "seosprings",
//            "goldenclicks",//+
//            "seojump"---
//            "seogold",//+
//            "profitcentr24",//+
//            "seobux",
//            "prodvisots",
//            "seo24"====
//            "soofast",
            "profitcentr",
            "seofast"
//            "seoclub",
//            "wmrfast"
//            "sarseo"
    );
    List<Task> tasks = new ArrayList<>();
    for (String site : sites){
      tasks.add(new Task(site));
    }
    while (true){
      for (Task task : tasks){
        Thread performanceTask = new Thread(task);
        performanceTask.start();
        performanceTask.join();
      }
    }
  }
}

