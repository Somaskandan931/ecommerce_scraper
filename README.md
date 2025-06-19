# 📚 BookScraper – Static E-Commerce Scraper (Java + Selenium)

This project is a **static HTML web scraper** built using **Java** and **Selenium WebDriver** to extract book data from the demo site [Books to Scrape](https://books.toscrape.com/). It collects book **titles**, **prices**, and **availability** from multiple pages and exports the data into a CSV file.

---

## ✅ Project Type: Static Content Extraction

### 🎯 Target Site:
> https://books.toscrape.com/

- No JavaScript rendering
- Pure HTML structure
- Pagination supported

---

## 🛠️ Tech Stack

| Tool       | Use Case                            |
|------------|--------------------------------------|
| Java       | Main programming language            |
| Selenium   | For automated browser interaction    |
| Jsoup      | (Optional) HTML parsing utility      |
| OpenCSV    | For exporting data into CSV format   |
| Maven      | Project & dependency management      |

---

## 💡 Features

- 🔍 Scrape book **title**, **price**, and **availability**
- 📄 Navigate across all paginated book listings
- 💾 Save results to a CSV file
- 🧩 Clean modular design (Product class, Exporter class, etc.)
- ⚙️ Uses `ArrayList`, `HashMap` for structured data collection

---

---

## ▶️ How to Run

### 📌 Prerequisites

- Java 8 or above
- Maven installed
- Chrome browser + matching [ChromeDriver](https://chromedriver.chromium.org/downloads)

### 🧪 Steps to Run

```bash
# Clone the repo
git clone https://github.com/yourusername/book-scraper.git
cd book-scraper

# Install dependencies and build
mvn clean install

# Run the scraper
mvn exec:java -Dexec.mainClass="com.somas.scraper.BookScraper"

📦 Output
Scraped book data is exported as:

bash
Copy
Edit
output/books.csv
🧪 Sample Output Fields
Title	Price	Availability
A Light in the Attic	£51.77	In stock
Tipping the Velvet	£53.74	In stock
Soumission	£50.10	In stock

⚠️ Disclaimer
This scraper is for educational use only. The target site books.toscrape.com is provided by its creators for testing and learning web scraping, and scraping it is fully permitted.
