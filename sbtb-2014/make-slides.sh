OUTPUT_FILE=index.html

pandoc -s -f markdown -t slidy -o $OUTPUT_FILE README.md
sed -i 's/<\/head>/  <style type="text\/css">p.caption { font-style: italic; }<\/style>\n<\/head>/g' $OUTPUT_FILE
sed -i 's/<\/head>/  <style type="text\/css">div.figure { text-align: center; }<\/style>\n<\/head>/g' $OUTPUT_FILE

wget -nc http://www.w3.org/Talks/Tools/Slidy2/styles/slidy.css
wget -nc http://www.w3.org/Talks/Tools/Slidy2/scripts/slidy.js

sed -i 's/http:\/\/www.w3.org\/Talks\/Tools\/Slidy2\/styles\///g' $OUTPUT_FILE
sed -i 's/http:\/\/www.w3.org\/Talks\/Tools\/Slidy2\/scripts\/slidy.js(.gz)?/slidy.js/g' $OUTPUT_FILE
