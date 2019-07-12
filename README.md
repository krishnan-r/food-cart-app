# Food-Cart-App

This project is a prototype mobile application along with a backend web service to pre-order food from a food court, following various industry-standard software development patterns.

# Features 
- An application server built using Vert.x & Java
- Android mobile app client


![Screenshot](https://user-images.githubusercontent.com/6822941/59745364-44489a00-9292-11e9-99f0-62fffff16a23.png)


# Developer Documentation



## Running the containerized database using docker compose

Docker compose is used to run the mysql and phpmyadmin containers automatically. 

```bash
docker-compose up 
# docker-compose up -V # To reinitialize the datbase on subsequent runs.
```

#### While running on docker-toolbox (on windows)
If you are running docker through docker-toolbox which uses VirtualBox, you might have to use the ip address of the virtual machine to access the services in the containers. To use the database from localhost, configure ssh port forwarding as follows:


```bash
docker-machine ssh default -L 3306:localhost:3306 -L 8081:localhost:8081
```

This allows you to access the mysql database and phpmyadmin through localhost on the host machine.