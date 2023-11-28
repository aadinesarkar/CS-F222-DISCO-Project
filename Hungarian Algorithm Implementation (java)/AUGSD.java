import java.io.*;
import java.util.*;

public class AUGSD
{
    private Prof[] profs;
    private Course[] FD_CDCs;
    private Course[] HD_CDCs;
    private Course[] FD_Elecs;
    private Course[] HD_Elecs;
    private int[][] FD_CDCmatching;
    private int[][] HD_CDCmatching;
    private int[][] FD_ElecMatching;
    private int[][] HD_ElecMatching;
    private int[][] elecMatching;//do not use for printing

    /**
     * Instantiates a college academic division/department with...
     * @param profs an array of instructors
     * @param courses an array of courses
     */
    public AUGSD(Prof[] profs, Course[] FD_CDCs, Course[] HD_CDCs, Course[] FD_Elecs, Course[] HD_Elecs)
    {
        this.profs=profs;
        this.FD_CDCs=FD_CDCs;
        this.HD_CDCs=HD_CDCs;
        this.FD_Elecs=FD_Elecs;
        this.HD_Elecs=HD_Elecs;
        
        this.FD_CDCmatching=new int[profs.length][FD_CDCs.length];
        this.HD_CDCmatching=new int[profs.length][HD_CDCs.length];
        this.FD_ElecMatching=new int[profs.length][FD_Elecs.length];
        this.HD_ElecMatching=new int[profs.length][HD_Elecs.length];
    }

    public Course[] getFD_CDCs()
    {
        return FD_CDCs;
    }

    public Course[] getHD_CDCs() 
    {
        return HD_CDCs;
    }

    public Course[] getFD_Elecs() 
    {
        return FD_Elecs;
    }

    public Course[] getHD_Elecs() 
    {
        return HD_Elecs;
    }

    public int[][] getMatching(CourseTypes type) 
    {
        switch(type)
        {
            case FD_CDC:
                return FD_CDCmatching;
            case HD_CDC:
                return HD_CDCmatching;
            case FD_Elec:
                return FD_ElecMatching;
            case HD_Elec:
                return HD_ElecMatching;
            default:
                return new int[][]{{},{}};
        }
    }

    /**
     * @return the list of instructors in the college/department
     */
    public Prof[] getProfs()
    {
        return profs;
    }

    /**
     * @return value of the objective function. Call only after calling match() exactly once.
     */
    public int getMetric()
    {
        int n=0;
        for(Prof prof: profs)
        {
            if(prof.getPrefList(CourseTypes.FD_CDC).length>n)
                n=prof.getPrefList(CourseTypes.FD_CDC).length;
            if(prof.getPrefList(CourseTypes.HD_CDC).length>n)
                n=prof.getPrefList(CourseTypes.HD_CDC).length;
            if(prof.getPrefList(CourseTypes.FD_Elec).length>n)
                n=prof.getPrefList(CourseTypes.FD_Elec).length;
            if(prof.getPrefList(CourseTypes.HD_Elec).length>n)
                n=prof.getPrefList(CourseTypes.HD_Elec).length;
        }
        //n is now its desired value

        int metric=0;
        for(Prof prof: profs)
        {
            for(Course course: prof.getCourses())
            {
                if(course.courseIndex(prof.getPrefList(course.getType()))>=0)
                {
                    metric+=n-course.courseIndex(prof.getPrefList(course.getType()));
                }
                else
                {
                    metric-=1;
                }
            }
        }
        return metric;
    }

    /**
     * Initiates a sequence of actions that populate the matching matrix 
     */
    public void match(boolean rev)
    {
        FD_CDCmatching = new int[profs.length][FD_CDCs.length];
        HD_CDCmatching = new int[profs.length][HD_CDCs.length];
        FD_ElecMatching = new int[profs.length][FD_Elecs.length];
        HD_ElecMatching = new int[profs.length][HD_Elecs.length];
        this.step1();
        this.step2(rev);
        this.step3();
        this.step4(rev);
        this.step5();
        this.step6();
        this.step7(rev);
    }

