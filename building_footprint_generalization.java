//Author: Balazs Dienes
//Contact: dienes.balazs88@gmail.com

//This code was prepared as an assignment for the class: Visualization Techniques of Spatial Data at TU Berlin.
//This is a generalization method that automatically reduces the number of vertices in two-dimensional building outlines. See "balazs_dienes_generalization_report.pdf" for details.

package buildinggeneralizator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Class 1
//Building vertices (coordinates) have three properties: x coord., y coord., and being deletable
class Coordinate{

    private double x;
    private double y;
    private boolean deletable = false;

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }
    
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}

//Class 2
//Building vertex coordinates are stored in a list

class Building{

    List<Coordinate> coordinates = new ArrayList<>();
    
}

//Main
public class BuildingGeneralizator {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        
        
        List<Building> buildings = new ArrayList<>();        
        
	//Read input file
        File file = new File("...\\source.txt");
        
        Scanner scan = new Scanner(file);
        
        while (scan.hasNextLine()){
        Building building = new Building();
        
        String read;
        read = scan.nextLine();

	//Split input coordinates to "x" and "y"
        String[] splittedCoordinates = read.split(", ");
        
            for (int i = 0; i < splittedCoordinates.length - 1; i++) {
                
                String[] xyCoordinates = splittedCoordinates[i].split(" ");
                
		//Set "x" and "y" coordinates
                Coordinate c = new Coordinate();
                c.setX(Double.parseDouble(xyCoordinates[0]));
                
                c.setY(Double.parseDouble(xyCoordinates[1]));
                
		//Add coordinates to building
                building.coordinates.add(c);
            }
            
	    //Add building to building list
            buildings.add(building);
        
        }        
        scan.close();
        
        Scanner scan2 = new Scanner(System.in);
        
	//Set buffer distance based on map scale (e.g. 1:50,000)
        double puffer = -1;
        System.out.print("Please, define scale: 1:");
        float scale = scan2.nextFloat();
        
        if (scale > 0 && scale < 50000) {
            puffer = 0;
        } else if(scale >= 50000 && scale < 100000){
            puffer = 1;
        } else if(scale >= 100000){
            puffer = 2;
        } else {
            System.out.println("Wrong scale! System exit.");
            System.exit(0);
        }
        
        for (int i = 0; i < buildings.size(); i++) {
            
            for (int j = 0; j < buildings.get(i).coordinates.size()-1; j++) {
                
	      //Distance between adjacent vertices
              double distance;
              
              double x1 = buildings.get(i).coordinates.get(j).getX();
              double x2 = buildings.get(i).coordinates.get(j+1).getX();
              double dx = x2-x1;
              
              double y1 = buildings.get(i).coordinates.get(j).getY();
              double y2 = buildings.get(i).coordinates.get(j+1).getY();
              double dy = y2-y1;
                
              distance = Math.sqrt(dx*dx + dy*dy);
              
                if (distance <= puffer) {
                    
                    double distancePrevious;

                    double xP1 = buildings.get(i).coordinates.get(j).getX();
                    double xP2 = 0;
                    double yP2 = 0;

		    //Case 1:
                    if(j-1 == -1){
                      xP2 = buildings.get(i).coordinates.get(buildings.get(i).coordinates.size()-1).getX();
                      yP2 = buildings.get(i).coordinates.get(buildings.get(i).coordinates.size()-1).getY();
                    } else {
                      xP2 = buildings.get(i).coordinates.get(j-1).getX();
                      yP2 = buildings.get(i).coordinates.get(j-1).getY();
                    }
                    double dPx = xP2-xP1;

                    double yP1 = buildings.get(i).coordinates.get(j).getY();

                    double dPy = yP2-yP1;

                    distancePrevious = Math.sqrt(dPx*dPx + dPy*dPy);
                    //---End of case 1---

		    //Case 2:
                    double distanceNext;

                    double xN1 = buildings.get(i).coordinates.get(j+1).getX();
                    double xN2 = 0;
                    double yN2 = 0;
                    
                    if(j+2 > buildings.get(i).coordinates.size()-1){
                        
                       xN2 = buildings.get(i).coordinates.get(0).getX();
                       yN2 = buildings.get(i).coordinates.get(0).getY();
                    } else {
                       xN2 = buildings.get(i).coordinates.get(j+2).getX();
                       yN2 = buildings.get(i).coordinates.get(j+2).getY();
                    }
                    
                    double dNx = xN2-xN1;

                    double yN1 = buildings.get(i).coordinates.get(j+1).getY();
                    
                    double dNy = yN2-yN1;

                    distanceNext = Math.sqrt(dNx*dNx + dNy*dNy);
                    //---End of case 2---

		    //Comparison of distances
		    //I.e. deciding which points are going to be deleted
                    if (distanceNext > distancePrevious) {
                        buildings.get(i).coordinates.get(j).setDeletable(true);
                    } else {
                        buildings.get(i).coordinates.get(j+1).setDeletable(true);
                    }                                                            
                }    
            }            
        }
        
