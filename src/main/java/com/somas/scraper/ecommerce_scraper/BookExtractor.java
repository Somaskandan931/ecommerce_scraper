package com.somas.scraper.ecommerce_scraper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Extracts book data from books.toscrape.com using Selenium WebDriver
 */
public class BookExtractor {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "https://books.toscrape.com/";
    
    public BookExtractor() {
        setupDriver();
    }
    
    /**
     * Sets up the Chrome WebDriver with options
     */
    private void setupDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    /**
     * Extracts books from all pages of the website
     * @return List of Book objects
     */
    public List<Book> extractAllBooks() {
        List<Book> allBooks = new ArrayList<>();
        int currentPage = 1;
        
        System.out.println("Starting book extraction from: " + BASE_URL);
        
        while (true) {
            String pageUrl = currentPage == 1 ? BASE_URL : BASE_URL + "catalogue/page-" + currentPage + ".html";
            
            try {
                driver.get(pageUrl);
                
                // Wait for books to load
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("article.product_pod")));
                
                List<Book> pageBooks = extractBooksFromCurrentPage();
                
                if (pageBooks.isEmpty()) {
                    System.out.println("No more books found. Stopping at page " + (currentPage - 1));
                    break;
                }
                
                allBooks.addAll(pageBooks);
                System.out.println("Extracted " + pageBooks.size() + " books from page " + currentPage);
                
                // Check if there's a next page
                if (!hasNextPage()) {
                    System.out.println("Reached last page: " + currentPage);
                    break;
                }
                
                currentPage++;
                
                // Add a small delay to be respectful to the server
                Thread.sleep(1000);
                
            } catch (Exception e) {
                System.err.println("Error processing page " + currentPage + ": " + e.getMessage());
                break;
            }
        }
        
        System.out.println("Total books extracted: " + allBooks.size());
        return allBooks;
    }
    
    /**
     * Extracts books from the current page
     * @return List of books on current page
     */
    private List<Book> extractBooksFromCurrentPage() {
        List<Book> books = new ArrayList<>();
        
        try {
            List<WebElement> bookElements = driver.findElements(By.cssSelector("article.product_pod"));
            
            for (WebElement bookElement : bookElements) {
                try {
                    // Extract title
                    WebElement titleElement = bookElement.findElement(By.cssSelector("h3 a"));
                    String title = titleElement.getAttribute("title");
                    
                    // Extract price
                    WebElement priceElement = bookElement.findElement(By.cssSelector("p.price_color"));
                    String price = priceElement.getText();
                    
                    // Extract availability
                    WebElement availabilityElement = bookElement.findElement(By.cssSelector("p.instock.availability"));
                    String availability = availabilityElement.getText().trim();
                    
                    Book book = new Book(title, price, availability);
                    books.add(book);
                    
                } catch (Exception e) {
                    System.err.println("Error extracting book data: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error finding book elements: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Checks if there's a next page available
     * @return true if next page exists, false otherwise
     */
    private boolean hasNextPage() {
        try {
            List<WebElement> nextButtons = driver.findElements(By.cssSelector("li.next a"));
            return !nextButtons.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Closes the WebDriver
     */
    public void close() {
        if (driver != null) {
            driver.quit();
            System.out.println("WebDriver closed successfully");
        }
    }
    
    /**
     * Extracts books from a specific page number
     * @param pageNumber the page number to scrape
     * @return List of books from the specified page
     */
    public List<Book> extractBooksFromPage(int pageNumber) {
        String pageUrl = pageNumber == 1 ? BASE_URL : BASE_URL + "catalogue/page-" + pageNumber + ".html";
        
        try {
            driver.get(pageUrl);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("article.product_pod")));
            
            return extractBooksFromCurrentPage();
            
        } catch (Exception e) {
            System.err.println("Error extracting books from page " + pageNumber + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}