import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;



public class ReadCSV
{
    String file_name = null;
    List<Poi> pois = new ArrayList<>();

    public ReadCSV(String File_Name)
    {
        file_name = File_Name;
        Read();
    }   
    
    void Read()
    {
        if (file_name != null)
        {
            InputStream is = getClass().getClassLoader().getResourceAsStream(file_name);
            if (is == null) throw new RuntimeException("CSV not found");
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is))) 
            {
                String line;
                while ((line = bufferedReader.readLine()) != null) 
                {
                    String[] values = line.split(",");

                    // parses the values in line list
                    String name = values[0];
                    String[] types = {values[1], values[2]};
                    String[] prices = values[3].split("-");
                    String[] hour_parts = values[4].split("-");
                    int[] hours = {Integer.parseInt(hour_parts[0]), Integer.parseInt(hour_parts[1])};
                    String address = values[5];
                    int coordinates = Integer.parseInt(values[6]);
                    int image_index = Integer.parseInt(values[7]);
                    
                    // create a new POI and add it to the poi list
                    Poi poi = new Poi(name, types, prices, hours, address, coordinates, image_index);
                    pois.add(poi);
                }
            } catch (IOException e) 
            {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }
    }

    List<Poi> returnPOIs()
    {
        return pois;
    }
}
