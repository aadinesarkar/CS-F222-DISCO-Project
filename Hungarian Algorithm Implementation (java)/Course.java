import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Course
{
    private String code;//eg. "CS F222"
    private Prof[] profs;//instructors assigned to the course
    private CourseTypes type;//fd/hd? cdc/elec?
    private int reqdProfs;

    /**
     * Instantiates a Course.
     * @param code course code (eg. "CS F222")
     * @param type a variable of enumerated type CourseTypes that indicates if the course is undergraduate or graduate, and compulsory or elective
     * Default capacity 2 instructors
     */
    public Course(String code, CourseTypes type)//constructor
    {
        this.code=code;
        this.type=type;
        this.profs=new Prof[0];
        this.reqdProfs=2;
    }

    /**
     * Instantiates a Course.
     * @param code course code (eg. "CS F222")
     * @param type a variable of enumerated type CourseTypes that indicates if the course is undergraduate or graduate, and compulsory or elective
     * @param reqdProfs number of instructors required for the course FOR FD CDCs ONLY.
     */
    public Course(String code, CourseTypes type, int reqdProfs)//constructor
    {
        this.code=code;
        this.type=type;
        this.profs=new Prof[0];
        this.reqdProfs=reqdProfs;
    }
    
    /**
     * @return the course code (eg. "CS F222")
     */
    public String getCode()
    {
        return code;
    }
    
    /**
     * @return the number of instructors the course has been assigned to
     */
    public int profCount()
    {
        return profs.length;
    }
    
    /**
     * @return true iff the course can be assigned to more instructors
     */
    public boolean isVacant()
    {
        return profs.length<reqdProfs;
    }
    
    /**
     * @return an array of instructors to whom the course has been assigned
     */
    public Prof[] getProfs()
    {
        return profs;
    }
    
    /**
     * @return variable of enumerated type CourseTypes indicating if the course is undergraduate or graduate, and compulsory or elective
     */
    public CourseTypes getType()
    {
        return type;
    }
    
    /**
     * @return number of instructors required to satisfy the course
     */
    public int getReqdProfs()
    {
        return reqdProfs;
    }

    /**
     * assigns this course to the specified instructor
     * @param prof instructor to whom course must be assigned
     * @return 0 if addition was successful, -1 otherwise
     */
    public int assignToProf(Prof prof)
    {
        if(profs.length<reqdProfs)
        {
            Prof[] temp = new Prof[profs.length+1];
            for(int i=0; i<profs.length; i++)
            {
                temp[i]=profs[i];
            }
            temp[profs.length]=prof;
            profs=temp;
            return 0;
        }
        return -1;
    }

    /**
     * Removes ONE instance of prof from course. If course assigned to same prof twice only one instance is removed.
     * @param prof instructor to be removed
     * @return 0 if successful, else -1
     */
    public int removeProf(Prof prof)
    {
        int index=-1;
        for(int i=0; i<profs.length; i++)
        {
            if(profs[i].equals(prof))
                index=i;
        }
        if(index==-1)
        {
            return -1;
        }
        Prof[] temp=new Prof[profs.length-1];
        for(int i=0; i<index; i++)
        {
            temp[i]=profs[i];
        }
        for(int i=index+1; i<profs.length; i++)
        {
            temp[i-1]=profs[i];
        }
        profs=temp;
        return 0;
    }
    
    /**
     * Useful for printing
     * @return a string containing the list of instructors to whom the course is assigned
     */
    public String getProfListString()
    {
        String string="[";
        for(Prof prof: profs)
        {
            string=string+prof.getName()+"      ";
        }
        string=string+"]";
        return string;
    }
    
    /**
     * @param prefList ordered array of preferred courses
     * @param courseList array of courses
     * @return array of Hungarian-friendly costs indexed as courseList
     */
    public static double[] weightList(Course[] prefList, Course[] courseList)//returns an array whose elements are the 'weights', i.e. index in prefList of corresponding CourseList element
    {
        double[] weightsList=new double[courseList.length];//array of weights for each course
        for(int i=0; i<courseList.length; i++)//for every course in courseList
        {
            weightsList[i]=10000.00;//weight is 10000 if course is not found in prefList
            for(int k=0; k<prefList.length; k++)
            {
                if(courseList[i].equals(prefList[k]))//but if course is found in prefList
                    weightsList[i]=(double)k;//weight is index in courseList
            }
        }
        return weightsList;
    }
    
    /**
     * @param courses an array of courses
     * @return the index of the calling course in the given array of courses
     */
    public int courseIndex(Course[] courses)
    {
        for(int i=0; i<courses.length; i++)
        {
            if(this.equals(courses[i]))
                return i;
        }
        return -1;
    }
    
    /**
     * @param profs an array of instructors each with their own preference lists
     * @param courses an ordered array of courses
     * @param type type of courses array. Please ensure correctness
     * @return a Hungarian-friendly cost matrix
     */
    public static double[][] weightMatrix(Prof[] profs, Course[] courses, CourseTypes type)
    {
            double[][] matrix=new double[profs.length][courses.length];
            for(int i=0; i<profs.length; i++)
            {
                matrix[i]=Course.weightList(profs[i].getPrefList(type), courses);
            }
            return matrix;
    }

    /**
     * Special overload to join both kinds of electives in a single matrix.
     * @param profs DO NOT USE
     * @param fdelecs DO NOT USE
     * @param hdelecs DO NOT USE
     * @return DO NOT USE
     */
    public static double[][] weightMatrix(Prof[] profs, Course[] fdelecs, Course[] hdelecs)
    {
            double[][] matrix=new double[profs.length][fdelecs.length+hdelecs.length];
            for(int i=0; i<profs.length; i++)
            {
                double[] temp=new double[fdelecs.length+hdelecs.length];
                double[] fd=Course.weightList(profs[i].getPrefList(CourseTypes.FD_Elec), fdelecs);//length fdelecs.length
                double[] hd=Course.weightList(profs[i].getPrefList(CourseTypes.HD_Elec), hdelecs);//length hdelecs.length
                for(int j=0; j<fdelecs.length; j++)
                {
                    temp[j]=fd[j];
                }
                for(int j=fdelecs.length; j<fdelecs.length+hdelecs.length; j++)
                {
                    temp[j]=hd[j-fdelecs.length];
                }
                matrix[i]=temp;
            }
            return matrix;
    }

    /**
     * Searches for a course in an array of courses by name.
     * @param code the code of the course to be searched for
     * @param courses the array of courses to be searched in
     * @return true iff found
     */
    public static boolean isInCourseList(String code, Course[] courses)
    {
        for(Course course: courses)
        {
            if(course.getCode().equals(code))
                return true;
        }
        return false;
    }

    /**
     * Searches for a course in an array of courses.
     * @param key the course to be searched for
     * @param courses the array of courses to be searched in
     * @return true iff found
     */
    public static boolean isInCourseList(Course key, Course[] courses)
    {
        for(Course course: courses)
        {
            if(course.equals(key))
                return true;
        }
        return false;
    }

    /**
     * to be used with isInCourseList()
     * @param code coursecode of search key
     * @param courses array of courses
     * @return the array element, if found. An arbitrary CS F000 if not - UNSAFE, CHECK USING isInCourseList() first
     */
    public static Course findInCourseList(String code, Course[] courses)
    {
        for(Course course: courses)
        {
            if(course.getCode().equals(code))
                return course;
        }
        return new Course("CS F000", CourseTypes.FD_CDC);
    }

    /**
     * to be used with isInCourseList()
     * @param code search key
     * @param courses array of courses
     * @return the array element, if found. An arbitrary CS F000 if not - UNSAFE, CHECK USING isInCourseList() first
     */
    public static Course findInCourseList(Course key, Course[] courses)
    {
        for(Course course: courses)
        {
            if(course.equals(key))
                return course;
        }
        return new Course("CS F000", CourseTypes.FD_CDC);
    }

    /**
     * Reads a list of courses into an array from the specified comma-separated-value file.
     * Each line must follow the below format with no whitespaces immediately before or after commas.
     * Type should be one of the following(note punctuation): FD_CDC HD_CDC FD_Elec HD_Elec
     * Course code,Type,Required Profs
     * @param filename name of the .csv file to read from
     * @param type type of course to read
     * @return an array of courses
     */
    public static Course[] readCourseFile(String filename, CourseTypes type)
    {
        ArrayList<Course> courseList = new ArrayList<>();
        String line = "";  
        String splitBy = ",";
        try   
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));  
            br.readLine();//header column
            while ((line = br.readLine()) != null)   //returns a Boolean value  
            {  
                String[] fields = line.split(splitBy, 3);    // use comma as separator  
                if(!fields[0].equals("") && !fields[1].equals("") && !fields[2].equals(""))
                {
                    if(fields[1].equals(type.toString()))
                    {
                        courseList.add(new Course(fields[0], type, 2*Integer.parseInt(fields[2])));
                    }
                }
            }
            br.close();
        }
        catch (IOException e)   
        {  
            e.printStackTrace();
        }

        Course[] courses = new Course[courseList.size()];
        for(int i=0; i<courseList.size(); i++)
        {
            courses[i]=courseList.get(i);
        }

        //TO CHECK IF COURSES HAVE BEEN INITIALISED CORRECTLY
        // for(Course course: courses)
        // {
        //     System.out.println(course+"  reqprofs="+course.getReqdProfs());
        // }

        return courses;
    }

    public String toString()
    {
        return getCode()+" "+reqdProfs;//remove reqdProfs
    }
    public boolean equals(Object other)
    {
        return (other instanceof Course)&&this.getCode().equals(Course.class.cast(other).getCode())&&this.getType().equals(Course.class.cast(other).getType());
    }
}