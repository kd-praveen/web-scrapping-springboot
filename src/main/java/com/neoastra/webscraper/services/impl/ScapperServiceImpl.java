package com.neoastra.webscraper.services.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.neoastra.webscraper.dto.ResponseDTO;
import com.neoastra.webscraper.services.ScapperService;

@Service
public class ScapperServiceImpl implements ScapperService {

    //Reading data from property file to a list
    @Value("#{'${website.urls}'.split(',')}")
    List<String> urls;

    @Override
    public Set<ResponseDTO> getVehicleByModel(String vehicleModel) {
        //Using a set here to only store unique elements
        Set<ResponseDTO> responseDTOS = new HashSet<>();
        //Traversing through the urls
        for (String url: urls) {

            if (url.contains("ikman")) {
                //method to extract data from Ikman.lk
                extractDataFromIkman(responseDTOS, url + vehicleModel);
            } else if (url.contains("riyasewana")) {
               //method to extract Data from riyasewana.com
                extractDataFromRiyasewana(responseDTOS, url + vehicleModel);
            }

        }

        return responseDTOS;
    }

    private void extractDataFromRiyasewana(Set<ResponseDTO> responseDTOS, String url) {

        try {
            //loading the HTML to a Document Object
            Document document = Jsoup.connect(url).get();
            //Selecting the element which contains the ad list
            Element element = document.getElementById("content");
            //getting all the <a> tag elements inside the content div tag
            Elements elements = element.getElementsByTag("a");

           //traversing through the elements
            for (Element ads: elements) {
                ResponseDTO responseDTO = new ResponseDTO();

                if (!StringUtils.isEmpty(ads.attr("title")) ) {
                    //mapping data to the model class
                    responseDTO.setTitle(ads.attr("title"));
                    responseDTO.setUrl(ads.attr("href"));

                    // Extracting img src
                    Element imgElement = ads.select("img").first();
                    if (imgElement != null) {
                        responseDTO.setImageUrl(imgElement.attr("src"));
                    }
                }
                if (responseDTO.getUrl() != null) responseDTOS.add(responseDTO);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void extractDataFromIkman(Set<ResponseDTO> responseDTOS, String url) {
        try {
            //loading the HTML to a Document Object
            Document document = Jsoup.connect(url).get();
            //Selecting the element which contains the ad list
            Element element = document.getElementsByClass("list--3NxGO").first();
            //getting all the <a> tag elements inside the list--3NxGO class
            Elements elements = element.getElementsByTag("a");

            for (Element ads: elements) {

                ResponseDTO responseDTO = new ResponseDTO();

                if (StringUtils.isNotEmpty(ads.attr("href"))) {
                   //mapping data to our model class
                    responseDTO.setTitle(ads.attr("title"));
                    responseDTO.setUrl("https://ikman.lk"+ ads.attr("href"));

                    // Extracting img src
                    Element imgElement = ads.select("img").first();
                    if (imgElement != null) {
                        responseDTO.setImageUrl(imgElement.attr("src"));
                    }
                }
                if (responseDTO.getUrl() != null) responseDTOS.add(responseDTO);

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
