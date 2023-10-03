package com.neoastra.webscraper.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.playwright.*;
import com.neoastra.webscraper.dto.AmazonResponseDTO;
import com.neoastra.webscraper.dto.ResponseDTO;
import com.neoastra.webscraper.services.ScapperService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/api/v1/web-scapper")
@AllArgsConstructor
@NoArgsConstructor
public class ScapperController {

    @Autowired
    ScapperService scapperService;

    @GetMapping("/{vehicleModel}")
    public Set<ResponseDTO> getVehicleByModel(@PathVariable String vehicleModel) {
        return scapperService.getVehicleByModel(vehicleModel);
    }

    @GetMapping("/movie/{searchString}")
    public Set<AmazonResponseDTO> getMovieDetails(@PathVariable String searchString) {

        String dynamicScrapingUrl = "https://amazon.com/";
        String searchbarCSSSelectorQuery = "input#twotabsearchtextbox";
        String searchButtonSelectorQuery = "input#nav-search-submit-button";

        Document searchedHTMLResult = this.searchOnPage(dynamicScrapingUrl, searchbarCSSSelectorQuery,
                searchButtonSelectorQuery, searchString, 60000);

        String prodcuctDataCSSSelector = "div.a-section.a-spacing-small.puis-padding-left-small.puis-padding-right-small";

        List<Element> productDetails = this.getElementsByCSSQuery(searchedHTMLResult, prodcuctDataCSSSelector);

        String productTextSelector = "span.a-size-base-plus.a-color-base.a-text-normal";
        String productPriceSelector = "span.a-price-whole";
        String productCurrencySelector = "span.a-price-symbol";

        Set<AmazonResponseDTO> responseDTOS = new HashSet<>();

        for (Element productDetail : productDetails) {
            String productName = productDetail.select(productTextSelector).text();
            String productPrice = productDetail.select(productPriceSelector).text();
            String currency = productDetail.select(productCurrencySelector).text();

            System.out.println("productName = " + productName);
            System.out.println("currency = " + currency);
            System.out.println("productPrice = " + productPrice);

            AmazonResponseDTO responseDTO = new AmazonResponseDTO();
            responseDTO.setProductName(productName);
            responseDTO.setProductPrice(productPrice);
            responseDTO.setCurrency(currency);
            responseDTOS.add(responseDTO);
        }
        return responseDTOS;
    }

    public List<Element> getElementsByCSSQuery(Document doc, String cssQuery) {
        return doc.select(cssQuery);
    }

    public Document searchOnPage(String url, String searchbarCSSSelectorQuery, String searchButtonSelectorQuery,
            String searchText, int pageLoadTimeout) {
        try (
                Playwright playwright = Playwright.create()) {
            final BrowserType chromium = playwright.chromium();
            final Browser browser = chromium.launch();
            final Page page = browser.newPage();
            page.navigate(url);
            page.fill(searchbarCSSSelectorQuery, searchText);
            page.click(searchButtonSelectorQuery);
            page.waitForTimeout(pageLoadTimeout);
            Document doc = Jsoup.parse(page.content());
            browser.close();
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
