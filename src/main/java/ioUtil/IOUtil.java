package ioUtil;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class IOUtil {

    public static Map<String, Integer> getMapValue() {
        Map<String, Integer> numberValue = new HashMap<>();
        numberValue.put("0", 0);
        numberValue.put("1", 1);
        numberValue.put("2", 2);
        numberValue.put("3", 3);
        numberValue.put("4", 4);
        numberValue.put("5", 5);
        numberValue.put("6", 6);
        numberValue.put("7", 7);
        numberValue.put("8", 8);
        numberValue.put("9", 9);
        numberValue.put("A", 10);
        numberValue.put("B", 11);
        numberValue.put("C", 12);
        numberValue.put("D", 13);
        numberValue.put("E", 14);
        numberValue.put("F", 15);
        return numberValue;
    }

    public static void copy(String source, String dest) throws IOException {
        File file = new File(source);
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            throw new RuntimeException("File does not exist");
        }
        File destFile = new File(dest);
        if (!destFile.getParentFile().isDirectory() || !destFile.getParentFile().canWrite()) {
            throw new RuntimeException("Permission denied");
        }
        try (InputStream inputStream = new FileInputStream(file);
             OutputStream out = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[8192];
            int readCount;
            while ((readCount = inputStream.read(buffer, 0, buffer.length)) > -1) {
                out.write(buffer, 0, readCount);
            }
        }
    }

    public static void delete(String source) {
        File file = new File(source);
        if (file.exists() && file.canRead()) {
            file.delete();
        }
    }

    public static void move(String source, String dest) throws IOException {
        copy(source, dest);
        File file = new File(source);
        file.delete();
    }

    public static void split(String source, int count, String dest) throws IOException {
        File file = new File(source);
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            throw new RuntimeException("average");
        }
        long length = file.length();
        if (length > 2) {
            int averageFileSize = (int) (length / count);
            try (FileInputStream inputStream = new FileInputStream(source)) {
                byte[] buffer = new byte[2048];
                for (int i = 0; i < count; i++) {
                    int totalReadBytesCount = 0;
                    int readTo = (averageFileSize - totalReadBytesCount > buffer.length) ? buffer.length : (averageFileSize - totalReadBytesCount);
                    File file1 = new File(dest + "/split" + i + ".txt");
                    try (OutputStream outputStream = new FileOutputStream(file1)) {
                        int readCount;
                        while ((readTo > 0) && (readCount = inputStream.read(buffer, 0, readTo)) > -1) {
                            outputStream.write(buffer, 0, readCount);
                            totalReadBytesCount += readCount;
                            if (i == count - 1) {
                                readTo = (int) (((length - (averageFileSize * (count - 1))) - totalReadBytesCount > buffer.length) ? buffer.length : (length - (averageFileSize * (count - 1))) - totalReadBytesCount);
                            } else {
                                readTo = (averageFileSize - totalReadBytesCount > buffer.length) ? buffer.length : (averageFileSize - totalReadBytesCount);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void combine(String sourceFolder, String fileNamePrefix, String destination) throws IOException {
        Comparator<File> comparator = new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        File file = new File(destination);
        File folder = new File(sourceFolder);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            if (folder.exists() && folder.isDirectory()) {
                File[] listAllFiles = folder.listFiles();
                if (listAllFiles.length > 0) {
                    List<File> listFilesContainsNamePrefix = new ArrayList<>();
                    for (File getFileFromList : listAllFiles) {
                        if (getFileFromList.getName().contains(fileNamePrefix)) {
                            listFilesContainsNamePrefix.add(getFileFromList);
                        }
                    }
                    Collections.sort(listFilesContainsNamePrefix, comparator);
                    for (File containsNamePrefix : listFilesContainsNamePrefix) {
                        try (FileInputStream fileInputStream = new FileInputStream(containsNamePrefix)) {
                            byte[] buffer = new byte[8192];
                            int readCount;
                            while ((readCount = fileInputStream.read(buffer)) > -1) {
                                fileOutputStream.write(buffer, 0, readCount);
                            }
                        }
                    }
                }
            }

        }

    }

    public static void zip(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists() && file.canRead()) {
            File zipFile = new File("/home/seda/Desktop/test2.zip");
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
                addZip(zipOutputStream, file);
            }
        }
    }

    private static void addZip(ZipOutputStream zipOutputStream, File file) throws IOException {
        if (file.isFile()) {
            addFile(zipOutputStream, file);
            return;
        } else {
            File[] files = file.listFiles();
            for (File file2 : files) {
                addZip(zipOutputStream, file2);
            }

        }
    }

    private static void addFile(ZipOutputStream zipOutputStream, File file) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            String[] split = file.getParent().split("/home/seda/Desktop");
            ZipEntry zipEntry = new ZipEntry(split[split.length - 1] + "/" + file.getName());
            zipOutputStream.putNextEntry(zipEntry);
            byte[] bytes = new byte[8192];
            int length;
            while ((length = inputStream.read(bytes)) > -1) {
                zipOutputStream.write(bytes, 0, length);
            }
            zipOutputStream.closeEntry();
        }
    }

    public static void unZip(String zipFliePath) throws IOException {
        File file = new File(zipFliePath);
        if (file.exists()) {
            try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file))) {
                ZipEntry zipEntry = zipInputStream.getNextEntry();
                while (zipEntry != null) {
                    File destFile = new File(file.getParentFile().getAbsolutePath() + "/" + zipEntry.getName());
                    if (zipEntry.isDirectory()) {
                        destFile.mkdirs();
                    } else {
                        getFileFromZip(zipInputStream, destFile);
                    }
                    zipEntry = zipInputStream.getNextEntry();
                }
            }
        }

    }

    private static void getFileFromZip(ZipInputStream zipInputStream, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] bytes = new byte[8192];
        int length;
        while ((length = zipInputStream.read(bytes)) > -1) {
            fileOutputStream.write(bytes, 0, length);
        }
        fileOutputStream.close();
    }

    public static List<String> printRecursive(String filePath, List<String> list) {
        File file = new File(filePath);
        if (file.exists()) {
            list.add(file.getAbsolutePath());
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    printRecursive(file1.getAbsolutePath(), list);
                }
            }
        }
        return list;
    }

    public static String switchTenthNumberSystem(int number, int toNumber) {
        String returnedNumber = "";
        if (number < toNumber) {
            return String.valueOf(number);
        } else {
            while (number != 0) {
                returnedNumber += number % toNumber;
                number /= toNumber;
            }
        }

        return reverseString(returnedNumber);
    }

    public static int toNumberForTenthBase(String number, int base) {
        Map<String, Integer> mapValue = getMapValue();
        String reverseNumber = reverseString(number);
        char[] charsNumber = reverseNumber.toCharArray();
        int returnedNumber = 0;
        for (int i = charsNumber.length - 1; i >= 0; i--) {
            if (mapValue.get((String.valueOf(charsNumber[i]))) > base) {
                throw new RuntimeException("invalid value");
            }
            int val = 1;
            Integer integer = mapValue.get((String.valueOf(charsNumber[i])));
            if ((integer != 0)) {
                for (int j = i; j > 0; j--) {
                    val *= base;
                }
                returnedNumber += integer * val;
            }
        }
        return returnedNumber;
    }

    private static String reverseString(String string) {
        char[] chars = string.toCharArray();
        String newString = "";
        for (int i = chars.length - 1; i >= 0; i--) {
            newString += chars[i];
        }
        return newString;
    }

    public static void main(String[] args) throws IOException {
//        delete("/home/seda/Desktop/test1.txt");
//        copy("/home/seda/Desktop/test.txt", "/home/seda/Desktop/test1.txt");
//        split("/home/seda/Desktop/test2.txt", 4, "/home/seda/Desktop");
//        zip("/home/seda/Desktop/test.txt");
//        unZip("/home/seda/Desktop/test.zip");
        List<String> list = new ArrayList<>();
        List<String> fileNames = printRecursive("/home/seda/Desktop/", list);
        for (String fileName : fileNames) {
            System.out.println(fileName);
        }
//        split("/home/seda/Desktop/test.txt", 5, "/home/seda/Desktop/");
//        combine("/home/seda/Desktop", "/home/seda/Desktop/split0.txt", "/home/seda/Desktop/split1.txt");
//        factorial5(5);
//        combine("/home/seda/Desktop/test1", "split", "/home/seda/Desktop/test1/test.txt");
//        zip("/home/seda/Desktop/test1");
//        System.out.println(switchTenthNumberSystem(25, 16));
//        System.out.println(toNumberForTenthBase("10", 16));


    }


}
