## Description
This file describes the basics for Grafana Loki logging. Loki is the name of the server that collects log messages, and Grafana is the name of the user interface where you can view logs. Together, these components form an open-source log aggregation system that allows users to efficiently store and query log messages.

## Requirements
Docker is installed (On Windows you can install it from https://docs.docker.com/desktop/setup/install/windows-install/)

## Instructions
1. Start docker desktop
  
2. Open the terminal and navigate to the project root (SYSC4806A-Group4-Amazin-Online-Bookstore)
  - This directory should contain `docker-compose.yml`

3. In the terminal run `docker-compose up`
  - This starts the Loki log collection server at http://loki:3100
  - This also starts Grafana which is accessible at http://localhost:3000

4. Start the bookstore application

5. Visit the Grafana user interface at http://localhost:3000

6. Sign in with username = `admin` password = `admin`

7. Skip the page recommending you to change your password

8. In the side bar, navigate to Connection > Data sources and click "Add data source"
<img width="1886" height="834" alt="image" src="https://github.com/user-attachments/assets/f64328de-3874-4ed4-bf8c-cc147c06f47f" />

9. Search for Loki

10. In the Connections box, add the link to the Loki server: http://loki:3100
<img width="1151" height="788" alt="image" src="https://github.com/user-attachments/assets/d026d726-cdeb-41bc-8a03-062e8e61c4ef" />

11. Scroll down to the bottom of the page and click "Save & Test"
    
12. Go to the Explore tab and set the Label Filter to 'app' and 'bookstore'
<img width="1930" height="777" alt="image" src="https://github.com/user-attachments/assets/1c627b43-bee7-48b8-93fb-4e531ce05e0c" />

13. Search for keywords using the "Line Contains" box.
    
14. To terminate your connection, you can use Ctrl + Shift + C in the terminal and then run `docker-compose down`

## Grafana Loki Files
- docker-compose.yml : Sets up and runs the Loki and Grafana containers, making sure both services are started and connected so logs can be collected and viewed.
- logback-spring.xml : Controls how the Spring Boot application formats its logs and sends them to Loki, including which labels and details Grafana will be able to filter on.
