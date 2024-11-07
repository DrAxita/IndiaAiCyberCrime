# Running the Project in Console

To run this project in the console, follow these steps:

1. Make sure you have Maven installed on your system. You can check by running `mvn -v` in your terminal.

2. Navigate to the project's root directory (where the `pom.xml` file is located) in your terminal.

3. Run the following Maven command to compile and run the project:

   ```
   mvn compile exec:java
   ```

   This command will compile the project and then execute the main class specified in the `pom.xml` file, which is `com.example.App`.

4. You should see the output "Hello World!" in the console.

Note: If you want to package the project into a JAR file and then run it, you can use these commands instead:

1. Package the project:
   ```
   mvn package
   ```

2. Run the generated JAR file:
   ```
   java -jar target/india_ai_cyber_crime-1.0.jar
   ```
   java -cp target/india_ai_cyber_crime-1.0.jar com.example.App

These instructions assume that your Java and Maven environment variables are correctly set up.