    /**
     * matches the set of all instructors with the set of all FD CDCs
     * adds 1 to the matching matrix where an assignment is made
     */
    public void step1()
    {
        int[] assignment=(new HungarianAlgorithm(Course.weightMatrix(profs, FD_CDCs, CourseTypes.FD_CDC))).execute();
        for(int i=0; i<profs.length; i++)
        {
            for(int j=0; j<FD_CDCs.length; j++)
            {
                if(assignment[i]==j)
                {
                    FD_CDCmatching[i][j]++;
                    profs[i].assignCourse(FD_CDCs[j]);
                    FD_CDCs[j].assignToProf(profs[i]);
                }
            }
        }
    }

    /**
     * matches the set of free instructors with the set of all FD CDCs such that course sharing is maximised if specified
     * adds 1 to the matching matrix where an assignment is made 
     */
    public void step2(boolean rev)
    {
        Prof[] freeProfs=AUGSD.filterFreeProfs(profs);
        Prof[] revFreeProfs = new Prof[freeProfs.length];

        //reversing if selected
        for(int i=0; i<revFreeProfs.length; i++)
        {
            revFreeProfs[i]=rev?freeProfs[freeProfs.length-i-1]:freeProfs[i];
        }

        int[] assignment=(new HungarianAlgorithm(Course.weightMatrix(revFreeProfs, FD_CDCs, CourseTypes.FD_CDC))).execute();
        for(int i=0; i<revFreeProfs.length; i++)
        {
            for(int j=0; j<FD_CDCs.length; j++)
            {
                if(assignment[i]==j)
                {
                    FD_CDCmatching[revFreeProfs[i].getProfIndex(profs)][j]++;
                    profs[revFreeProfs[i].getProfIndex(profs)].assignCourse(FD_CDCs[j]);
                    FD_CDCs[j].assignToProf(profs[revFreeProfs[i].getProfIndex(profs)]);
                }
            }
        }
    }

    /**
     * matches the set of free instructors with the set of all HD CDCs
     * adds 1 to the matching matrix where an assignment is made 
     */
    public void step3()
    {
        Prof[] freeProfs=AUGSD.filterFreeProfs(profs);
        int[] assignment=(new HungarianAlgorithm(Course.weightMatrix(freeProfs, HD_CDCs, CourseTypes.HD_CDC))).execute();
        for(int i=0; i<freeProfs.length; i++)
        {
            for(int j=0; j<HD_CDCs.length; j++)
            {
                if(assignment[i]==j)
                {
                    HD_CDCmatching[freeProfs[i].getProfIndex(profs)][j]++;
                    profs[freeProfs[i].getProfIndex(profs)].assignCourse(HD_CDCs[j]);
                    HD_CDCs[j].assignToProf(profs[freeProfs[i].getProfIndex(profs)]);
                }
            }
        }
    }

    /**
     * matches the set of free instructors with the set of all HD CDCs such that course sharing is maximised if specified
     * adds 1 to the matching matrix where an assignment is made 
     */
    public void step4(boolean rev)
    {
        Prof[] freeProfs=AUGSD.filterFreeProfs(profs);
        Prof[] revFreeProfs = new Prof[freeProfs.length];
        //reversing
        for(int i=0; i<revFreeProfs.length; i++)
        {
            revFreeProfs[i]=rev?freeProfs[freeProfs.length-i-1]:freeProfs[i];
        }

        int[] assignment=(new HungarianAlgorithm(Course.weightMatrix(revFreeProfs, HD_CDCs, CourseTypes.HD_CDC))).execute();
        for(int i=0; i<revFreeProfs.length; i++)
        {
            for(int j=0; j<HD_CDCs.length; j++)
            {
                if(assignment[i]==j)
                {
                    HD_CDCmatching[revFreeProfs[i].getProfIndex(profs)][j]++;
                    profs[revFreeProfs[i].getProfIndex(profs)].assignCourse(HD_CDCs[j]);
                    HD_CDCs[j].assignToProf(profs[revFreeProfs[i].getProfIndex(profs)]);
                }
            }
        }
    }

