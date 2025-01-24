package ru.mycompany.examples;

import java.io.*;
import java.util.Scanner;

public class EnterImageToFile {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    String filename;
    File fileRoot = new File(".", "captcha");
    File file;
    while (true) {
      while (true) {
        System.out.print("Файл: ");
        filename = scanner.nextLine() + ".txt";
        file = new File(fileRoot, filename);
        if (file.exists()) {
          break;
        }
        System.out.println("Нет такого файла !");
      }

      System.out.print("Данные: ");
      String data = scanner.nextLine();

      if (!isExist(file, data)) {
        addToFile(file, data);
        System.out.println("Добавлено");
      } else {
        System.out.println("Пропущено");
      }
    }
  }

  private static void addToFile(File file, String data) {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
      bw.write(data + "\n");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean isExist(File file, String data) {
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String fileString = br.readLine();
      while (fileString != null) {
        if (fileString.equals(data)) {
          return true;
        }
        fileString = br.readLine();
      }
      return false;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
