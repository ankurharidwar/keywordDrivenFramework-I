echo "########## Building Code ##########";

echo "########## Creating bin dir ###########";
mkdir -p $1/bin;
sleep 1;

echo "########## Copy the testng file ##########"
cp $1/../../../launchConfiguration/testng_gui.xml $1/exe/;

echo "########## Compiling code and triggering build as configured in testNg.xml ##########";
sh $1/tpt/apache-ant-1.9.3_unix/bin/ant -v -buildfile $1/build-jenkins.xml -Dautomation_home=$1;