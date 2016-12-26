package parser.web;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class mainclass{
    private static class InputParameters{
        int countOfPictures;
        String[] tags;
        InputParameters(String[] arguments){
            countOfPictures=Integer.parseInt(arguments[0]);
            tags=new String[arguments.length-1];
            for (int i =1;i<arguments.length;i++){
                tags[i-1]=arguments[i];
            }
        }
    }
    static private void collectFilenames(InputParameters parameters,ArrayList<String> fileId){
        int page=1,countPicturesToDownload=parameters.countOfPictures;
        ArrayList<String> tmparraylist=null;
        while(countPicturesToDownload>0){
            tmparraylist=parsewallpape("https://alpha.wallhaven.cc/search?q="+String.join("+",parameters.tags)+"&categories=111&page="+page,countPicturesToDownload);
            page++;
            countPicturesToDownload-=tmparraylist.size();
            if(tmparraylist==null){
                break;
            }
            fileId.addAll(tmparraylist);
        }
       }
    static private void downloadAndSave(ArrayList<String> fileId,InputParameters parameters){
        for (int i=0;i<fileId.size();i++) {
            System.out.println("\n"+(i+1)+"/"+fileId.size()+" "+String.join("_",parameters.tags)+"_"+fileId.get(i));
            try {
                if(!download(new URL("https://wallpapers.wallhaven.cc/wallpapers/full/wallhaven-"+fileId.get(i)+".jpg"),new File("c:\\wallpapes\\"+String.join("_",parameters.tags)+"_"+fileId.get(i)+".jpg"))){
                    try {
                        Files.deleteIfExists(new File("c:\\wallpapes\\"+String.join("_",parameters.tags)+"_"+fileId.get(i)+".jpg").toPath());
                    } catch (IOException e3) {
                        System.out.println("file not deleted");
                        e3.printStackTrace();
                    }
                    try {
                        download(new URL("https://wallpapers.wallhaven.cc/wallpapers/full/wallhaven-"+fileId.get(i)+".png"),new File("c:\\wallpapes\\"+String.join("_",parameters.tags)+"_"+fileId.get(i)+".png"));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    System.out.println(i+1+"/"+fileId.size()+" "+String.join("_",parameters.tags)+"_"+fileId.get(i)+".png downloaded");
                    continue;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            System.out.println(i+1+"/"+fileId.size()+" "+String.join("_",parameters.tags)+"_"+fileId.get(i)+".jpg downloaded");
        }
    }
    static ArrayList<String> parsewallpape(String link, Integer count){
        ArrayList<String> listofindex=new ArrayList();
        URL ur= null;
        try {
            ur = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //System.out.print(ur.getFile());
        File file = new File("c:\\wallpapes\\temp.html");
        download(ur,file);
        String content = null;
        Scanner sc;
        try {//пишу в темповый файл а потом читаю из него наврно я нарк
            sc=new Scanner(new File("c:\\wallpapes\\temp.html"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        content =sc.useDelimiter("\\Z").next() ;
        Matcher m = Pattern.compile("(class=\"preview\"[^0-9]*\\/wallpaper\\/)([0-9]+)").matcher(content);
        if (count==0){
            return null;
        }
        while(m.find()&&count!=0)
        {
            count--;
            listofindex.add(m.group(2));
        }
        sc.close();
        try {
            Files.deleteIfExists(new File("c:\\wallpapes\\temp.html").toPath());
        } catch (IOException e3) {
            System.out.println("tmpfile not deleted");
            e3.printStackTrace();
        }
        return listofindex;
    }
    static public boolean download(URL link, File file){
        File folder=new File( file.getParent());
        if (!folder.exists()){
            folder.mkdirs();
        }
        URLConnection connection = null;
        try {
            connection = link.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }

        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        try {
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        int i;
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(
                    connection.getInputStream());
        } catch (IOException e) {
            //System.out.println("File not found");
            //e.printStackTrace();
            return false;
        }
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(
                    new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        try {
            while ((i = bis.read()) != -1) {
                bos.write(i);}
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //InputStream is = connection.getInputStream();
        //System.out.println("asd");
        return true;
    }
    public static void main(String[] args) {
        InputParameters parameters =new InputParameters(args);
        ArrayList<String> fileNames= new ArrayList<String>(parameters.countOfPictures);
        collectFilenames(parameters,fileNames);
        downloadAndSave(fileNames,parameters);
    }
}