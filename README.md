# Send email app
This app sends emails with a specified template. 
Email, contract number, date of the contract, the amount are set by the user
Supports multiple recipients, each recipient should be added by a new line in the input field (same for all input elements).
# Build executable jar-file
```bash
mvn clean compile assembly:single
```
# Run
Use start.bat file or via command line:
```bash
java -jar path/to/jar/name.jar 
```
