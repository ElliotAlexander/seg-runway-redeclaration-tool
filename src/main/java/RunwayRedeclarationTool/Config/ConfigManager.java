package RunwayRedeclarationTool.Config;

import RunwayRedeclarationTool.Exceptions.ConfigurationFileNotFound;
import RunwayRedeclarationTool.Exceptions.MalformattedConfigFile;
import RunwayRedeclarationTool.Logger.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigManager {


    private final String config_file_string = "config.txt";

    public ConfigManager(){
        File f = new File(config_file_string);
        if(!f.exists()){
            try {
                Logger.Log("Couldn't find " + config_file_string);
                Logger.Log("Creating a new config file...");
                f.createNewFile();

                // Now let's copy our default config file into the new file
                // If anyone knows a better way to copy a file out of the jar then TODO?

                Logger.Log("Copying default config file out of classpath.");
                // First let's setup a bufferedwriter for our output file
                BufferedWriter writer = new BufferedWriter(new FileWriter(f));

                // now wrap in BR to read line by line
                InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream("config.txt");
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));

                // Then let's write line by line?
                String nl = reader.readLine();
                while(nl != null){
                    // this won't add newlines automatically
                    writer.append(nl + "\n");
                    nl = reader.readLine();
                }

                // Close our writer
                writer.close();

                // Close our reader :(
                reader.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Configuration load_config(File f) throws ConfigurationFileNotFound {
        if(f.exists()){
            ArrayList<String> file_String = new ArrayList<String>();
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(f);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    file_String.add(line);
                }
                fileReader.close();

                // Parse the config file :)
                return new Configuration(parse_config_string(file_String.toArray(new String[file_String.size()])));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MalformattedConfigFile malformattedConfigFile) {
                malformattedConfigFile.printStackTrace();
            }
        } else {
            throw new ConfigurationFileNotFound("Failed to load file " + f.getName());
        }
        return null;
    }


    // Note that we've already sorted
    private HashMap<String, String> parse_config_string(String[] line_arr) throws MalformattedConfigFile {

        HashMap<String, String> config_arr =  new HashMap<String, String>();
        int line_number = 0;
        String current_section = "";
        for(String l : line_arr){

            // Ignore start of line commenting
            if(l.charAt(0) == '#'){
                continue;
            // Ignore end of line commenting
            } else if(l.split("#").length != 0) {
                l = l.split("#")[0];
            }

            // Sections should still be terminated with a colon
            if(!l.contains(":")) {
                throw new MalformattedConfigFile("Error parsing line: " + l);
            } else {
                String[] key_pair = l.split(":");


                if(key_pair.length > 2){
                    Logger.Log(Logger.Level.WARNING, "Possible errors in config line (" + line_number + "). \nLine: " + l);
                }

                if (key_pair.length == 1){
                    Logger.Log("Loading configuration section: " + l);
                    current_section = key_pair[0];
                    continue;
                }
                Logger.Log("Loading keypair as full configuration value.");
                String key = key_pair[0];
                String value = key_pair[1];

                if(key.charAt(0) == '\t'){
                    // Strip the tab from our key
                    key.replaceAll("\\s+","");
                    key = current_section + key;
                }

                config_arr.put(key ,value);
                Logger.Log("Loaded config value: key : " + key + ", value : " + value);
            }

        }
        return config_arr;
    }
}
