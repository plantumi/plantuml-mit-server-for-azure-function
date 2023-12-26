# plantuml-mit-server-for-azure-function
PlantUML server that works as an Azure Function with MIT License

# Quick start
Clone or download this source code. Then run the function locally or deploy to Azure functions according to the developers guide.
https://learn.microsoft.com/en-us/azure/azure-functions/functions-develop-vs-code?tabs=node-v3%2Cpython-v2%2Cin-process&pivots=programming-language-csharp#debugging-functions-locally

# Usage
After deploy the function, it can be accessed with this URL from a web browser.

https://<your_function_name>.azurewebsites.net/api/convert?url=/<format>/<encoded_plantuml_text>

Example:

https://<your_function_name>.azurewebsites.net/api/convert?url=/svg/SoWkIImgAStDuU9ooazIqBLJSCp9J4wrKl18pSd9L-JYSaZDIm5A0m00


\<format\> can be one of
- svg
- xmi_argo
- xmi
- xmi_star
- png
- img
