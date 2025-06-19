package com.somas.scraper.ecommerce_scraper;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class for the Books to Scrape web scraper
 */
public class BookScrapperMain {
    
    public static void main(String[] args) {
        System.out.println("=== Books to Scrape Web Scraper ===");
        System.out.println("Starting scraping process...\n");
        
        BookExtractor extractor = null;
        Scanner scanner = new Scanner(System.in);
        
        try {
            // Initialize the extractor
            extractor = new BookExtractor();
            
            // Show menu options
            showMenu();
            
            int choice = getUserChoice(scanner);
            
            switch (choice) {
                case 1:
                    scrapeAllPages(extractor);
                    break;
                case 2:
                    scrapeSpecificPage(extractor, scanner);
                    break;
                case 3:
                    scrapeFirstNPages(extractor, scanner);
                    break;
                default:
                    System.out.println("Invalid choice. Scraping all pages by default.");
                    scrapeAllPages(extractor);
            }
            
        } catch (Exception e) {
            System.err.println("An error occurred during scraping: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up resources
            if (extractor != null) {
                extractor.close();
            }
            scanner.close();
            System.out.println("\nScraping process completed!");
        }
    }
    
    /**
     * Displays the menu options to the user
     */
    private static void showMenu() {
        System.out.println("Choose scraping option:");
        System.out.println("1. Scrape all pages");
        System.out.println("2. Scrape specific page");
        System.out.println("3. Scrape first N pages");
        System.out.print("Enter your choice (1-3): ");
    }
    
    /**
     * Gets user choice with input validation
     */
    private static int getUserChoice(Scanner scanner) {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            return choice;
        } catch (Exception e) {
            scanner.nextLine(); // Clear invalid input
            return 1; // Default to scraping all pages
        }
    }
    
    /**
     * Scrapes all pages and exports to CSV
     */
    private static void scrapeAllPages(BookExtractor extractor) {
        System.out.println("Scraping all pages...");
        
        long startTime = System.currentTimeMillis();
        List<Book> books = extractor.extractAllBooks();
        long endTime = System.currentTimeMillis();
        
        if (books.isEmpty()) {
            System.out.println("No books were extracted!");
            return;
        }
        
        // Print statistics
        printScrapingStatistics(books, startTime, endTime);
        
        // Export to CSV
        exportToCSV(books);
    }
    
    /**
     * Scrapes a specific page number
     */
    private static void scrapeSpecificPage(BookExtractor extractor, Scanner scanner) {
        System.out.print("Enter page number to scrape: ");
        int pageNumber;
        
        try {
            pageNumber = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (Exception e) {
            System.out.println("Invalid page number. Using page 1.");
            pageNumber = 1;
            scanner.nextLine(); // Clear invalid input
        }
        
        System.out.println("Scraping page " + pageNumber + "...");
        
        long startTime = System.currentTimeMillis();
        List<Book> books = extractor.extractBooksFromPage(pageNumber);
        long endTime = System.currentTimeMillis();
        
        if (books.isEmpty()) {
            System.out.println("No books found on page " + pageNumber);
            return;
        }
        
        // Print statistics
        printScrapingStatistics(books, startTime, endTime);
        
        // Export to CSV
        String filename = "books_page_" + pageNumber + ".csv";
        BookCSVWriter writer = new BookCSVWriter();
        writer.writeBooksToCSV(books, filename);
    }
    
    /**
     * Scrapes first N pages
     */
    private static void scrapeFirstNPages(BookExtractor extractor, Scanner scanner) {
        System.out.print("Enter number of pages to scrape: ");
        int numPages;
        
        try {
            numPages = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (Exception e) {
            System.out.println("Invalid number. Using 5 pages.");
            numPages = 5;
            scanner.nextLine(); // Clear invalid input
        }
        
        System.out.println("Scraping first " + numPages + " pages...");
        
        long startTime = System.currentTimeMillis();
        List<Book> allBooks = new ArrayList<>();
        
        for (int i = 1; i <= numPages; i++) {
            List<Book> pageBooks = extractor.extractBooksFromPage(i);
            if (pageBooks.isEmpty()) {
                System.out.println("No more books found at page " + i + ". Stopping.");
                break;
            }
            allBooks.addAll(pageBooks);
        }
        
        long endTime = System.currentTimeMillis();
        
        if (allBooks.isEmpty()) {
            System.out.println("No books were extracted!");
            return;
        }
        
        // Print statistics
        printScrapingStatistics(allBooks, startTime, endTime);
        
        // Export to CSV
        String filename = "books_first_" + numPages + "_pages.csv";
        BookCSVWriter writer = new BookCSVWriter();
        writer.writeBooksToCSV(allBooks, filename);
    }
    
    /**
     * Exports books to CSV with different options
     */
    private static void exportToCSV(List<Book> books) {
        BookCSVWriter writer = new BookCSVWriter();
        
        try {
            // Write all books to one CSV
            String mainFile = writer.writeBooksToCSV(books);
            
            // Also create separate files for in-stock vs out-of-stock
            String[] categoryFiles = writer.writeBooksToCSVByAvailability(books);
            
            System.out.println("\nCSV files created:");
            System.out.println("- Main file: " + mainFile);
            if (categoryFiles[0] != null) {
                System.out.println("- In stock books: " + categoryFiles[0]);
            }
            if (categoryFiles[1] != null) {
                System.out.println("- Out of stock books: " + categoryFiles[1]);
            }
            
        } catch (Exception e) {
            System.err.println("Error creating CSV files: " + e.getMessage());
        }
    }
    
    /**
     * Prints scraping statistics
     */
    private static void printScrapingStatistics(List<Book> books, long startTime, long endTime) {
        double durationSeconds = (endTime - startTime) / 1000.0;
        
        // Count availability status
        long inStockCount = books.stream()
            .filter(book -> book.getAvailability().toLowerCase().contains("in stock"))
            .count();
        long outOfStockCount = books.size() - inStockCount;
        
        // Price analysis
        double averagePrice = books.stream()
            .mapToDouble(book -> {
                try {
                    String priceStr = book.getPrice().replaceAll("[£$]", "");
                    return Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            })
            .average()
            .orElse(0.0);
        
        System.out.println("\n=== Scraping Results ===");
        System.out.println("Total books scraped: " + books.size());
        System.out.println("Time taken: " + String.format("%.2f", durationSeconds) + " seconds");
        System.out.println("Books per second: " + String.format("%.2f", books.size() / durationSeconds));
        System.out.println("In stock: " + inStockCount);
        System.out.println("Out of stock: " + outOfStockCount);
        System.out.println("Average price: £" + String.format("%.2f", averagePrice));
        
        // Show some sample books
        System.out.println("\n=== Sample Books ===");
        books.stream().limit(3).forEach(System.out::println);
        
        if (books.size() > 3) {
            System.out.println("... and " + (books.size() - 3) + " more books");
        }
    }
}