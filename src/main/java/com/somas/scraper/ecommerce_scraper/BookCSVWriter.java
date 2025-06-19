package com.somas.scraper.ecommerce_scraper;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Handles writing book data to CSV files using OpenCSV
 */
public class BookCSVWriter {
    private static final String[] CSV_HEADER = {"Title", "Price", "Availability", "Scraped_At"};
    
    /**
     * Writes books to a CSV file with timestamp
     * @param books List of books to write
     * @param filename Custom filename (optional)
     * @return The filename that was written to
     */
    public String writeBooksToCSV(List<Book> books, String filename) {
        if (filename == null || filename.isEmpty()) {
            filename = generateDefaultFilename();
        }
        
        try (FileWriter fileWriter = new FileWriter(filename);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {
            
            // Write header
            csvWriter.writeNext(CSV_HEADER);
            
            // Write book data
            String timestamp = getCurrentTimestamp();
            for (Book book : books) {
                String[] bookData = {
                    cleanText(book.getTitle()),
                    cleanText(book.getPrice()),
                    cleanText(book.getAvailability()),
                    timestamp
                };
                csvWriter.writeNext(bookData);
            }
            
            System.out.println("Successfully wrote " + books.size() + " books to: " + filename);
            return filename;
            
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
            throw new RuntimeException("Failed to write CSV file", e);
        }
    }
    
    /**
     * Writes books to CSV with default filename
     * @param books List of books to write
     * @return The filename that was written to
     */
    public String writeBooksToCSV(List<Book> books) {
        return writeBooksToCSV(books, null);
    }
    
    /**
     * Generates a default filename with timestamp
     * @return Generated filename
     */
    private String generateDefaultFilename() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);
        return "books_data_" + timestamp + ".csv";
    }
    
    /**
     * Gets current timestamp for data tracking
     * @return Formatted timestamp string
     */
    private String getCurrentTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
    
    /**
     * Cleans text data for CSV writing
     * @param text Text to clean
     * @return Cleaned text
     */
    private String cleanText(String text) {
        if (text == null) {
            return "";
        }
        // Remove extra whitespace and newlines
        return text.trim().replaceAll("\\s+", " ");
    }
    
    /**
     * Writes books to multiple CSV files based on availability status
     * @param books List of books to categorize and write
     * @return Array of filenames that were created
     */
    public String[] writeBooksToCSVByAvailability(List<Book> books) {
        // Separate books by availability
        List<Book> inStockBooks = books.stream()
            .filter(book -> book.getAvailability().toLowerCase().contains("in stock"))
            .toList();
            
        List<Book> outOfStockBooks = books.stream()
            .filter(book -> !book.getAvailability().toLowerCase().contains("in stock"))
            .toList();
        
        String[] filenames = new String[2];
        
        // Write in-stock books
        if (!inStockBooks.isEmpty()) {
            filenames[0] = writeBooksToCSV(inStockBooks, "books_in_stock_" + 
                getCurrentTimestamp().replaceAll("[: -]", "_") + ".csv");
        }
        
        // Write out-of-stock books
        if (!outOfStockBooks.isEmpty()) {
            filenames[1] = writeBooksToCSV(outOfStockBooks, "books_out_of_stock_" + 
                getCurrentTimestamp().replaceAll("[: -]", "_") + ".csv");
        }
        
        return filenames;
    }
    
    /**
     * Appends books to an existing CSV file
     * @param books List of books to append
     * @param filename Existing CSV filename
     */
    public void appendBooksToCSV(List<Book> books, String filename) {
        try (FileWriter fileWriter = new FileWriter(filename, true);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {
            
            String timestamp = getCurrentTimestamp();
            for (Book book : books) {
                String[] bookData = {
                    cleanText(book.getTitle()),
                    cleanText(book.getPrice()),
                    cleanText(book.getAvailability()),
                    timestamp
                };
                csvWriter.writeNext(bookData);
            }
            
            System.out.println("Successfully appended " + books.size() + " books to: " + filename);
            
        } catch (IOException e) {
            System.err.println("Error appending to CSV file: " + e.getMessage());
            throw new RuntimeException("Failed to append to CSV file", e);
        }
    }
}