    /**
     * To take care of 3-instructor FD CDCs like CS F111
     */
    public void step5()
    {
        for(int count=0; count<30; count++)
        {
            Prof[] freeProfs = AUGSD.filterFreeProfs(profs);
            Course[] vacantfdcdcs = AUGSD.filterVacantCourses(FD_CDCs);
            int[] assignment=(new HungarianAlgorithm(Course.weightMatrix(freeProfs, vacantfdcdcs, CourseTypes.FD_CDC))).execute();
            for(int i=0; i<freeProfs.length; i++)
            {
                for(int j=0; j<vacantfdcdcs.length; j++)
                {
                    if(assignment[i]==j)
                    {
                        FD_CDCmatching[freeProfs[i].getProfIndex(profs)][vacantfdcdcs[j].courseIndex(FD_CDCs)]++;
                        profs[freeProfs[i].getProfIndex(profs)].assignCourse(vacantfdcdcs[j]);
                        FD_CDCs[vacantfdcdcs[j].courseIndex(FD_CDCs)].assignToProf(profs[freeProfs[i].getProfIndex(profs)]);
                    }
                }
            }
        }
    }

    /**
     * Assign HD Elecs once
     */
    public void step6()
    {
        elecMatching=new int[profs.length][FD_Elecs.length+HD_Elecs.length];
        for(int i=0; i<profs.length; i++)//combined matching matrix for fd, hd elecs
        {
            for(int j=0; j<FD_Elecs.length; j++)
            {
                elecMatching[i][j]=FD_ElecMatching[i][j];
            }
            for(int j=FD_Elecs.length; j<FD_Elecs.length+HD_Elecs.length; j++)
            {
                elecMatching[i][j]=HD_ElecMatching[i][j-FD_Elecs.length];
            }
        }


        Prof[] freeProfs=AUGSD.filterFreeProfs(profs);
        int[] assignment=(new HungarianAlgorithm(Course.weightMatrix(freeProfs, FD_Elecs, HD_Elecs))).execute();
        for(int i=0; i<freeProfs.length; i++)
        {
            for(int j=0; j<FD_Elecs.length+HD_Elecs.length; j++)
            {
                if(assignment[i]==j)
                {
                    if(j<FD_Elecs.length)
                    {
                        elecMatching[freeProfs[i].getProfIndex(profs)][j]++;
                        profs[freeProfs[i].getProfIndex(profs)].assignCourse(FD_Elecs[j]);
                        FD_Elecs[j].assignToProf(profs[freeProfs[i].getProfIndex(profs)]);
                    }
                    else
                    {
                        elecMatching[freeProfs[i].getProfIndex(profs)][j]++;
                        profs[freeProfs[i].getProfIndex(profs)].assignCourse(HD_Elecs[j-FD_Elecs.length]);
                        HD_Elecs[j-FD_Elecs.length].assignToProf(profs[freeProfs[i].getProfIndex(profs)]);
                    }
                }
            }
        }
    }

