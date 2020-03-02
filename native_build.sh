echo "---------------------------------------"
echo 
echo "Building Native Bundled Version"
echo
echo "---------------------------------------"

echo ">>> Building Media Viewer Angular App"
(cd ./mediaViewer; ng build --prod)
echo

echo ">>> Copying Media Viewer to Quarkus Resources Directory"
cp -r ./mediaViewer/dist/mediaViewer/* ./mediahoster/src/main/resources/META-INF/resources

echo ">>> Packaging Native Quarkus Media Hoster" 
mvn -f ./mediahoster clean package -Pnative 
echo

echo ">>> Removing compiled angular files in working dir"
rm -rvf ./mediahoster/src/main/resources/META-INF/resources/*
echo

echo ">>> Moving Bundled Fat Jar To Root Directory"
mv ./mediahoster/target/mediahoster-*-runner .

echo ">>> Done! Created at "
pwd
ls -lth mediahoster-*-runner
