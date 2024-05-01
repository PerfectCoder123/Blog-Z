package com.example.autoslider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ImageParser {

    static List<String> response;

    public  List<String> fetchUrl(String tag) {
        response = new ArrayList<>();
        ParserThread freePik = new ParserThread("https://www.freepik.com/search?format=search&page=1&query=", "&type=photo", tag, "data-src", Arrays.asList(), Arrays.asList());
        ParserThread unSplash = new ParserThread("https://unsplash.com/s/photos/", "", tag, "src", Arrays.asList("https://images.unsplash.com/photo"), Arrays.asList("plus"));
        ParserThread iStock = new ParserThread("https://www.istockphoto.com/search/2/image?phrase=", "", tag, "src", Arrays.asList("https://media.istockphoto.com/id"), Arrays.asList());
        ParserThread freeImage = new ParserThread("https://www.freeimages.com/search/", "", tag, "data-src", Arrays.asList(), Arrays.asList());

        Thread freePikThread = new Thread(freePik);
        Thread freeImageThread = new Thread(unSplash);
        Thread istockThread = new Thread(iStock);
        Thread unSplashThread = new Thread(freeImage);

        freeImageThread.start();
        freePikThread.start();
        istockThread.start();
        unSplashThread.start();

        try {
            freeImageThread.join();
            freePikThread.join();
            istockThread.join();
            unSplashThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int imageCounter = 0;
        while (true){
            if(imageCounter >= unSplash.response.size() && imageCounter >= freeImage.response.size() && imageCounter >= freePik.response.size() && imageCounter >= iStock.response.size()) break;
            if(imageCounter<unSplash.response.size())response.add(unSplash.response.get(imageCounter));
            if(imageCounter<freePik.response.size())response.add(freePik.response.get(imageCounter));
            if(imageCounter<freeImage.response.size())response.add(freeImage.response.get(imageCounter));
            if(imageCounter<iStock.response.size()) response.add(iStock.response.get(imageCounter));
            imageCounter++;
        }

        return response;
    }
}

class ParserThread implements Runnable{
    List<String> response;
    String tag;
    String url;
    String attribute;
    String queryParameter;
    List<String> validSubstringUrl;
    List<String> inValidSubstringUrl;

    ParserThread(String url,String queryParameter,String tag,String attribute,List<String> validSubstringUrl, List<String> inValidSubstringUrl){
        response = new ArrayList<>();
        this.attribute = attribute;
        this.url = url;
        this.tag = tag;
        this.queryParameter = queryParameter;
        this.validSubstringUrl = validSubstringUrl;
        this.inValidSubstringUrl = inValidSubstringUrl;
    }

    @Override
    public void run() {
        try {
            Document doc = Jsoup.connect(url + tag + queryParameter)
                    .ignoreHttpErrors(true)
                    .timeout(30000).get();
            Elements element = doc.select("img[src]");
            for (Element link : element) {
                String imageUrl = link.attr(attribute);
                if(urlChecker(imageUrl)) {
                    response.add(imageUrl);
                }
                if(response.size() > 20) return;
            }
        } catch (Exception e) {

        }
    }

    private boolean urlChecker(String imageUrl){

        for(String urlSubstring : validSubstringUrl){
            if(!imageUrl.contains(urlSubstring)) return false;
        }
        for(String notUrlSubstring : inValidSubstringUrl){
            if(imageUrl.contains(notUrlSubstring)) return false;
        }
        if(imageUrl.equals("")) return false;
        return true;
    }
}