	//If less than four vertices would remain after generalization, only min and max "x" and "y" are kept
	//Coordinates of min and max "x" and "y"
        for (int i = 0; i < buildings.size(); i++) {
            int counter = 0;
            double minX = buildings.get(i).coordinates.get(0).getX();
            double minY = buildings.get(i).coordinates.get(0).getY();
            double maxX = buildings.get(i).coordinates.get(0).getX();
            double maxY = buildings.get(i).coordinates.get(0).getY();
                
            for (int j = 0; j < buildings.get(i).coordinates.size()-1; j++) {
                
		//Count non-deletable vertices
                if (buildings.get(i).coordinates.get(j).isDeletable() == false) {
                   counter++; 
                }
                
                if (maxX < buildings.get(i).coordinates.get(j+1).getX()) {
                    maxX = buildings.get(i).coordinates.get(j+1).getX();
                }
                if (maxY < buildings.get(i).coordinates.get(j+1).getY()) {
                    maxY = buildings.get(i).coordinates.get(j+1).getY();
                }
                
                if (minX > buildings.get(i).coordinates.get(j+1).getX()) {
                    minX = buildings.get(i).coordinates.get(j+1).getX();
                }
                if (minY > buildings.get(i).coordinates.get(j+1).getY()) {
                    minY = buildings.get(i).coordinates.get(j+1).getY();
                }
                
            }
            
	    //Testing whether less than 4 points would remain after generalization
            if (counter < 4) {
                buildings.get(i).coordinates.clear();
                 
                Coordinate c1 = new Coordinate();
                c1.setX(maxX);
                c1.setY(maxY);
                buildings.get(i).coordinates.add(c1);
                
                Coordinate c2 = new Coordinate();
                c2.setX(maxX);
                c2.setY(minY);
                buildings.get(i).coordinates.add(c2);
                
                Coordinate c3 = new Coordinate();
                c3.setX(minX);
                c3.setY(minY);
                buildings.get(i).coordinates.add(c3);
                
                Coordinate c4 = new Coordinate();
                c4.setX(minX);
                c4.setY(maxY);
                buildings.get(i).coordinates.add(c4);
            } else {
                for (int j = 0; j < buildings.get(i).coordinates.size()-1; j++) {
                    if (buildings.get(i).coordinates.get(j).isDeletable() == true) {
                        buildings.get(i).coordinates.remove(j);
                    }
                }
            }
            
        }
        
	//Write coordinates remaining after generalization
        PrintWriter writer = new PrintWriter("...\\result.txt");
        
        for (int i = 0; i < buildings.size(); i++) {
            for (int j = 0; j < buildings.get(i).coordinates.size(); j++) {
                writer.print(buildings.get(i).coordinates.get(j).getX() + " " + buildings.get(i).coordinates.get(j).getY() + ",");
            }
            writer.println();
        }        
        writer.close();        
    }    
}
