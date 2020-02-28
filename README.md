# Media Hoster

A program that scans (every 1 second), hosts and streams media files to other devices using REST web services/ HTTP protocols.

**Disclaimer:_You should only use it for non-sensetive data and in secure network, as it does not provide any encryption. The data transferred between your devices are only protected by your own network, e.g., WIFI and so on. I personally only use it for streaming movies in local network._**

This repository contains two main applications:

- **Media Hoster** (in folder <a href="https://github.com/CurtisNewbie/MediaHoster/tree/master/mediahoster">"./mediahoster"</a>) scans files in specified folder, and exposes them as resources via HTTP/REST web services. Media resources can be <a href="https://medium.com/canal-tech/how-video-streaming-works-on-the-web-an-introduction-7919739f7e1">streamed in chunked data</a> (e.g., played directly on your video player or web browser) or downloaded. The <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Range_requests">Partial Content (206) and Byte-Range Requests</a> are also supported.
- **Media Viewer** (in folder <a href="https://github.com/CurtisNewbie/MediaHoster/tree/master/mediaViewer">"./mediaViewer"</a>) is an Angular application, that facilitates the interaction with the Media Hoster, using this is optional. You can send HTTP requests directly to retrieve the whole media file. However, for your convenience, this angular app shows you the whole list of available media files, and plays them on a standard HTML Video tag.

**Prerequisite**

- Java 11
- Nodejs (if you want to run frontend seperately)
- Maven (if you are running the program that is not packaged)

## How To Run it?

### Running Bundled Version

This section tells you how to run the executable, the next section shows you how to actually use it.

If the program is packaged and the angular app is bundled inside the jar file, you can run it in the normal way. It will start listening to localhost:8080. **I strongly recommend you to use the bundled fat jar version, so that you always talk to the same hostname and same port.**

    java -jar mediahoster-1.0.2-SNAPSHOT-runner.jar

However, you must be aware that, by default, this program is packaged to be a fat jar that includes all dependencies (around 14mb including angular app). This command will not work if you intend to run a thin jar without `/lib` files. More on <a href="https://quarkus.io/guides/getting-started">Quarkus</a>.

### Package Bundled Version Using Script

If you have modified the configuration or code, and wish to build the whole bundled version on your own. I have created a simple script that should work for you. It is at root directory, named "build.sh". If you are using Windows OS, you may need to modify it a bit or make one for yourself. To run it, simply enter:

    ./build.sh

### Running Media Hoster and Media Viewer Individually

If the program is not packaged, you need to run it with maven. Navigate to `./mediahoster`, and execute command below. When you execute following command in your CLI, the Quarkus will start the development mode, and listens to localhost:8080.

    mvn clean quarkus:dev

If you want to package a fat jar for Media Hoster, simple executes following command with maven in `"./mediahoster"`:

    mvn clean package

If you want to run the Media Viewer (angular app) on a seperate server, you need to build it as below in `./mediaViewer` and host the `/dist` folder using your preferred server. More about this on <a href="https://angular.io/guide/deployment">Angular</a>.

    ng build --prod

## How To Use it?

This section tells you how to interact with the media hoster and media viewer. I assume that your are using the bundled fat jar version in release. **If not specified, the Media Hoster always creates a folder called `media/`, and it expects you to put all your media files in it**. Customisation is possible, but you have to build the fat Jar on your own. For more information about this, read the last section.

The Media Viewer is simply a one-page web application which interacts with the Media Hoster for you, you access the webpage by entering the URL as follows:

    http://yourIp:8080/

    e.g.,

    http://192.168.1.1:8080/

This ip address is your IPv4. If you are on Windows, you can find your ip using the command `ipconfig` in cmd. If you are on linux, you can find it using the command `hostname -I` in bash. You should make sure all devices are in the same network.

If you want to interact with the Media Hoster directly, there are two HTTP requests available. To get a list of all available media files:

    GET - return an array of names of media files

    http://yourIp:8080/media/all

To get a specific media file for streaming or downloading, your provide name of media file in query parameter as below:

    GET - return media file

    http://yourIp:8080/media?filename=mediaFileName

    e.g.,

    http://192.168.1.1:8080/media?filename=tonystark.mp4

## Optional Configuration

### Default Media Directory

By default, the Media Hoster application creates a folder named `media/` at where the jar or the application is located, and expects you to put all media files in it.

    For example,

    .../somefolder/
                  |
                  |__ mediaHoster.jar
                  |__ media/
                            |
                            |__ tonystark.mp4
                            |__ ...

### Preferred Media Directory

If you have a preferred directory for it to use, you can configure this as below in `./mediahoster/src/main/resources/application.properties`, it won't delete nor overwrite your files. If the provided path is illegal, it simply outputs an error and uses its default folder instead.

    path_to_media_directory=/my/preferred/media/directory
