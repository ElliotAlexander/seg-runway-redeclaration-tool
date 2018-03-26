package RunwayRedeclarationTool.Models;

public class Airport {
    public String airport_name, airport_id;

    public Airport(String airport_name, String airport_id){
        this.airport_name = airport_name;
        this.airport_id = airport_id;
    }

    public String getAirport_name() {
        return airport_name;
    }

    public String getAirport_id() {
        return airport_id;
    }

    public String toString(){
        return airport_name + " (" + airport_id + ")";
    }
}
