echo "---------------------------------------"
echo 
echo "Building Bundled Fat Jar Version"
echo
echo "---------------------------------------"

echo ">>> Building Media Viewer Angular App"
(cd ./mediaViewer; ng build --prod)
echo

echo ">>> Copying Media Viewer to Quarkus Resources Directory"
cp -r ./mediaViewer/dist/mediaViewer/* ./mediahoster/src/main/resources/META-INF/resources

echo ">>> Packaging Quarkus Media Hoster Fat Jar" 
mvn -f ./mediahoster clean package 
echo

echo ">>> Moving Bundled Fat Jar To Root Directory"
mv ./mediahoster/target/mediahoster*runner.jar .

echo ">>> Done! Created at "
pwd
ls -lth mediahoster*.jar
