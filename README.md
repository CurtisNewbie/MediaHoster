# Media Hoster

A program that automatically scans, hosts and streams media files to other devices using REST web services/ HTTP protocols. If you are curious of what it does, have a look at the GIF Demo in the last section. I have personally tested it on different devices, it's working properly on Linux, Windows OS, Android and IOS.

**_Disclaimer:You should only use it for non-sensetive data and in secure network, as it does not provide any data encryption. The data transferred between your devices are only protected by your own network, e.g., WIFI and so on._**

This repository contains two main applications:

- **Quarkus - Media Hoster** (in folder <a href="https://github.com/CurtisNewbie/MediaHoster/tree/master/mediahoster">"./mediahoster"</a>) that scans files in specified folder, and exposes them as resources via RESTful web services. Media resources can be <a href="https://medium.com/canal-tech/how-video-streaming-works-on-the-web-an-introduction-7919739f7e1">streamed in chunked data</a> (e.g., played directly on your video player or web browser) or downloaded. The <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Range_requests">Byte-Range Requests</a> is supported.
- **Angular - Media Viewer** (in folder <a href="https://github.com/CurtisNewbie/MediaHoster/tree/master/mediaViewer">"./mediaViewer"</a>) that facilitates the interaction with the Media Hoster. You can send HTTP requests directly to retrieve the whole media file. However, for your convenience, this angular app shows you the playlist, plays them on the webpage and does all the HTTP calls for you.

An **Android TV app** is developed to work with this webapp. This app does the same thing as the Angular app, except that it works on an Android TV device. It's available in <a href="https://github.com/CurtisNewbie/AndrodTv_UrlPlayer">AndrodTv_UrlPlayer</a> 

**Prerequisite**

- Java 11
- Nodejs (OPTIONAL, if you want to run frontend seperately)
- Maven (OPTIONAL, if you are running the program that is not packaged)
- <a href="https://www.graalvm.org/">GraalVM</a> (OPTIONAL, if you want to compile the native version)

## How To Run it?

This section tells you how to run the executable, the next section shows you how to actually use it. **I strongly recommend you to use the bundled fat jar version that is available in <a href="https://github.com/CurtisNewbie/MediaHoster/releases">release</a>, so that you always talk to the same hostname and port, and you only need to run one application.**

### Running Bundled Version

If the program is packaged and the angular app is bundled inside the jar file (e.g., the ones in release), you can run it in the normal way. It will start listening to localhost:8080.

    java -jar mediahoster-1.0.7-bundled.jar

However, you must be aware that, by default, this program is packaged to be a fat jar that includes all dependencies (around 14mb including angular app). This command will not work if you intend to run a thin jar without `/lib` files. More on <a href="https://quarkus.io/guides/getting-started">Quarkus</a>.

### Running Media Hoster and Media Viewer Individually

If the program is not packaged, you need to run it with maven. Navigate to `./mediahoster`, and execute command below. When you execute following command in your CLI, the Quarkus will start the development mode, and listens to localhost:8080.

    mvn clean quarkus:dev

If you want to package a fat jar for Media Hoster, simple executes following command with maven in `./mediahoster`:

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

If you want to interact with the Media Hoster directly, there are three HTTP requests available. To get a list of all available media files:

    GET - return an array of names of media files (JSON)

    http://yourIp:8080/media/all

To get a specific media file for streaming or downloading, your provide name of media file in query parameter as below:

    GET - return media file (video/mp4)

    http://yourIp:8080/media?filename=mediaFileName

    e.g.,

    http://192.168.1.1:8080/media?filename=tonystark.mp4

To get the number of media files available:

    GET - return the number of media files available (text/plain)

    http://yourIp:8080/media/amount

## Packaging Bundled Version Using Script

If you have modified the configuration or code, and wish to build the whole bundled version on your own. I have created a simple script that should work for you. It is at root directory, named "build.sh". If you are using Windows OS, you may need to modify it a bit or make one for yourself. To run it, simply enter:

    ./build.sh

A script for native, bundled version is also availble below, but you should have GraavlVM installed. Note that this script is for Linux OS. To run it, simply enter:

    ./native_build.sh

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

## Issues

In previous version **Quarkus 1.2.1.Final**, under certain situations, the `java.lang.IllegalMonitorStateException` is thrown. This is a bug in Quarkus and has been fixed already in **Quarkus 1.3.0.Final**. However, in order to migrate to this latest version, the unit tests must be disabled. For unknown reasons, this version does not recoginise/load the test methods, though this may be caused by the new class loading architecture. The unit tests will be enabled as soon as I find a workaround.

## Demo

[Mar 18, 2020]

<img src="https://user-images.githubusercontent.com/45169791/76889978-6c474700-687e-11ea-8b06-de00ab16bcab.gif">
