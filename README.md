# LEAPS : One – Stop Solution For All Your Fashion Needs.

Outfit rental platform which will connect users who want to rent out outfits for any particular event with customers who
would use the outfit for the event. Platform is a mobile application. The Cloth Rental App is a Mobile Application-based
platform that allows users to rent designer clothes for special occasions, photoshoots, or everyday wear. This app makes
it easy for users to access high-quality designer clothes without the need to purchase them outright.

### Instructions

* This is a Maven Project. Ensure, Maven is installed on your system.
* It is Recommended that you use Linux Based OS.

### Getting Started :

* To run a Spring Boot project, you will need to have Java installed on your machine. Here are some instructions to
  download and install Java:

    1. Go to the Oracle website (https://www.oracle.com/java/technologies/downloads/) or OpenJDK
       website (https://jdk.java.net/) to download the Java Development Kit (JDK).
    2. Choose the appropriate JDK version for your operating system and click the download button.
    3. Read and accept the license agreement.
    4. Follow the instructions to install the JDK on your machine.

* Once you have installed Java, you can proceed with running the Spring Boot project. Here are some instructions for
  that:

    1. Clone or download the Spring Boot project from the repository.
    2. Open your terminal or command prompt and navigate to the project directory.
    3. Run the command mvn clean install to build the project.
    4. Once the build is complete, run the command mvn spring-boot:run to start the application.
    5. The application should now be running on your local machine. You can access it by navigating
       to http://localhost:8080 in your web browser.

Note: If you encounter any errors while running the project, make sure to check the project dependencies and ensure that
they are properly installed on your machine.

### How to run in local

* Change the Application Properties (E.g. username/password of DB) present in resources/application.properties according
  to your local mysql-server.
* Create a database called leapsb or rename database name in application.properties -> spring.datasource.url=jdbc:mysql:
  //localhost:3306/leapsdb?useSSL=false
* After starting application, go to http://localhost:8080/swagger-ui/

The Cloth Rental App is a Mobile Application-based platform that allows users to rent designer clothes for special
occasions, photoshoots, or everyday wear. This app makes it easy for users to access high-quality designer clothes
without the need to purchase them outright.

## Features :

### User registration -

    • Registration via email and phone number.
    • OTP based registration system.
    • Registration is for owner and borrower.

### User onboarding -

    • Key fields need to be defined
    • Fields that are not mandatory need to be defined

### User profile -

#### Owner -

    • Image
    • Merchandise/Outfits
    • Price range

#### Borrower -

    • Image
    • Interested outfits
    • Price range

### Category listing page -

    • Event category
    • Gender
    • Men
    • Women
    • Outfit category
    • Price category

### Product description page -

    • Product Image
    • Quantity
    • Size
    • Price per day

### Checkout -

    • View summary of the Products
    • Proceed to Payment

### Payment Integration -

    • RazorPay

### Product Return Remainder-

    • Will recieve a mail before 2 days remaining for the return of the product.

### Analytics and reporting -

### Technologies Used :

    • Java
    * Spring Boot
    * Spring Security
    * Amazon S3
    * Twilio

### Generic App Information:-

https://docs.google.com/document/d/1S1W6aAPLp6Ae6uArF1SG3nHbjJMpN3en6cIjrUC1XPM/edit?usp=sharing