    /**
     * Assign HD Elecs second time, complete assignment of partially assigned courses, and de-assign the courses that remain only partially assigned.
     */
    public void step7(boolean rev)
    {
        Prof[] freeProfs = AUGSD.filterFreeProfs(profs);
        Prof[] revFreeProfs = new Prof[freeProfs.length];
        //reversing
        for(int i=0; i<revFreeProfs.length; i++)
        {
            revFreeProfs[i]=rev?freeProfs[freeProfs.length-i-1]:freeProfs[i];
        }

        int[] assignment=(new HungarianAlgorithm(Course.weightMatrix(freeProfs, FD_Elecs, HD_Elecs))).execute();
        for(int i=0; i<freeProfs.length; i++)
        {
            for(int j=0; j<FD_Elecs.length+HD_Elecs.length; j++)
            {
                if(assignment[i]==j)
                {
                    if(j<FD_Elecs.length)
                    {
                        elecMatching[freeProfs[i].getProfIndex(profs)][j]++;
                        profs[freeProfs[i].getProfIndex(profs)].assignCourse(FD_Elecs[j]);
                        FD_Elecs[j].assignToProf(profs[freeProfs[i].getProfIndex(profs)]);
                    }
                    else
                    {
                        elecMatching[freeProfs[i].getProfIndex(profs)][j]++;
                        profs[freeProfs[i].getProfIndex(profs)].assignCourse(HD_Elecs[j-FD_Elecs.length]);
                        HD_Elecs[j-FD_Elecs.length].assignToProf(profs[freeProfs[i].getProfIndex(profs)]);
                    }
                }
            }
        }

        //removing half-assigned courses
        for(int j=0; j<FD_Elecs.length+HD_Elecs.length; j++)//for every FD elective x
        {
            if(j<FD_Elecs.length)
            {
                if(FD_Elecs[j].profCount()>0 && FD_Elecs[j].isVacant())//if x has only one instructor
                {
                    for(int i=0; i<profs.length; i++)//look at the list of instructors
                    {
                        if(elecMatching[i][j]==1)//find the one taking x
                        {
                            FD_Elecs[j].removeProf(profs[i]);//remove the edge joining x with the instructor
                            profs[i].removeCourse(FD_Elecs[j]);
                            elecMatching[i][j]--;
                        }
                    }
                }
            }
            else
            {
                if(HD_Elecs[j-FD_Elecs.length].profCount()>0 && HD_Elecs[j-FD_Elecs.length].isVacant())//if x has only one instructor
                {
                    for(int i=0; i<profs.length; i++)//look at the list of instructors
                    {
                        if(elecMatching[i][j]==1)//find the one taking x
                        {
                            HD_Elecs[j-FD_Elecs.length].removeProf(profs[i]);//remove the edge joining x with the instructor
                            profs[i].removeCourse(HD_Elecs[j-FD_Elecs.length]);
                            elecMatching[i][j]--;
                        }
                    }
                }
            }
        }

        //reassigning individual elec matchings
        for(int i=0; i<profs.length; i++)//combined matching matrix for fd, hd elecs
        {
            for(int j=0; j<FD_Elecs.length; j++)
            {
                FD_ElecMatching[i][j]=elecMatching[i][j];
            }
            for(int j=FD_Elecs.length; j<FD_Elecs.length+HD_Elecs.length; j++)
            {
                HD_ElecMatching[i][j-FD_Elecs.length]=elecMatching[i][j];
            }
        }
    }


    /**
     * @param courses an array of courses
     * @return an array containing courses that are not yet fully assigned
     */
    public static Course[] filterVacantCourses(Course[] courses)
    {
        int count=0;
        for(Course course: courses)
        {
            if(course.isVacant())
                count++;
        }
        Course[] vacantCourses=new Course[count];
        for(int i=0, k=0; i<courses.length; i++)
        {
            if(courses[i].isVacant())
                vacantCourses[k++]=courses[i];
        }
        return vacantCourses;
    }
    
    /**
     * @param profs an array of instructors
     * @return an array containing instructors who have at least one free slot
     */
    public static Prof[] filterFreeProfs(Prof[] profs)
    {
        int count=0;
        for(Prof prof: profs)
        {
            if(prof.isFree())
                count++;
        }
        Prof[] freeProfs=new Prof[count];
        for(int i=0, k=0; i<profs.length; i++)
        {
            if(profs[i].isFree())
                freeProfs[k++]=profs[i];
        }
        return freeProfs;
    }
    
    /**
     * @param courses an array of courses
     * @return an array containing courses that are assigned to exactly one instructor
     */
    public static Course[] filterPartiallyAssignedCourses(Course[] courses)
    {
        int count=0;
        for(Course course: courses)
        {
            if(course.profCount()>0&&course.isVacant())
                count++;
        }
        Course[] partiallyAssignedCourses=new Course[count];
        for(int i=0, k=0; i<courses.length; i++)
        {
            if(courses[i].profCount()>0&&courses[i].isVacant())
                partiallyAssignedCourses[k++]=courses[i];
        }
        return partiallyAssignedCourses;
    }

