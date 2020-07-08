# Media Hoster

A program that automatically scans, hosts and streams media files to other devices. Currently, only `mp4`, `webm` and `ogg` formats are supported. If you are curious of what it does, have a look at the GIF Demo in the last section. I have personally tested it on different devices, it's working properly on Linux, Windows OS, Android and IOS.

_Disclaimer:You should only use it for non-sensetive data and in secure network, as it does not provide any data encryption. The data transferred between your devices are only protected by your own network, e.g., WIFI and so on._

This repository contains two applications:

- **Quarkus - Media Hoster** (in folder <a href="https://github.com/CurtisNewbie/MediaHoster/tree/master/mediahoster">"./mediahoster"</a>) that scans files in specified folder, and exposes them as resources via RESTful web services. Media resources can be <a href="https://medium.com/canal-tech/how-video-streaming-works-on-the-web-an-introduction-7919739f7e1">streamed in chunked data</a> (e.g., played directly on your video player or web browser) or downloaded. The <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Range_requests">Byte-Range Requests</a> is supported.
- **Angular - Media Viewer** (in folder <a href="https://github.com/CurtisNewbie/MediaHoster/tree/master/mediaViewer">"./mediaViewer"</a>) that shows you the playlist, plays them on the webpage and does all the HTTP calls for you.

Extra Support:

- An **Android TV App** is developed to work with this webapp. This app does the same thing as the Angular app, except that it works on an Android TV device. It's available in <a href="https://github.com/CurtisNewbie/AndrodTv_UrlPlayer">AndrodTv_UrlPlayer</a>

**Prerequisite**

- Java 11
- Nodejs (OPTIONAL, if you want to run frontend seperately)
- Maven (OPTIONAL, if you are running the program that is not packaged)
- <a href="https://www.graalvm.org/">GraalVM</a> (OPTIONAL, if you want to compile the native version)

## How To Run it?

This section shows you how to run and use the app. Custom configuration via `application.properties` file or `Command Line Arguments(CLI)` are both available. CLI Configuration is discussed in this section and it's always preferable, since you don't need to rebuild the app yourself. Read the next section for `application.properties` configuration if you are interested.

Download the latest release. Run it as follows:

    java -jar mediahoster-1.0.8-bundled.jar

The Media Viewer is simply a one-page web application which interacts with the Media Hoster for you, you access the webpage by entering the URL as follows:

    http://yourIp:8080/

    e.g.,

    http://192.168.1.1:8080/

If you are on Windows, you can find your ip using the command `ipconfig` in cmd. If you are on linux, you can find it using the command `hostname -I` in bash. You should make sure all devices are in the same network.

**By Default, the Media Hoster always creates a folder called `media/`, and it expects you to put all your media files in it**. This is the default behaviour configured in `application.properties`, read the next section if you are interested. I personally recommend using CLI configuration for specifying the media directory. **Note that CLI Configuration is always of higher priority over the property configuration.** It's as follows:

    // Use the '-DDir' flag for your preferred directory (relative/absolute path)

    -DDir=

    E.g.,

    java -jar mediahoster-1.0.8-runner.jar -DDir=/home/zhuangyongj/media/

Once the app is up and running, it will start scanning the directory. Only the supported media files are recognised and exposed as resources. Currently, only `mp4`, `webm` and `ogg` are supported.

## Property Configuration

This section discusses configuration in `application.properties` file.

### Preferred Media Directory

If you have a preferred directory for it to use, you can configure this as below in `./mediahoster/src/main/resources/application.properties`, it won't delete nor overwrite your files. If the provided path is illegal, it simply outputs an error and uses its default folder instead.

    path_to_media_directory=/my/preferred/media/directory

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

## Extra Information

### RESTAPI

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

Byte-range request is supported, e.g., for curl:

    curl localhost:8080/media?filename=tonystark.mp4 -H 'Range: bytes=0-1' -o tonystark.mp4

### Packaging Bundled Version Using Script

If you have modified the configuration or code, and wish to build the whole bundled version on your own. I have created a simple script that should work for you. It is at root directory, named "build.sh". If you are using Windows OS, you may need to make one for yourself. To run it, simply enter:

    ./build.sh

A script for native, bundled version is also availble below, but you should have GraavlVM installed. Note that this script is for Linux OS. To run it, simply enter:

    ./native_build.sh

## Demo

[Mar 18, 2020]

<img src="https://user-images.githubusercontent.com/45169791/76889978-6c474700-687e-11ea-8b06-de00ab16bcab.gif">
