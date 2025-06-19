package com.somas.scraper.ecommerce_scraper;

/**
 * Data model representing a book with title, price, and availability
 */
public class Book {
    private String title;
    private String price;
    private String availability;

    // Default constructor
    public Book() {}

    // Constructor with all fields
    public Book(String title, String price, String availability) {
        this.title = title;
        this.price = price;
        this.availability = availability;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getAvailability() {
        return availability;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", availability='" + availability + '\'' +
                '}';
    }
}