    /**
     * Reads a CSV file with lines in the format "Name,load,FD_CDC,HD_CDC,FD_Elec,HD_Elec"
     * NO whitespaces before or after commas
     * NO tolerance for errors
     * Header row IS expected. Any instructor preferences placed in the first line will be ignored.
     * @param filename name of CSV file to read from
     * @param fdcdc array of FD CDCs to match with
     * @param hdcdc array of HD CDCs to match with
     * @param fdelec array of HD electives to match with
     * @param hdelec array of HD electives to match with
     * @return array of instructors with populated preference lists
     * Uncomment print loop to print list of instructors to console
     */
    public static Prof[] readInput(String filename, Course[] fdcdc, Course[] hdcdc, Course[] fdelec, Course[] hdelec)
    {
        ArrayList<Prof> profList=new ArrayList<Prof>();
        String line = "";  
        String splitBy = ",";
        Prof prevProf=null;
        try   
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));  
            br.readLine();//header column
            while ((line = br.readLine()) != null)   //returns a Boolean value  
            {  
                String[] fields = line.split(splitBy, 6);    // use comma as separator  
                //System.out.println("NAME: Dr " + fields[0] + ", LOAD: " + fields[1] + " FD CDC: " + fields[2] + ", HD CDC" + fields[3] + ", FD Elec: " + fields[4] + ", HD Elec: " + fields[5]);
                Prof thisLineProf=new Prof(fields[0], Integer.parseInt(fields[1]), new Course[]{}, new Course[]{}, new Course[]{}, new Course[]{});
                if(!thisLineProf.equals(prevProf))
                {
                    profList.add(prevProf);
                    prevProf=thisLineProf;
                }
                else
                {
                if(Course.isInCourseList(fields[2], fdcdc))
                    prevProf.addPreference(Course.findInCourseList(fields[2], fdcdc));
                if(Course.isInCourseList(fields[3], hdcdc))
                    prevProf.addPreference(Course.findInCourseList(fields[3], hdcdc));
                if(Course.isInCourseList(fields[4], fdelec))
                    prevProf.addPreference(Course.findInCourseList(fields[4], fdelec));
                if(Course.isInCourseList(fields[5], hdelec))
                    prevProf.addPreference(Course.findInCourseList(fields[5], hdelec));
                }
            }  
            br.close();
        }
        catch (IOException e)   
        {  
            e.printStackTrace();  
        }
        profList.remove(0);//remove null
        profList.add(prevProf);

        // //TO CHECK IF PROFS HAVE BEEN INITIALISED CORRECTLY
        // for(Prof prof: profList)
        // {
        //     System.out.println(prof+" (load "+prof.getLoad()+")\n"+prof.getPreferenceListsString()+"\n\n");
        // }

        Prof[] profs = new Prof[profList.size()];
        for(int i=0; i<profList.size(); i++)
        {
            profs[i]=profList.get(i);
        }

        return profs;
    }
    
    /**
     * Makes a csv-compatible string indicating a four-row matrix, three with courses assigned to the instructor indicated by the column, and the fourth with the value of the objective function for the given assignment.
     * Call only after calling match() exactly once.
     * @param arr array indicating locations of profs in prof
     * @return csv output string
     */
    public String makeOutput(int[] arr)
    {
        Prof[] unpermutedProfs=new Prof[profs.length];

        for(int i=0; i<unpermutedProfs.length; i++)
        {
            unpermutedProfs[arr[i]]=profs[i];
        }

        for(Course cdc: FD_CDCs)//if any CDC
        {
            if(cdc.isVacant())//remains unassigned
            {
                return "";//the assignment is invalid so do not print it
            }
        }

        for(Course cdc: HD_CDCs)//if any CDC
        {
            if(cdc.isVacant())//remains unassigned
            {
                return "";//the assignment is invalid so do not print it
            }
        }

        String output="";

        //line 1
        for(int i=0; i<unpermutedProfs.length; i++)
        {
            if(unpermutedProfs[i].getCourseCount()==0)//if any instructor has 0 courses, do not print anything
            {
                return "";
            }
            if(i<unpermutedProfs.length-1)//all but last prof
            {
                if(unpermutedProfs[i].getCourseCount()>=1)
                {
                    output=output+unpermutedProfs[i].getCourses()[0]+" ("+unpermutedProfs[i].getCourses()[0].getType()+"),";
                }
                else
                {
                    output=output+",";
                }
            }
            else//last prof (no comma)
            {
                if(unpermutedProfs[i].getCourseCount()>=1)
                {
                    output=output+unpermutedProfs[i].getCourses()[0]+" ("+unpermutedProfs[i].getCourses()[0].getType()+")";
                }
            }
        }
        output=output+"\n";

        //line 2
        for(int i=0; i<unpermutedProfs.length; i++)
        {

            if(i<unpermutedProfs.length-1)//all but last prof
            {
                if(unpermutedProfs[i].getCourseCount()>=2)
                {
                    output=output+unpermutedProfs[i].getCourses()[1]+" ("+unpermutedProfs[i].getCourses()[1].getType()+"),";
                }
                else
                {
                    output=output+",";
                }
            }
            else//last prof (no comma)
            {
                if(unpermutedProfs[i].getCourseCount()>=2)
                {
                    output=output+unpermutedProfs[i].getCourses()[1]+" ("+unpermutedProfs[i].getCourses()[1].getType()+")";
                }
            }
        }
        output=output+"\n";

        //line 3
        for(int i=0; i<unpermutedProfs.length; i++)
        {
            if(i<unpermutedProfs.length-1)//all but last prof
            {
                if(unpermutedProfs[i].getCourseCount()>=3)
                {
                    output=output+unpermutedProfs[i].getCourses()[2]+" ("+unpermutedProfs[i].getCourses()[2].getType()+"),";
                }
                else
                {
                    output=output+",";
                }
            }
            else//last prof (no comma)
            {
                if(unpermutedProfs[i].getCourseCount()>=3)
                {
                    output=output+unpermutedProfs[i].getCourses()[2]+" ("+unpermutedProfs[i].getCourses()[2].getType()+")";
                }
            }
        }
        output=output+"\n";

        //writing metric
        output=output+"Metric:,";
        output=output+getMetric();
        for(int i=0; i<profs.length-2; i++)//profs.length-2 commas
        {
            output=output+",";//blank cells
        }
        output=output+"\n";

        return output;
    }

    /**
     * Prints a matrix containing the number of edges between two vertices in a hypothetical bipartite matching between the instructors (rows) and the courses (columns)
     */
    public void printMatching()
    {
        for(int i=0; i<FD_CDCs.length; i++)//print column headers
        {
            System.out.print(FD_CDCs[i].getCode()+" ");
        }
        System.out.print("      ");
        for(int i=0; i<HD_CDCs.length; i++)//print column headers
        {
            System.out.print(HD_CDCs[i].getCode()+" ");
        }
        System.out.print("      ");
        for(int i=0; i<FD_Elecs.length; i++)//print column headers
        {
            System.out.print(FD_Elecs[i].getCode()+" ");
        }
        System.out.print("      ");
        for(int i=0; i<HD_Elecs.length; i++)//print column headers
        {
            System.out.print(HD_Elecs[i].getCode()+" ");
        }
        System.out.print("      ");

        System.out.println();
        for(int i=0; i<profs.length; i++)
        {
            for(int j=0; j<FD_CDCs.length; j++)
            {
                System.out.print(FD_CDCmatching[i][j]+"       ");
            }
            System.out.print("      ");
            for(int j=0; j<HD_CDCs.length; j++)
            {
                System.out.print(HD_CDCmatching[i][j]+"       ");
            }
            System.out.print("      ");
            for(int j=0; j<FD_Elecs.length; j++)
            {
                System.out.print(FD_ElecMatching[i][j]+"       ");
            }
            System.out.print("      ");
            for(int j=0; j<HD_Elecs.length; j++)
            {
                System.out.print(HD_ElecMatching[i][j]+"       ");
            }
            System.out.println(" "+profs[i].getName()+" (LOAD: current "+profs[i].getCourseCount()+", max "+profs[i].getLoad()+")");
        }

        System.out.println("");
        for(int i=0; i<FD_CDCs.length; i++)
        {
            System.out.print(FD_CDCs[i].profCount()+"       ");
        }
        System.out.print("      ");
        for(int i=0; i<HD_CDCs.length; i++)
        {
            System.out.print(HD_CDCs[i].profCount()+"       ");
        }
        System.out.print("      ");
        for(int i=0; i<FD_Elecs.length; i++)
        {
            System.out.print(FD_Elecs[i].profCount()+"       ");
        }
        System.out.print("      ");
        for(int i=0; i<HD_Elecs.length; i++)
        {
            System.out.print(HD_Elecs[i].profCount()+"       ");
        }
        System.out.print("      \n");
    }
}