
import java.util.ArrayList;
import java.util.List;

class Main
{
    public static void main(String[] args) 
    {
        ReadCSV read_csv = new ReadCSV("pois.csv");
        List<Poi> poi_list = read_csv.returnPOIs();
        testCSV(poi_list);


    }

    static void testCSV (List<Poi> list)
    {
        int i = 0;
        for (Poi poi : list) // Right now I only print the first price $ because im too lazy to make a condition
            // to check if a poi has a range of $$ but i can assure you it (probably) works
        {
            poi = list.get(i);
            System.out.printf("Location %d: %s\n" +
                            "Types: %s and %s\n" +
                            "Price: %s\n" +
                            "Hours: %d to %d\n" +
                            "Address: %s\n" +
                            "Coordinate: %d\n" +
                            "Image Index: %d\n\n", (i+1),
                    poi.name, poi.types[0], poi.types[1],
                    poi.prices[0], poi.hours[0], poi.hours[1],
                    poi.address, poi.coordinates, poi.image_index);
            i++;

        }

    }
}

