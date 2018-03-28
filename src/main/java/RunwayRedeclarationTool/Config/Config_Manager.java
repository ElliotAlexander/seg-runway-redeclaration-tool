package RunwayRedeclarationTool.Config;

import RunwayRedeclarationTool.Exceptions.ConfigurationFileNotFound;
import RunwayRedeclarationTool.Exceptions.MalformattedConfigFile;
import RunwayRedeclarationTool.Logger.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Config_Manager {


    private final String config_file_string;
    private final String userdata_path;

    public Config_Manager(){
        String user_home = System.getProperty("user.home");
        user_home = user_home.replaceAll("\\\\", "/");
        Logger.Log("User home : " + user_home);
        userdata_path = user_home + "/" + "Runway_Redeclaration_Tool";
        File dir = new File(userdata_path);
        if(!dir.exists()){
            dir.mkdir();
        }
        File f = new File(userdata_path + "/config.txt");
        config_file_string = f.getAbsolutePath();
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
                InputStream in = Config_Manager.class.getClassLoader().getResourceAsStream("config.txt");
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

    public Configuration load_config() throws ConfigurationFileNotFound {
        File f = new File(config_file_string);
        if(f.exists()){
            ArrayList<String> file_String = new ArrayList<>();
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

    private HashMap<String, String> parse_config_string(String[] line_arr) throws MalformattedConfigFile {

        HashMap<String, String> config_arr =  new HashMap<>();
        for(String l : line_arr){

            // strip whitespace and tabs
            l = l.replaceAll("\\s+","");
            // Escape backslashes - essential for file paths.
            l = l.replace("\\", "/");

            // This needs to be in a seperate block to the below.
            if(l.equals("")){
                continue;
            }

            // Ignore start of line commenting
            if(l.charAt(0) == '#'){
                continue;
            // Ignore end of line commenting
            } else if(l.split("#").length != 0) {
                l = l.split("#")[0];
            }

            if(!l.contains(":")) {
                throw new MalformattedConfigFile("Error parsing line: " + l);
            } else {
                String[] key_pair = l.split(":", 2);



                // We need to split on the first colon, as key : value, but then ignore all future colons.
                // This is important for file paths - C:/ etc.
                String key = key_pair[0];
                String value = key_pair[1];

                value = value.replace("%USERHOME%", userdata_path);

                // We'll store all keys in lowercase
                config_arr.put(key.toLowerCase() ,value);
                Logger.Log("Loaded config value: key : " + key + ", value : " + value);
            }

        }
        return config_arr;
    }
}
