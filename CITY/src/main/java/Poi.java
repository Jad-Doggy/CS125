public class Poi
{
    String name = null;
    String[] types = null;
    String[] prices = null;
    int[] hours = null;
    String address = null;
    int coordinates = 0;
    int image_index = 0;

    public Poi(String Name, String[] Types, String[] Prices, int[] Hours, String Address, int Coordinates, int Image_index) // change coordinates to something else if needed
    {
        this.name = Name;
        this.types = Types;
        this.prices = Prices;
        this.hours = Hours;
        this.address = Address;
        this.coordinates = Coordinates;
        this.image_index = Image_index;
    }
}