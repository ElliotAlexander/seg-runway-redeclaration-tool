#!/bin/bash

# The path to a built executable Jar file.
JARFILE=RRT.jar

# The name of the database file, set in the config.
DBNAME=airports.db

# The name of the config file, this shouldn't change.
CONFIGNAME=config.txt
DBFILE=~/Runway_Redeclaration_Tool/db/$DBNAME
CONFIGFILE=~/Runway_Redeclaration_Tool/$CONFIGNAME

# This might need to adjusted for systems where the application launches slower.
# The application should be fully open before closing again, not on the splashscreen.
TIMEOUT=5

INITIALRUNOUT=inital_run.txt
TEST1OUT=test1.txt
TEST2OUT=test2.txt

echo "Running application initially"
echo "Using timeout value [$TIMEOUT]"
timeout $TIMEOUT java -jar $JARFILE > $INITIALRUNOUT
echo "Finished, beginning tests" && echo && echo

echo "[[ Test 1 ]]  " && echo
echo "Beginning DB Rebuild Test"
echo "Removing database folder [DBFILE=$DBFILE]"
rm $DBFILE
echo "Running application." && echo "Sleeping for $TIMEOUT seconds"
timeout $TIMEOUT java -jar $JARFILE > $TEST1OUT
if [ -f $DBFILE ]; then
     echo "[PASS] Database rebuilt successfully." && echo 
else 
    echo "[FAIL] Database failed to rebuild. Using DBNAME=$DBFILE"
    echo "Outputting logs:" && echo
    cat $TEST1OUT
fi

echo "[[ Test 2 ]]" && echo
echo "Beginning Config rebuild test"
echo "Removing config [CONFIGFILE=$CONFIGFILE]"
rm $CONFIGFILE
echo "Running application." && echo "Sleeping for $TIMEOUT seconds"
timeout $TIMEOUT java -jar $JARFILE > $TEST2OUT
if [ -f $CONFIGFILE ]; then
    echo "[PASS] Config rebuilt successfully."
else 
    echo "[FAIL] Config failed to rebuild. Using ConfigFile=$CONFIGFILE"
    echo "Outputting logs:" && echo .
    cat $TEST2OUT
fi





