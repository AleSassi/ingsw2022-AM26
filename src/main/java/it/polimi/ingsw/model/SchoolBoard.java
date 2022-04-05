package it.polimi.ingsw.model;

import it.polimi.ingsw.model.student.EmptyCollectionException;
import it.polimi.ingsw.model.student.Student;
import it.polimi.ingsw.model.student.StudentHost;

import java.util.ArrayList;

public class SchoolBoard {
    private int AvaibleTowerCount;
    StudentHost diningRoom=new StudentHost();
    StudentHost Entrance=new StudentHost();
    private Tower towertype;
    ArrayList<Professor> prof=new ArrayList<Professor>();

    public SchoolBoard(Tower tower){
        towertype=tower;

    }

    public int GetCountAtTheTable(Student s){
    return diningRoom.getCount(s);
    }


    public int getAvaibleTowerTowers(){
        return AvaibleTowerCount;
    }

    public ArrayList<Professor> getControlledProfessor(){
        return prof;
    }

    public void addStudentToEntrance(Student s){
        Entrance.placeStudents(s, 1);

    }

    public void RemoveStudentFromEntrance(Student s) throws EmptyCollectionException{
        if(Entrance.getCount(s)==0){
            throw new EmptyCollectionException();
        
        }else{
        Entrance.removeStudents(s, 1);}

    }


    public void AddStudentToTable(Student s){
        diningRoom.placeStudents(s, 1);

        }

    public void RemoveStudentTable(Student s) throws EmptyCollectionException{
        try
        {diningRoom.removeStudents(s, 1);}
        catch(EmptyCollectionException e){
            throw e;

        }
        }

    public void gainTower(){
        AvaibleTowerCount+=1;
    }

    public Tower PickAndRemove() throws InsufficientTowersException {
        if(this.AvaibleTowerCount==0){
            throw new InsufficientTowersException();}
        else{
    this.AvaibleTowerCount=this.AvaibleTowerCount-1;
    return towertype;}


    }
}
